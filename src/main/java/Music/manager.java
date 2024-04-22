package Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class Manager {

    public final AudioPlayer audioPlayer;
    public final Scheduler trackScheduler;
    private final Handler audioHandler;

    public Manager(AudioPlayerManager manager) {

        this.audioPlayer = manager.createPlayer();
        this.trackScheduler = new Scheduler(this.audioPlayer);
        this.audioPlayer.addListener(this.trackScheduler);
        this.audioHandler = new Handler(this.audioPlayer);

    }

    public Handler getAudioHandler() {

        return this.audioHandler;

    }

}
