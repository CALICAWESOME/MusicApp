package edu.wit.mobileapp.musicapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TabHost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class DAW extends AppCompatActivity {

    Node root = new Node();
    final int numChords = 4;
    boolean playing = false;

    Theory.note key = Theory.note.Eb;
    Theory.type degree = Theory.type.major;
    // TODO: sync this with DAW buttons (see line 45)
    int prog[] = new int[numChords];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daw);
        Toolbar toolbar = (Toolbar) findViewById(R.id.bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Write");
        spec.setContent(R.id.tab1);
        spec.setIndicator("WRITE"); 
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Edit");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Edit");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Play");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Play");
        host.addTab(spec);

        host.setCurrentTab(1);

        // fill trie with data from prog.txt
        fillTrie();

        Button chord1 = (Button) findViewById(R.id.chord1);
        chord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chordSelectorDialog(1);
            }
        });

        Button chord2 = (Button) findViewById(R.id.chord2);
        chord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chordSelectorDialog(2);
            }
        });

        Button chord3 = (Button) findViewById(R.id.chord3);
        chord3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chordSelectorDialog(3);
            }
        });

        Button chord4 = (Button) findViewById(R.id.chord4);
        chord4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chordSelectorDialog(4);
            }
        });

        Random rand = new Random();
        prog[0] = 1;
        int boi[] = getSugg(1);
        prog[1] = boi[rand.nextInt(boi.length)];
        boi = getSugg(2);
        prog[2] = boi[rand.nextInt(boi.length)];
        boi = getSugg(3);
        prog[3] = boi[rand.nextInt(boi.length)];

        for (int i : prog) {
            Log.v("HEY", Theory.num2Chord(i, key).toString());
        }

        final ImageView playhead = (ImageView) findViewById(R.id.playhead);
        playhead.setVisibility(View.INVISIBLE);
        final TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 1.0f,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(8000);
        animation.setInterpolator(new LinearInterpolator());
        final ImageView playButton = (ImageView) findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playing){
                    playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    playing = false;
                    playhead.setVisibility(View.INVISIBLE);
                    playhead.clearAnimation();
                }
                else{
                    playButton.setImageResource(R.drawable.ic_pause_black_24dp);
                    playing = true;
                    playhead.setVisibility(View.VISIBLE);
                    playhead.startAnimation(animation);
                }
            }
        });

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMinValue(70);
        numberPicker.setMaxValue(200);
        numberPicker.setValue(120); // default
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                if(playing){
                    playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    playing = false;
                    playhead.setVisibility(View.INVISIBLE);
                    playhead.clearAnimation();
                }
                animation.setDuration(16000*60/i2);
            }
        });

        NumberPicker keyPicker = (NumberPicker) findViewById(R.id.keyPicker);
        String[] keys = {"C", "G", "D", "A", "E", "B", "F♯", "C♯", "C♭", "G♭", "D♭", "A♭", "E♭", "B♭", "F"};
        keyPicker.setMinValue(0);
        keyPicker.setMaxValue(keys.length-1);
        keyPicker.setDisplayedValues(keys);
    }

    // TODO: make this work for selecting chords and getting suggestions
    private void chordSelectorDialog(int chordNum) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.chord_picker);
        TextView t = (TextView) dialog.findViewById(R.id.textView2);
        String theBoy = "CHORD " + chordNum;
        t.setText(theBoy);

        // root picker
        final NumberPicker rootPick = (NumberPicker) dialog.findViewById(R.id.chordRootPicker);
        final String[] notez = new String[Theory.note.values().length];
        for (int i = 0; i < notez.length; i++)
            notez[i] = Theory.note.values()[i].name();
        rootPick.setMinValue(0);
        rootPick.setMaxValue(notez.length-1);
        rootPick.setDisplayedValues(notez);

        // type picker
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.chordTypePicker);
        String[] types = {"major", "minor", "diminished"};
        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, types));

        Button done = (Button) dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rootIndex = rootPick.getValue();
                int typeIndex = spinner.getSelectedItemPosition();
                Theory.chord theBoi = new Theory.chord(
                        Theory.note.values()[rootIndex],
                        Theory.type.values()[typeIndex]);
                // set current chord in prog
                // translate to number??!?!??
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //////////////////////////////////
    // AFTER HERE IS ALL TRIE STUFF //
    //////////////////////////////////

    private class Node {
        private boolean end;
        private Node[] next;
        Node() {
            end = false;
            next = new Node[7];
        }
    }

    private void fillTrie() {
        try {
            InputStream is = getAssets().open("prog.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String l;
            while ((l = reader.readLine()) != null) {
                String[] ll = l.split(" ");
                int[] line = new int[numChords];
                for (int i = 0; i < numChords; i++) {
                    line[i] = Integer.parseInt(ll[i]);
                }
                insertRecursive(root, line, 0);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertRecursive(Node curr, int[] line, int i) {
        // base case, empty string means parent is the last letter in a word
        if (i == line.length) {
            curr.end = true;
            return;
        }
        int j = line[i]-1; // j is the index of the interval, not the interval itself.
        // if next[j] is null, put a node there
        if (curr.next[j] == null) {
            curr.next[j] = new Node();
        }
        // do the deed
        insertRecursive(curr.next[j], line, i+1);
    }

    /**
     * getSugg gets suggestions for the next chord to come in the progression. Suggestions are made
     * for chord number chordNum in the progression.
     * @param chordNum get suggestions for the chord to come after this chord in the progression
     * @return an array of suggested chords, each number is the root of the chord relative to the
     * tonic of the key
     */
    private int[] getSugg(int chordNum) {
        return getSuggRecursive(root, chordNum, 0);
    }

    private int[] getSuggRecursive(Node curr, int chordNum, int i) {
        if (curr == null) {
            return new int[0];
        }
        if (i < chordNum)
            return getSuggRecursive(curr.next[prog[i]-1], chordNum, i+1);

        // ret will be converted into an array of ints
        ArrayList<Integer> ret = new ArrayList<>();
        // for each chord that would come next
        for (int j = 0; j < curr.next.length; j++)
            if (curr.next[j]!= null)
                ret.add(j);

        // becausee Java can't implicitly convert integer objects to int type
        int realret[] = new int[ret.size()];
        // fill realRet manually
        for (int j = 0; j < ret.size(); j++)
            realret[j] = ret.get(j)+1;

        return realret;
    }
}
