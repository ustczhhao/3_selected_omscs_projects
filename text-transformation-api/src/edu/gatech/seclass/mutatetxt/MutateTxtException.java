package edu.gatech.seclass.mutatetxt;

/** Signals that an error occurred when running MutateTxt. */
public class MutateTxtException extends Exception {
    /**
     * Constructs a MutateTxtException with the specified message describing the error.
     *
     * @param s the error message
     */
    MutateTxtException(String s) {
        super(s);
    }
}
