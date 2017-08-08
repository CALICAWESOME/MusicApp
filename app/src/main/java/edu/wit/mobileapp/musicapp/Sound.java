package edu.wit.mobileapp.musicapp;

import android.media.MediaPlayer;
import android.util.Log;

class Sound {

    private MediaPlayer sound;

    Sound(MediaPlayer sound) {
        this.sound = sound;
        this.sound.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.v("app", mp.toString());
            }
        });
        this.sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.pause();
            }
        });
    }

    public void start() {
        if(sound.isPlaying()){
            sound.seekTo(0);
        }
        sound.start();
    }

    public void release(){
        sound.release();
    }

    public void pause(){
        if (sound.isPlaying()) {
            sound.pause();
            sound.seekTo(0);
        }
    }
}
