package edu.wit.mobileapp.musicapp;

class Theory {

    static String nonEmojiFlat = "\u266D\uFE0E";

    enum note {
        C(0), Db(1), D(2), Eb(3), E(4), F(5), Gb(6), G(7), Ab(8), A(9), Bb(10), B(11);

        private final int val;
        note(int val) {
            this.val = val;
        }
        public int getVal() {
            return val;
        }
        note addHalfSteps(int i) {
            return note.values()[(this.getVal() + i) % note.values().length];
        }
        note getInterval(type t, int interval) {
            // catch perfect 4ths, 5ths and tritones
            final int[] halfSteps = {0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21};
            int intervalIndex = interval-1;
            switch (t) {
                // STILL TODO: catch fourths and fifths
                case tritone:
                    return this.addHalfSteps(6);
                case major:
                    return this.addHalfSteps(halfSteps[intervalIndex]);
                case minor:
                    return this.addHalfSteps(halfSteps[intervalIndex]-1);
            }
            return this;
        }
    }

    enum type {
        major, minor, diminished, tritone
    }

    static final private type[] majorScaleTypes = {
            type.major, type.minor, type.minor, type.major, type.major, type.minor, type.diminished
    };

    static class chord {
        note root;
        type type;

        chord(note root, type type) {
            this.root = root;
            this.type = type;
        }
        note[] getNotes() {
            return buildChord(root, type);
        }
        @Override
        public String toString() {
            String typeabbr = "";
            switch (type) {
                case minor:
                    typeabbr = "-";
                    break;
                case diminished:
                    typeabbr = "°";
                    break;
            }
            return root.toString().replace("b", nonEmojiFlat) + typeabbr;
        }
    }

    static chord num2Chord(int num, note tonic) {
        // TODO: is this chord's root always a major interval from the tonic?
        int scaleTypeIndex = num-1;
        return new chord(tonic.getInterval(type.major, num), majorScaleTypes[scaleTypeIndex]);
    }

    // TODO: make this suck less (see below)
    static private note[] buildChord(note root, type t) {
        note[] chord = new note[3];
        chord[0] = root;
        switch (t) {
            case major:
                chord[1] = root.addHalfSteps(4);
                chord[2] = root.addHalfSteps(7);
                break;
            case minor:
                chord[1] = root.addHalfSteps(3);
                chord[2] = root.addHalfSteps(7);
                break;
            case diminished:
                chord[1] = root.addHalfSteps(3);
                chord[2] = root.addHalfSteps(6);
        }
        return chord;
    }

    public static void main(String[] args) {
        Theory.chord chord = new chord(note.Bb, type.diminished);
        for (note n : chord.getNotes()) {
            System.out.println(n);
        }
    }

}
