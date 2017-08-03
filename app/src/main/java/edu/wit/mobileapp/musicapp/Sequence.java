package edu.wit.mobileapp.musicapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by cannistrarod on 8/2/2017.
 */

public class Sequence implements Runnable{

    //array of  64 arraylists of mediaplayer sounds to play at the given step
    private ArrayList<MediaPlayer>[] sounds;
    private int bpm;
    private Thread seq;
    private int numOfMeasures = 4;
    private double tick;
    private double currentTime = 0.0;
    private double maxTime;
    private boolean isPlaying = false;

    public Sequence(){
        this.setBpm(120);
        init();
    }

    public Sequence(int bpm){
        this.setBpm(bpm);
        init();
    }

    public Sequence(int measures, int bpm){
        numOfMeasures = measures;
        this.setBpm(bpm);
        init();
    }

    public void setBpm(int x){
        this.pause();

        bpm = x;
        tick = 250.0*60.0/bpm;
        maxTime = tick * numOfMeasures * 16;
    }

    private void init(){
        sounds = (ArrayList<MediaPlayer>[])new ArrayList[numOfMeasures*16];
        for (int i=0;i<sounds.length;i++){
            sounds[i] = new ArrayList<MediaPlayer>();
        }
    }

    //adds a mediaplayer sound object that will play at step t (Ex: if t = 4, the sound will play on the 2nd beat)
    public void addSound(MediaPlayer x, int t){
        this.pause();

        sounds[t].add(x);
    }

    //multiple sounds at one given time
    public void addSounds(MediaPlayer[] x, int t){
        this.pause();
        for (MediaPlayer m : x) {
            this.addSound(m, t);
        }
    }

    public void prepare(){
        for (int i=0;i<sounds.length;i++){
            for (MediaPlayer m : sounds[i]) {
                m.prepareAsync();
            }
        }
    }

    public void removeSound(MediaPlayer x, int t){
        this.pause();
        sounds[t].remove(x);
    }

    public void clearSequence(){
        this.pause();
        for (int i=0;i<sounds.length;i++){
            if (sounds[i] != null){
                sounds[i].clear();
            }
        }
    }

    public void play(){
        if (isPlaying){
            return;
        }else{
            if(seq != null && seq.isAlive()){
                try{
                    seq.join();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            isPlaying = true;
            seq = new Thread(this);
            seq.start();
        }
    }

    private void playStep(){

        int x = (int) Math.floor(currentTime/tick);
        for (MediaPlayer m : sounds[x]) {
            if(m.isPlaying()){
                m.pause();
            }
            m.seekTo(0);
            m.start();
        }

        //update currentTime
        currentTime += tick;
        if (currentTime >= maxTime){
            currentTime = 0.0;
        }
    }

    //not actually pause, actually stop
    public void pause(){
        if (!isPlaying){
            return;
        }else{
            isPlaying = false;
            for (int i=0;i<sounds.length;i++){
                for (MediaPlayer sound : sounds[i]) {
                    if (sound.isPlaying()){
                        sound.pause();
                    }
                }
            }
            currentTime = 0.0;
            //kill thread
            //thread will be killed by isPlaying flag
        }
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    @Override
    public void run() {
        while(isPlaying){
            playStep();
            int milli = (int) Math.floor(tick);
            int nano = (int) Math.floor((tick-milli) * 1000000);
            try {
                Thread.sleep(milli, nano);
            }catch (Exception e){
                Log.v("app", "thread interrupted!");
                //yolo
            }
        }

    }
}
