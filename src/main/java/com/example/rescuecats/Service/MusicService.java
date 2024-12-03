package com.example.rescuecats.Service;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicService {

    private MediaPlayer mediaPlayer;

    public MusicService() {
        playBackgroundMusic();
    }

    public void playBackgroundMusic() {
        try {
            // Path to the music file
            String musicFile = getClass().getResource("/music/background.mp3").toExternalForm();
            Media media = new Media(musicFile);

            // Create MediaPlayer and play the music
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
            mediaPlayer.setVolume(0.5); // Adjust volume (0.0 to 1.0)
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }


}
