package edu.wit.mobileapp.musicapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DAW extends AppCompatActivity {

    Node root = new Node();
    final int numChords = 4;

    Theory.note key;    // ex: A
    Theory.type degree; // ex: minor
    // TODO: sync this with DAW buttons (see line 45)
    int prog[] = new int[numChords];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daw);
        fillTrie();

        Button chord1 = (Button) findViewById(R.id.chord1);
        chord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chordSelectorDialog();
            }
        });
        prog[0] = 1;
        prog[1] = 5;
        int suggz[] = getSugg(2); // beef!!!

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMinValue(40);
        numberPicker.setMaxValue(208);
        numberPicker.setValue(120); // default

        NumberPicker keyPicker = (NumberPicker) findViewById(R.id.keyPicker);
        String[] keys = {"C", "D♭", "D", "E♭", "E", "F", "G♭", "G", "A♭", "A", "B♭", "B"};
        keyPicker.setMaxValue(0);
        keyPicker.setMaxValue(keys.length-1);
        keyPicker.setDisplayedValues(keys);
    }

    // TODO: make this work for selecting chords and getting suggestions
    private void chordSelectorDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage("HEY");
        adb.setPositiveButton("yuh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("yuh", String.valueOf(which));
            }
        });
        adb.setNegativeButton("nuh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("nuh", String.valueOf(which));
            }
        });
        adb.create().show();
    }

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

        ArrayList<Integer> ret = new ArrayList<>();
        for (int j = 0; j < curr.next.length; j++)
            if (curr.next[j]!= null)
                ret.add(j);

        int realret[] = new int[ret.size()];
        for (int j = 0; j < ret.size(); j++)
            realret[j] = ret.get(j)+1;

        return realret;
    }
}
