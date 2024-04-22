package Music;

import SpookBot.Main;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler extends AudioEventAdapter {

    public final AudioPlayer audioPlayer;
    public final BlockingQueue<AudioTrack> queue;

    public Scheduler(AudioPlayer audioPlayer) {

        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingQueue<>();

    }

    public void queue(AudioTrack track) {

        if (!this.audioPlayer.startTrack(track, true)) {

            this.queue.offer(track);

        }

        Main.setActivity(true, this.audioPlayer.getPlayingTrack().getInfo().title + ", by " + this.audioPlayer.getPlayingTrack().getInfo().author);

    }

    public String getTitleSong() {
        return this.audioPlayer.getPlayingTrack().getInfo().title;
    }

    public String getAuthorSong() {
        return this.audioPlayer.getPlayingTrack().getInfo().author;
    }

    public void nextTrack() {

        this.audioPlayer.startTrack(this.queue.poll(), false);

        Main.setActivity(true, this.audioPlayer.getPlayingTrack().getInfo().title + ", by " + this.audioPlayer.getPlayingTrack().getInfo().author);

    }

    public void stopPlayback() {

        this.audioPlayer.stopTrack();

    }

    @Override
    public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack track, AudioTrackEndReason endReason) {

        Main.setActivity(false, "");

        //normal op
        if (endReason.mayStartNext) {

            nextTrack();

        }

    }

}
