package edu.wit.mobileapp.musicapp;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;

/**
 * Created by cannistrarod on 8/2/2017.
 */

public class Sequence {

    //array of  64 arraylists of mediaplayer sounds to play at the given step
    private ArrayList<MediaPlayer>[] sounds;
    private int bpm;
    private float tick;
    private float currentTime = 0;
    private boolean isPlaying = false;

    public Sequence(){
        bpm = 120;
        tick = (float) 250.0*60/bpm;
        sounds = (ArrayList<MediaPlayer>[])new ArrayList[64];
    }

    public Sequence(int bpm){
        this.setBpm(bpm);
        sounds = (ArrayList<MediaPlayer>[])new ArrayList[64];
    }

    public void setBpm(int x){
        if (isPlaying){
            this.pause();
        }
        bpm = x;
        tick = 250f*60f/bpm;
    }

    //adds a mediaplayer sound object that will play at step t (Ex: if t = 4, the sound will play on the 2nd beat)
    public void addSound(MediaPlayer x, int t){
        sounds[t].add(x);
    }

    //multiple sounds at one given time
    public void addSounds(MediaPlayer[] x, int t){
        for (MediaPlayer m : x) {
            this.addSound(m, t);
        }
    }

    public void play(){
        if (isPlaying){
            return;
        }else{
            isPlaying = true;
            Thread seq = new Thread();
            //TODO
            //start thread for sequence
            //
        }
    }

    //not actually, actually stop
    public void pause(){
        if (!isPlaying){
            return;
        }else{
            for (int i=0;i<sounds.length;i++){
                for (MediaPlayer sound : sounds[i]) {
                    if (sound.isPlaying()){
                        sound.stop();
                    }
                }
            }
            //kill thread
            isPlaying = false;
        }
    }
}
