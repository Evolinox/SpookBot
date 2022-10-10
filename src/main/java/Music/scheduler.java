package Music;

import SpookBot.main;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class scheduler extends AudioEventAdapter {

    public final AudioPlayer audioPlayer;
    public final BlockingQueue<AudioTrack> queue;

    public scheduler(AudioPlayer audioPlayer) {

        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingQueue<>();

    }

    public void queue(AudioTrack track) {

        if (!this.audioPlayer.startTrack(track, true)) {

            this.queue.offer(track);

        }

        main.setActivity(this.audioPlayer.getPlayingTrack().getInfo().title + ", by " + this.audioPlayer.getPlayingTrack().getInfo().author);

    }

    public void nextTrack() {

        this.audioPlayer.startTrack(this.queue.poll(), false);

        main.setActivity(this.audioPlayer.getPlayingTrack().getInfo().title + ", by " + this.audioPlayer.getPlayingTrack().getInfo().author);

    }

    public void stopPlayback() {

        this.audioPlayer.stopTrack();

    }

    @Override
    public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack track, AudioTrackEndReason endReason) {

        //get config xml block
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

        String activity = document.getElementsByTagName("botActivity").item(0).getTextContent();
        //end config xml block

        //normal op
        if (endReason.mayStartNext) {

            nextTrack();

            //dynamic activity op
            main.setActivity(activity);

        }

    }

}
