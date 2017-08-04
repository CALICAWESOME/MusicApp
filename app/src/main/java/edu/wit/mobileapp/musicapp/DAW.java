package edu.wit.mobileapp.musicapp;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TabHost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class DAW extends AppCompatActivity {

    Node root = new Node();
    final int numChords = 4;
    boolean playing = false;
    private int bpm = 120;
    Sequence[] drumTrack = new Sequence[4];
    Sequence[] pianoTrack = new Sequence[4];
    Timer timer;

    // TODO: hey Jared I bet this was really HARD to CODE
    // TODO: HAHEHEHAHEUAHEUHEUHEUHEUHUEH
    TextView chord1Name;
    TextView chord1Notes;
    TextView chord2Name;
    TextView chord2Notes;
    TextView chord3Name;
    TextView chord3Notes;
    TextView chord4Name;
    TextView chord4Notes;

    ArrayList<Sequence> tracks = new ArrayList<>();

    TextView[] chordNames;
    TextView[] chordNotes;
    Button[] progButtons;

    ImageView c1;
    ImageView db1;
    ImageView d1;
    ImageView eb1;
    ImageView e1;
    ImageView f1;
    ImageView gb1;
    ImageView g1;
    ImageView ab1;
    ImageView a1;
    ImageView bb1;
    ImageView b1;

    ImageView[] piano1;

    ImageView c2;
    ImageView db2;
    ImageView d2;
    ImageView eb2;
    ImageView e2;
    ImageView f2;
    ImageView gb2;
    ImageView g2;
    ImageView ab2;
    ImageView a2;
    ImageView bb2;
    ImageView b2;

    ImageView[] piano2;

    ImageView c3;
    ImageView db3;
    ImageView d3;
    ImageView eb3;
    ImageView e3;
    ImageView f3;
    ImageView gb3;
    ImageView g3;
    ImageView ab3;
    ImageView a3;
    ImageView bb3;
    ImageView b3;

    ImageView[] piano3;

    ImageView c4;
    ImageView db4;
    ImageView d4;
    ImageView eb4;
    ImageView e4;
    ImageView f4;
    ImageView gb4;
    ImageView g4;
    ImageView ab4;
    ImageView a4;
    ImageView bb4;
    ImageView b4;

    ImageView[] piano4;

    ImageView[][] pianos;

    private void resetPiano(int chordNum) {

        for(ImageView key: pianos[chordNum]){
            key.setImageResource(R.drawable.ic_rectangle);
        }

        pianos[chordNum][1].setImageResource(R.drawable.ic_black_key);
        pianos[chordNum][3].setImageResource(R.drawable.ic_black_key);
        pianos[chordNum][6].setImageResource(R.drawable.ic_black_key);
        pianos[chordNum][8].setImageResource(R.drawable.ic_black_key);
        pianos[chordNum][10].setImageResource(R.drawable.ic_black_key);
    }

    Theory.note key = Theory.note.C;
    Theory.type degree = Theory.type.major;
    ProgElement prog[] = new ProgElement[4];

    /**
     * A nice function to manage changing everything else when we want to edit the chord progression
     * @param i: index of the chord we're changing in prog
     * @param element: ProgElement to replace prog[i] with
     */
    void updateProgAt(int i, ProgElement element) {
        if (playing)
            stopPlaying(findViewById(R.id.play));
        // change prog[i]
        prog[i] = element;
        Theory.chord newChord = prog[i].getChord();
        // update buttons & other things
        progButtons[i].setText(newChord.toString());
        chordNames[i].setText(newChord.toString());
        chordNotes[i].setText(newChord.getNotesString());
        resetPiano(i);
        for(Theory.note n : prog[i].getChord().getNotes()){
            pianos[i][n.getVal()].setImageResource(R.drawable.ic_key_selected);
        }
    }

    private class ProgElement {
        int scaleStep;
        private Theory.chord chord;
        // constructor
        ProgElement(int scaleStep, Theory.chord chord) {
            this.scaleStep = scaleStep;
            this.chord = chord;
        }
        // getChord
        Theory.chord getChord() {
            if (scaleStep != 0)
                return Theory.num2Chord(scaleStep, key);
            else
                return chord;
        }
    }

    void stopPlaying(View v) {
        ImageView playButton = (ImageView) findViewById(v.getId());
        ImageView playhead = (ImageView) findViewById(R.id.playhead);

        playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        playing = false;
        playhead.setVisibility(View.INVISIBLE);
        playhead.clearAnimation();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        // release all sounds
        for (Sequence sequence : pianoTrack)
            for (ArrayList<MediaPlayer> slice : sequence.sounds)
                for (MediaPlayer sound : slice)
                    sound.release();
    }

    void startPlaying(View v, Animation animation) {
        ImageView playButton = (ImageView) findViewById(v.getId());
        ImageView playhead = (ImageView) findViewById(R.id.playhead);

        playButton.setImageResource(R.drawable.ic_pause_black_24dp);
        playing = true;
        playhead.setVisibility(View.VISIBLE);
        playhead.startAnimation(animation);
        for (int measure = 0; measure < pianoTrack.length; measure++) {
            pianoTrack[measure] = prog[measure].getChord().getSequence(getApplicationContext());
        }
        timer = new Timer(true);
        for (int sequence = 0; sequence < pianoTrack.length; sequence++) {
            for (int slice = 0; slice < pianoTrack[sequence].sounds.size(); slice++) {
                for (final MediaPlayer sound : pianoTrack[sequence].sounds.get(slice)) {
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            new Thread(new Sound(sound)).start();
                        }
                    };
                    double tick = 1000*60.0/bpm;
                    long delay = (long) Math.floor(sequence*tick);
                    long interval = (long) Math.floor(tick*4);
                    timer.scheduleAtFixedRate(task, 4*delay, 4*interval);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daw);
        Toolbar toolbar = (Toolbar) findViewById(R.id.bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        // TODO: tabs go away when screen is tilted sideways
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

        chord1Name = (TextView) findViewById(R.id.chord1Name);
        chord1Notes = (TextView) findViewById(R.id.chord1Notes);
        chord2Name = (TextView) findViewById(R.id.chord2Name);
        chord2Notes = (TextView) findViewById(R.id.chord2Notes);
        chord3Name = (TextView) findViewById(R.id.chord3Name);
        chord3Notes = (TextView) findViewById(R.id.chord3Notes);
        chord4Name = (TextView) findViewById(R.id.chord4Name);
        chord4Notes = (TextView) findViewById(R.id.chord4Notes);

        chordNames = new TextView[]{chord1Name, chord2Name, chord3Name, chord4Name};
        chordNotes = new TextView[]{chord1Notes, chord2Notes, chord3Notes, chord4Notes};

        c1 = (ImageView) findViewById(R.id.c1);
        db1 = (ImageView) findViewById(R.id.db1);
        d1 = (ImageView) findViewById(R.id.d1);
        eb1 = (ImageView) findViewById(R.id.eb1);
        e1 = (ImageView) findViewById(R.id.e1);
        f1 = (ImageView) findViewById(R.id.f1);
        gb1 = (ImageView) findViewById(R.id.gb1);
        g1 = (ImageView) findViewById(R.id.g1);
        ab1 = (ImageView) findViewById(R.id.ab1);
        a1 = (ImageView) findViewById(R.id.a1);
        bb1 = (ImageView) findViewById(R.id.bb1);
        b1 = (ImageView) findViewById(R.id.b1);

        piano1 = new ImageView[]{c1, db1, d1, eb1, e1, f1, gb1, g1, ab1, a1, bb1, b1};

        c2 = (ImageView) findViewById(R.id.c2);
        db2 = (ImageView) findViewById(R.id.db2);
        d2 = (ImageView) findViewById(R.id.d2);
        eb2 = (ImageView) findViewById(R.id.eb2);
        e2 = (ImageView) findViewById(R.id.e2);
        f2 = (ImageView) findViewById(R.id.f2);
        gb2 = (ImageView) findViewById(R.id.gb2);
        g2 = (ImageView) findViewById(R.id.g2);
        ab2 = (ImageView) findViewById(R.id.ab2);
        a2 = (ImageView) findViewById(R.id.a2);
        bb2 = (ImageView) findViewById(R.id.bb2);
        b2 = (ImageView) findViewById(R.id.b2);

        piano2 = new ImageView[]{c2, db2, d2, eb2, e2, f2, gb2, g2, ab2, a2, bb2, b2};

        c3 = (ImageView) findViewById(R.id.c3);
        db3 = (ImageView) findViewById(R.id.db3);
        d3 = (ImageView) findViewById(R.id.d3);
        eb3 = (ImageView) findViewById(R.id.eb3);
        e3 = (ImageView) findViewById(R.id.e3);
        f3 = (ImageView) findViewById(R.id.f3);
        gb3 = (ImageView) findViewById(R.id.gb3);
        g3 = (ImageView) findViewById(R.id.g3);
        ab3 = (ImageView) findViewById(R.id.ab3);
        a3 = (ImageView) findViewById(R.id.a3);
        bb3 = (ImageView) findViewById(R.id.bb3);
        b3 = (ImageView) findViewById(R.id.b3);

        piano3 = new ImageView[]{c3, db3, d3, eb3, e3, f3, gb3, g3, ab3, a3, bb3, b3};

        c4 = (ImageView) findViewById(R.id.c4);
        db4 = (ImageView) findViewById(R.id.db4);
        d4 = (ImageView) findViewById(R.id.d4);
        eb4 = (ImageView) findViewById(R.id.eb4);
        e4 = (ImageView) findViewById(R.id.e4);
        f4 = (ImageView) findViewById(R.id.f4);
        gb4 = (ImageView) findViewById(R.id.gb4);
        g4 = (ImageView) findViewById(R.id.g4);
        ab4 = (ImageView) findViewById(R.id.ab4);
        a4 = (ImageView) findViewById(R.id.a4);
        bb4 = (ImageView) findViewById(R.id.bb4);
        b4 = (ImageView) findViewById(R.id.b4);

        piano4 = new ImageView[]{c4, db4, d4, eb4, e4, f4, gb4, g4, ab4, a4, bb4, b4};

        pianos = new ImageView[][]{piano1, piano2, piano3, piano4};

        ///////////////////
        // CHORD BUTTONS //
        ///////////////////
        final Button chord1 = (Button) findViewById(R.id.chord1);
        chord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chordSelectorDialog(0, chord1);
            }
        });

        final Button chord2 = (Button) findViewById(R.id.chord2);
        chord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chordSelectorDialog(1, chord2);
            }
        });

        final Button chord3 = (Button) findViewById(R.id.chord3);
        chord3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chordSelectorDialog(2, chord3);
            }
        });

        final Button chord4 = (Button) findViewById(R.id.chord4);
        chord4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chordSelectorDialog(3, chord4);
            }
        });

        progButtons = new Button[]{chord1, chord2, chord3, chord4};

        /////////////////
        // SET UP PROG //
        /////////////////
        updateProgAt(0, new ProgElement(1, null));
        updateProgAt(1, new ProgElement(5, null));
        updateProgAt(2, new ProgElement(6, null));
        updateProgAt(3, new ProgElement(4, null));

        //////////////
        // PLAYHEAD //
        //////////////
        final ImageView playhead = (ImageView) findViewById(R.id.playhead);
        playhead.setVisibility(View.INVISIBLE);
        final TranslateAnimation animation = new TranslateAnimation (
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 1.0f,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(8000);
        animation.setInterpolator(new LinearInterpolator());
        final ImageView playButton = (ImageView) findViewById(R.id.play);
        fillTracks(); //create drum sequence
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playing) {
                    stopPlaying(v);
                }
                else{
                    startPlaying(v, animation);
                }
            }
        });

        //////////////////
        // TEMPO PICKER //
        //////////////////
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMinValue(40);
        numberPicker.setMaxValue(208);
        numberPicker.setValue(bpm); // default
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                if(playing){
                    stopPlaying(findViewById(R.id.play));
                    playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    playing = false;
                    playhead.setVisibility(View.INVISIBLE);
                    playhead.clearAnimation();
                }
                bpm = i2;
                animation.setDuration(16000*60/bpm);
            }
        });

        ////////////////
        // KEY PICKER //
        ////////////////
        NumberPicker keyPicker = (NumberPicker) findViewById(R.id.keyPicker);
        final String[] notez = new String[Theory.note.values().length];
        for (int i = 0; i < notez.length; i++)
            notez[i] = Theory.note.values()[i].name().replace("b", Theory.nonEmojiFlat);
        keyPicker.setMinValue(0);
        keyPicker.setMaxValue(notez.length-1);
        keyPicker.setDisplayedValues(notez);
        keyPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                key = Theory.note.values()[newVal];
                for (ProgElement pe : prog) {
                    if (pe.chord != null) {
                        Theory.note oldKey = Theory.note.values()[oldVal];
                        Theory.note newKey = Theory.note.values()[newVal];
                        pe.chord.root = pe.chord.root.addHalfSteps(newKey.halfStepsDownTo(oldKey));
                    }
                }
                for (int i = 0; i < prog.length; i++)
                    updateProgAt(i, prog[i]);
            }
        });
    }

    ///////////////////////////
    // CHORD SELECTOR DIALOG //
    ///////////////////////////
    private void chordSelectorDialog(final int chordNum, final Button thisButton) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.chord_picker);
        TextView t = (TextView) dialog.findViewById(R.id.textView2);
        final String chordX = "CHORD " + (chordNum+1);
        t.setText(chordX);

        // root picker
        final NumberPicker rootPick = (NumberPicker) dialog.findViewById(R.id.chordRootPicker);
        final String[] notez = new String[Theory.note.values().length];
        for (int i = 0; i < notez.length; i++) {
            notez[i] = Theory.note.values()[i].name().replace("b", Theory.nonEmojiFlat);
            if (notez[i].length() == 1)
                notez[i] += '\uFE0E';
        }
        rootPick.setMinValue(0);
        rootPick.setMaxValue(notez.length-1);
        rootPick.setDisplayedValues(notez);

        // type picker
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.chordTypePicker);
        String[] types = {"major", "minor", "diminished"};
        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, types);
        spinner.setAdapter(spinnerAdapter);

        // set initial root & type
        // if we know this chord fits into the scale
        Theory.chord yeChord = prog[chordNum].getChord();
        rootPick.setValue(yeChord.root.getVal());
        spinner.setSelection(spinnerAdapter.getPosition(yeChord.type.toString()));

        int[] suggs = getSugg(chordNum);
        LayoutInflater inflater =  getLayoutInflater();
        TableLayout suggTable = (TableLayout) dialog.findViewById(R.id.suggsTable);
        for (final int i : suggs) {
            // turn the number into anote chord
            // - get suggs
            final Theory.chord chord = Theory.num2Chord(i, key);
            // - add suggs to dialog
            // >>> look up how to do this, yo
            View suggRow = inflater.inflate(R.layout.suggestion_row, null);
            Button b = (Button)suggRow.findViewById(R.id.suggButt);
            b.setText(chord.toString());
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateProgAt(chordNum, new ProgElement(i, null));
                    dialog.dismiss();
                }
            });
            suggTable.addView(suggRow);
        }

        Button done = (Button) dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rootIndex = rootPick.getValue();
                int typeIndex = spinner.getSelectedItemPosition();
                Theory.chord chord = new Theory.chord(
                        Theory.note.values()[rootIndex],
                        Theory.type.values()[typeIndex]);
                thisButton.setText(chord.toString());

                // set current chord in prog
                // translate to number??!?!??
                int scaleStep = chord.isPartOf(key);
                // if the chord fits into our scale
                if (scaleStep != 0) {
                    updateProgAt(chordNum, new ProgElement(scaleStep, null));
                }
                else {
                    updateProgAt(chordNum, new ProgElement(0, chord));
                }

                dialog.dismiss();
            }
        });

        dialog.show();

        TableRow record = (TableRow) findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // here
            }
        });
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
        // base case, empty string means parent is the last letter in anote word
        if (i == line.length) {
            curr.end = true;
            return;
        }
        int j = line[i]-1; // j is the index of the interval, not the interval itself.
        // if next[j] is null, put anote node there
        if (curr.next[j] == null) {
            curr.next[j] = new Node();
        }
        // do the deed
        insertRecursive(curr.next[j], line, i+1);
    }

    /**
     * getSugg gets suggestions for the next chord to come in the progression. Suggestions are made
     * for chord number chordNum in the progression.
     * @param chordNum "get suggestions for chord #..." (index, 0-3)
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
        if (i < chordNum && prog[i].scaleStep != 0)
            return getSuggRecursive(curr.next[prog[i].scaleStep-1], chordNum, i+1);

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

    private void fillTracks(){
        MediaPlayer kick = MediaPlayer.create(this, R.raw.kick);
        MediaPlayer hat = MediaPlayer.create(this, R.raw.hat);
        MediaPlayer snare = MediaPlayer.create(this, R.raw.snare);

        Sequence drums = new Sequence();
        drums.addSound(kick, 0);
        drums.addSound(hat, 2);
        drums.addSound(snare, 4);
        drums.addSound(hat, 6);



        //drums.addSound(MediaPlayer.create(getApplicationContext(), R.raw.test), 0);

        //tracks.add(drums);
    }
}
