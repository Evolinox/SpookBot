package Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class manager {

    public final AudioPlayer audioPlayer;
    public final scheduler trackScheduler;
    private final handler audioHandler;

    public manager(AudioPlayerManager manager) {

        this.audioPlayer = manager.createPlayer();
        this.trackScheduler = new scheduler(this.audioPlayer);
        this.audioPlayer.addListener(this.trackScheduler);
        this.audioHandler = new handler(this.audioPlayer);

    }

    public handler getAudioHandler() {

        return this.audioHandler;

    }

}
