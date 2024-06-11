package Music;

import SpookBot.Main;
import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.*;

public class Player {

    private static Player INSTANCE;
    private final Map<Long, Manager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public Player() {

        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);

    }

    public Manager getMusicManager(Guild guild) {

        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final Manager guildManager = new Manager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildManager.getAudioHandler());
            return guildManager;
        });

    }

    public void loadAndPlay(TextChannel textChannel, String trackUrl) {

        final Manager musicManager = this.getMusicManager(textChannel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {

                musicManager.trackScheduler.queue(audioTrack);

                textChannel.sendMessage("Adding **`" + audioTrack.getInfo().title + "`** to queue, by **`" + audioTrack.getInfo().author + "`**").queue();

            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                final List<AudioTrack> tracks = audioPlaylist.getTracks();

                if (!tracks.isEmpty()) {

                    musicManager.trackScheduler.queue(tracks.get(0));

                    textChannel.sendMessage("Adding **`" + tracks.get(0).getInfo().title + "`** to queue, by **`" + tracks.get(0).getInfo().author + "`**").queue();

                }

            }

            @Override
            public void noMatches() {
                Main.loggingService.severe("No Matches Found");
            }

            @Override
            public void loadFailed(FriendlyException e) {
                Main.loggingService.severe("Loading Track failed: " + e.getMessage());
            }
        });

    }

    public static Player getINSTANCE() {

        if(INSTANCE == null) {

            INSTANCE = new Player();

        }

        return INSTANCE;

    }

}
