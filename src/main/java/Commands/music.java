package Commands;

import Music.manager;
import Music.player;
import SpookBot.main;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class music extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "SpookBotSettings";
        File file = new File(path + File.separator + "config.xml");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (Exception d) {
            d.printStackTrace();
        }

        try {
            document = documentBuilder.parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String currentPrefix = document.getElementsByTagName("commandPrefix").item(0).getTextContent();
        String activity = document.getElementsByTagName("botActivity").item(0).getTextContent();

        if (event.getName().equals("play")) {

            if (!event.getMember().getVoiceState().inAudioChannel()) {

                event.getTextChannel().sendMessage("You need to be in a voice channel for this command to work!").queue();
                main.spookOS.writeToConsole(event.getMember().getEffectiveName() + " tried to play something, but was not connected to a Voicechat!");

            }

            if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

                final AudioManager audioManager = event.getGuild().getAudioManager();
                final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

                audioManager.openAudioConnection(memberChannel);
                main.spookOS.writeToConsole("Connecting to " + memberChannel);

            }

            String link = event.getOption("name").getAsString();

            if(!isUrl(link)) {
                link = "ytsearch:" + link + " audio";
            }

            player.getINSTANCE().loadAndPlay(event.getTextChannel(), link);

            main.spookOS.writeToConsole("Playing " + link);

        }

        if (event.getName().equals("stop")) {

            if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

                final AudioManager audioManager = event.getGuild().getAudioManager();

                audioManager.closeAudioConnection();

                main.spookOS.writeToConsole(event.getMember().getNickname() + " has stopped Audio Playback");
                main.spookOS.writeToConsole("Disconnecting from Voicechannel...");
                main.setActivity(activity);

            }

        }

        if (event.getName().equals("next")) {

            if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

                //here logic for next song from playlist
                final manager musicManager = Music.player.getINSTANCE().getMusicManager(event.getGuild());

                musicManager.trackScheduler.nextTrack();

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
