package Commands;

import Music.player;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.*;

import java.net.URI;
import java.net.URISyntaxException;

public class music extends ListenerAdapter {

    public void onMessageReceived (MessageReceivedEvent event) {

        if (event.getMessage().getContentStripped().startsWith("s!play")) {

            if (!event.getMember().getVoiceState().inAudioChannel()) {

                event.getTextChannel().sendMessage("You need to be in a voice channel for this command to work!").queue();

            }

            if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

                final AudioManager audioManager = event.getGuild().getAudioManager();
                final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

                audioManager.openAudioConnection(memberChannel);

            }

            String link = event.getMessage().getContentStripped().substring(7);

            if(!isUrl(link)) {
                link = "ytsearch:" + link + " audio";
            }

            player.getINSTANCE().loadAndPlay(event.getTextChannel(), link);

        }

        if (event.getMessage().getContentStripped().startsWith("s!stop")) {

            if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

                final AudioManager audioManager = event.getGuild().getAudioManager();

                audioManager.closeAudioConnection();

            }

        }

    }

    public boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

}
