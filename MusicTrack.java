package de.tum.cit.ase.bomberquest.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * This enum is used to manage the music tracks in the game.
 * Currently, only one track is used, but this could be extended to include multiple tracks.
 * Using an enum for this purpose is a good practice, as it allows for easy management of the music tracks
 * and prevents the same track from being loaded into memory multiple times.
 * See the assets/audio folder for the actual music files.
 * Feel free to add your own music tracks and use them in the game!
 */
public enum MusicTrack {

    BACKGROUND("background.mp3", 0.2f, true),
    BACKGROUND_MENU("background_menu.mp3", 0.2f, true),
    WIN("win.mp3", 0.2f),
    LOSE("lose.mp3", 0.2f),
    EXPLODE("explode.mp3", 0.2f),
    DROP("drop.mp3", 0.2f),
    COLLECT("collect.mp3", 0.2f);

    /** The music file owned by this variant. */
    private final Music music;

    //Constructor for music loop.
    MusicTrack(String fileName, float volume, boolean looping) {
        this.music = Gdx.audio.newMusic(Gdx.files.internal("audio/" + fileName));
        this.music.setLooping(looping);
        this.music.setVolume(volume);
    }

    //Constructor for music doesn't loop. （Method Overloading)
    MusicTrack(String fileName, float volume ) {
        this.music = Gdx.audio.newMusic(Gdx.files.internal("audio/" + fileName));
        this.music.setLooping(false);
        this.music.setVolume(volume);
    }

    /**
     * Play this music track.
     * This will not stop other music from playing - if you add more tracks, you will have to handle that yourself.
     */
    public void play() {
        this.music.play();
    }

    public void stop(){
        this.music.stop();
    }
}
