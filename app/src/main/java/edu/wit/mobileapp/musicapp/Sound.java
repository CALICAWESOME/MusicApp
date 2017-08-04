package edu.wit.mobileapp.musicapp;

import android.media.MediaPlayer;

class Sound implements Runnable {

    private MediaPlayer sound;

    Sound(MediaPlayer sound) {
        this.sound = sound;
        this.sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();
            }
        });
    }

    @Override
    public void run() {
        sound.seekTo(0);
        sound.start();
    }

}
