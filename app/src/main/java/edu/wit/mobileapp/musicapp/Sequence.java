package edu.wit.mobileapp.musicapp;

import android.media.MediaPlayer;

import java.util.ArrayList;

class Sequence {

    private static final int NOTES_PER_SEQUENCE = 16;

    ArrayList<ArrayList<Sound>> sounds = new ArrayList<>();

    Sequence() {
        for (int i = 0; i < NOTES_PER_SEQUENCE; i++)
            // fill sounds with empty arrayLists
            sounds.add(new ArrayList<Sound>());
    }

    /**
     * adds sound at this.sounds[index]
     */
    void addSound(MediaPlayer sound, int index) {
        sounds.get(index).add(new Sound(sound));
    }
}
