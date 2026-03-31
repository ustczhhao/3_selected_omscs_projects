package edu.gatech.seclass.mutatetxt;

/**
 * Interface created for use in Georgia Tech CS6300.
 *
 * <p>IMPORTANT: This interface should NOT be altered in any way.
 */
public interface MutateTxtInterface {

    enum Style {
        bold,
        italic,
        code
    }

    /** Reset the MutateTxt object to its initial state, for reuse. */
    void reset();

    /**
     * Sets the path of the input file. This method has to be called before invoking the {@link
     * #mutatetxt()} methods.
     *
     * @param filepath The file path to be set.
     */
    void setFilepath(String filepath);

    /**
     * Set to apply case-insensitive matching when used @TODO: flag ONLY. This method has to be
     * called before invoking the {@link #mutatetxt()} method.
     *
     * @param caseInsensitive Flag to toggle functionality
     */
    void setCaseInsensitive(boolean caseInsensitive);

    /**
     * Set to exclude all lines containing the substring excludeString. This method has to be called
     * before invoking the {@link #mutatetxt()} method.
     *
     * @param excludeString The string to be excluded
     */
    void setExcludeString(String excludeString);

    /**
     * Set to keep only the lines containing the given string. This method has to be called before
     * invoking the {@link #mutatetxt()} method.
     *
     * @param keepLines The string to be included
     */
    void setKeepLines(String keepLines);

    /**
     * Set to format the text based on the style parameter. This method has to be called before
     * invoking the {@link #mutatetxt()} method.
     *
     * @param style The style to be applied
     * @param strToFormat The string to be formatted
     */
    void setFormatText(Style style, String strToFormat);

    /**
     * Set to duplicate each line in the file n times, where n is an integer in the inclusive range
     * of 0 to 9. The duplicate lines should be sequential to each other. This method has to be
     * called before invoking the {@link #mutatetxt()} method.
     *
     * @param duplicateFactor Number of times to duplicate the line
     */
    void setDuplicateFactor(Integer duplicateFactor);

    /**
     * Set to add line numbers to each line, with the amount of padding based upon the padding
     * parameter, starting from 1. This method has to be called before invoking the {@link
     * #mutatetxt()} method.
     *
     * @param padding The amount of padding to be used
     */
    void setAddPaddedLineNumber(Integer padding);

    /**
     * Outputs a System.lineSeparator() delimited string that contains selected parts of the lines
     * in the file specified using {@link #setFilepath} and according to the current configuration,
     * which is set through calls to the other methods in the interface.
     *
     * <p>It throws a {@link MutateTxtException} if an error condition occurs (e.g., when the
     * specified file does not exist).
     *
     * @throws MutateTxtException thrown if an error condition occurs
     */
    void mutatetxt() throws MutateTxtException;
}
