package Commands;

import Music.manager;
import Music.player;
import SpookBot.main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class music extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        // Erster Part um die config.xml zu laden
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

        // Get Activity von der config
        String activity = document.getElementsByTagName("botActivity").item(0).getTextContent();

        // Schauen, ob überhaupt das play event getriggered wurde
        if (event.getName().equals("play")) {

            // Wenn der Discord Nutzer nicht in nem VC ist...
            if (!event.getMember().getVoiceState().inAudioChannel()) {

                // Antworte mit ner Nachricht, das er net in nem VC ist
                event.reply("You need to be in a voice channel for this command to work!").queue();
                // Info an die Konsole
                main.spookOS.writeToConsole(event.getMember().getEffectiveName() + " tried to play something, but was not connected to a Voicechat!");

            }

            //
            if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

                final AudioManager audioManager = event.getGuild().getAudioManager();
                final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

                audioManager.openAudioConnection(memberChannel);
                //main.spookOS.writeToConsole("Connecting to " + memberChannel);

            }

            String link = event.getOption("link").getAsString();

            if(!isUrl(link)) {
                link = "ytsearch:" + link + " audio";
            }

            player.getINSTANCE().loadAndPlay(event.getChannel().asTextChannel(), link);

            event.deferReply().queue();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            event.getHook().editOriginal("Sure! I've searched on YouTube for: " + link).queue();

            //main.spookOS.writeToConsole("Playing " + link);

        }

        if (event.getName().equals("stop")) {

            if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

                final AudioManager audioManager = event.getGuild().getAudioManager();

                audioManager.closeAudioConnection();

                event.reply("I'm leaving now. Bye, have a great Time!").queue();

                main.spookOS.writeToConsole(event.getMember().getNickname() + " has stopped Audio Playback");
                main.spookOS.writeToConsole("Disconnecting from Voicechannel...");
                main.setActivity(true, activity);

            }

        }

        if (event.getName().equals("next")) {

            if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

                //here logic for next song from playlist
                final manager musicManager = player.getINSTANCE().getMusicManager(event.getGuild());

                String titleSong = "scheduler.getTitleSong()";
                String authorSong = "scheduler.getAuthorSong()";

                event.reply("Now playing: " + titleSong + " by " + authorSong).queue();

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
