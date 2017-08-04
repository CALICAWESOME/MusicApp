package edu.wit.mobileapp.musicapp;

import android.media.MediaPlayer;

import java.util.ArrayList;

class Sequence {

    private static final int NOTES_PER_SEQUENCE = 8;

    ArrayList<ArrayList<MediaPlayer>> sounds = new ArrayList<>();

    Sequence() {
        for (int i = 0; i < NOTES_PER_SEQUENCE; i++)
            // fill sounds with empty arrayLists
            sounds.add(new ArrayList<MediaPlayer>());
    }

    /**
     * adds sound at this.sounds[index]
     */
    void addSound(MediaPlayer sound, int index) {
        sounds.get(index).add(sound);
    }

    private void playStep(int x) {
        for (MediaPlayer m : sounds.get(x))  {
            if(m.isPlaying()) {
                m.pause();
            }
            m.seekTo(0);
            m.start();
        }
    }
}
