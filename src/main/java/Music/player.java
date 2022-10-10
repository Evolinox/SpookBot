package Music;

import SpookBot.main;
import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;

import java.util.*;

public class player {

    private static player INSTANCE;
    private final Map<Long, manager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public player() {

        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);

    }

    public manager getMusicManager(Guild guild) {

        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final manager guildManager = new manager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildManager.getAudioHandler());
            return guildManager;
        });

    }

    public void loadAndPlay(TextChannel textChannel, String trackUrl) {

        final manager musicManager = this.getMusicManager(textChannel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {

                musicManager.trackScheduler.queue(audioTrack);

                textChannel.sendMessage("Adding **`")
                        .append(audioTrack.getInfo().title)
                        .append("`** to queue, by **`")
                        .append(audioTrack.getInfo().author)
                        .append("`**")
                        .queue();

            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                final List<AudioTrack> tracks = audioPlaylist.getTracks();

                if (!tracks.isEmpty()) {

                    musicManager.trackScheduler.queue(tracks.get(0));

                    textChannel.sendMessage("Adding **`")
                            .append(tracks.get(0).getInfo().title)
                            .append("`** to queue, by **`")
                            .append(tracks.get(0).getInfo().author)
                            .append("`**")
                            .queue();

                }

            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });

    }

    public static player getINSTANCE() {

        if(INSTANCE == null) {

            INSTANCE = new player();

        }

        return INSTANCE;

    }

}
