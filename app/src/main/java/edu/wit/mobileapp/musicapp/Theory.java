package edu.wit.mobileapp.musicapp;

class Theory {

    // forces unicode character and not emoji
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

        /**
         * @param t: interval type (major, minor, perfect, tritone)
         * @param interval: interval number, like a 7th or a 4th
         * @return note of that interval
         */
        note getInterval(type t, int interval) {
            // catch perfect 4ths, 5ths and tritones
            int halfStepIndex = interval-1;
            switch (t) {
                // STILL TODO: catch fourths and fifths
                case tritone:
                    return this.addHalfSteps(6);
                case major:
                    return this.addHalfSteps(halfSteps[halfStepIndex]);
                case minor:
                    return this.addHalfSteps(halfSteps[halfStepIndex]-1);
            }
            return this;
        }
        int halfStepsDownTo(note n) {
            if (this.getVal() > n.getVal())
                return this.getVal() - n.getVal();
            else
                return this.getVal() + 12 - n.getVal();
        }
    }

    enum type {
        major, minor, diminished, tritone
    }

    static final private type[] majorScaleTypes = {
            type.major, type.minor, type.minor, type.major, type.major, type.minor, type.diminished
    };

    static final private int[] halfSteps = {
            0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21
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
        int isPartOf(note key) {
            int halfStepDiff = this.root.halfStepsDownTo(key);
            for (int i = 0; i < majorScaleTypes.length; i++)
                // halfSteps contains the interval in half steps and the scale type is correct for that interval
                if (halfStepDiff == halfSteps[i] && majorScaleTypes[i] == type)
                    return i + 1;
            return 0;
        }
        String getNotesString() {
            String ret = "";
            for (int i=0; i<getNotes().length; i++) {
                ret+=getNotes()[i].name();
                if(i < getNotes().length-1) {
                    ret += "\n";
                }
            }
            return ret.replace("b", nonEmojiFlat);
        }
        @Override
        public String toString() {
            String typeabbr = "";
            switch (type) {
                case minor:
                    typeabbr = "m";
                    break;
                case diminished:
                    typeabbr = "°";
                    break;
            }
            return root.toString().replace("b", nonEmojiFlat + " ") + typeabbr;
        }
    }

    static chord num2Chord(int num, note key) {
        int scaleTypeIndex = num-1;
        return new chord(key.getInterval(type.major, num), majorScaleTypes[scaleTypeIndex]);
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

        chord hey = new chord(note.Ab, type.major);
        int ass = hey.isPartOf(note.G);

        return;

    }

}
