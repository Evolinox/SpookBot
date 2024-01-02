package Music;

import SpookBot.main;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

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

        main.setActivity(true, this.audioPlayer.getPlayingTrack().getInfo().title + ", by " + this.audioPlayer.getPlayingTrack().getInfo().author);

    }

    public void nextTrack() {

        this.audioPlayer.startTrack(this.queue.poll(), false);

        main.setActivity(true, this.audioPlayer.getPlayingTrack().getInfo().title + ", by " + this.audioPlayer.getPlayingTrack().getInfo().author);

    }

    public void stopPlayback() {

        this.audioPlayer.stopTrack();

    }

    @Override
    public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack track, AudioTrackEndReason endReason) {

        main.setActivity(false, "");

        //normal op
        if (endReason.mayStartNext) {

            nextTrack();

        }

    }

}
