package edu.wit.mobileapp.musicapp;

final class Theory {

    enum note {
        C(0), Db(1), D(2), Eb(3), E(4), F(5), Gb(6), G(7), Ab(8), A(9), Bb(10), B(11);

        private final int val;
        note(int val) {this.val = val;}
        public int getVal() {return val;}
    }

    enum type {
        major, minor, diminished, tritone
    }

    // TODO: make this suck less (see below)
    note[] buildChord(note root, type k) {
        note[] chord = new note[3];
        chord[0] = root;
        switch (k) {
            case major:
                chord[1] = addHalfSteps(root, 4);
                chord[2] = addHalfSteps(root, 7);
                break;
            case minor:
                chord[1] = addHalfSteps(root, 3);
                chord[2] = addHalfSteps(root, 7);
                break;
            case diminished:
                chord[1] = addHalfSteps(root, 3);
                chord[2] = addHalfSteps(root, 6);
        }
        return chord;
    }

    note getInterval(note n, type k, int i) {
        if (k == type.tritone)
            return addHalfSteps(n, 6);
        // TODO: for intervals... make a nother enumerator????
        // JK! a final array will do.
        // each index is an interval
        // the value at each index will be the number of half steps that make up that interval
        // catch perfect 4ths and 5ths
        return null; // for now
    }

    // TODO: make an interval function that returns the major minior or perfect ith interval from n
    private note addHalfSteps(note n, int i) {
        return note.values()[(n.getVal() + i) % note.values().length];
    }

    public static void main(String[] args) {
        Theory t = new Theory();
        note[] chord = t.buildChord(note.Eb, type.minor);
        for (note n : chord) {
            System.out.println(n);
        }
    }

}
