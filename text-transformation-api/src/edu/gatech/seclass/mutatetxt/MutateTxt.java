package edu.gatech.seclass.mutatetxt;

import java.util.*;
import java.nio.file.*;
import java.io.IOException;


/**
 * Implementation of the MutateTxtInterface for CS6300 Individual Project D4.
 *
 * This class stores configuration through setter methods and applies text transformations
 * when mutatetxt() is called, according to the specifications.
 */
public class MutateTxt implements MutateTxtInterface {

    private String filepath;
    private boolean ignoreCase;
    private String excludeSubstring;
    private String keepSubstring;
    private Style formatStyle;
    private String formatTarget;
    private Integer duplicateFactor;
    private Integer lineNumberPadding;

    @Override
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void setCaseInsensitive(boolean caseInsensitive) {
        this.ignoreCase = caseInsensitive;
    }

    @Override
    public void setExcludeString(String excludeString) {
        this.excludeSubstring = excludeString;
    }

    @Override
    public void setKeepLines(String keepLines) {
        this.keepSubstring = keepLines;
    }

    @Override
    public void setFormatText(Style style, String strToFormat) {
        this.formatStyle = style;
        this.formatTarget = strToFormat;
    }

    @Override
    public void setDuplicateFactor(Integer duplicateFactor) {
        this.duplicateFactor = duplicateFactor;
    }

    @Override
    public void setAddPaddedLineNumber(Integer padding) {
        this.lineNumberPadding = padding;
    }

    @Override
    public void reset() {
        filepath = null;
        ignoreCase = false;
        excludeSubstring = null;
        keepSubstring = null;
        formatStyle = null;
        formatTarget = null;
        duplicateFactor = null;
        lineNumberPadding = null;
    }



    @Override
    public void mutatetxt() throws MutateTxtException {
        // Validate inputs here

        if (filepath == null || filepath.trim().isEmpty()) {
            throw new MutateTxtException("Input file path has not set yet.");
        }

        if (excludeSubstring != null && keepSubstring != null) {
            throw new MutateTxtException("Not allowed to set both keepLines and excludeString simultaneously.");
        }

        // if (excludeSubstring != null) {
        //     if (excludeSubstring.isEmpty()) {
        //         throw new MutateTxtException("The Exclude substring cannot set to be empty.");
        //     }
        // }


        if (ignoreCase && keepSubstring == null && excludeSubstring == null) {
            throw new MutateTxtException("Case-insensitive mode requires to use with keep or exclude string.");
        }


        // if (keepSubstring != null) {
        //     if (keepSubstring.isEmpty()) {
        //         throw new MutateTxtException("The Keep substring cannot set to be empty.");
        //     }
        // }

        if (formatStyle != null) {
            if (formatTarget == null || formatTarget.isEmpty()) {
                throw new MutateTxtException("The Format target string cannot be null or empty.");
            }
        }

        if (duplicateFactor != null) {
            if (duplicateFactor < 0 || duplicateFactor > 9) {
                throw new MutateTxtException("The value of Duplicate factor must be within the range of 0 and 9.");
            }
        }

        if (lineNumberPadding != null) {
            if (lineNumberPadding < 1 || lineNumberPadding > 9) {
                throw new MutateTxtException("The value of Line number padding must be within the range of 1 and 9.");
            }
        }

        List<String> lines = readInputFile(filepath);
        lines = applyFilter(lines);
        lines = applyFormatting(lines);
        lines = applyDuplication(lines);
        lines = applyNumbering(lines);

        for (String line : lines) {
            System.out.println(line);
        }
    }



    private List<String> readInputFile(String filepath) throws MutateTxtException {
        Path path = Paths.get(filepath);
        if (!Files.exists(path)) {
            throw new MutateTxtException("The file does not exist: " + filepath);
        }
        if (!Files.isReadable(path)) {
            throw new MutateTxtException("The file is not readable: " + filepath);
        }
        if (Files.isDirectory(path)) {
            throw new MutateTxtException("The file path refers to a directory instead of a file: " + filepath);
        }

        try {
            String content = Files.readString(path);
            if (content.isEmpty()) {
                return new ArrayList<>();
            }

            String lineSep = System.lineSeparator();
            if (!content.endsWith(lineSep)) {
                throw new MutateTxtException("The Non-empty file must end with a newline.");
            }

            List<String> lines = new ArrayList<>();
            int from = 0;
            while (true) {
                int to = content.indexOf(lineSep, from);
                if (to == -1) {
                    break;
                }
                lines.add(content.substring(from, to));
                from = to + lineSep.length();
            }
            return lines;
        } catch (IOException e) {
            throw new MutateTxtException("Error occurs once reading file: " + e.getMessage());
        }
    }

    private List<String> applyFilter(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (shouldKeep(line)) {
                result.add(line);
            }
        }
        return result;
    }

    private boolean shouldKeep(String line) {
        if (keepSubstring != null) {
            return matches(line, keepSubstring);
        } else {
            if (excludeSubstring != null) {
                return !matches(line, excludeSubstring);
            } else {
                return true;
            }
        }
    }

    private boolean matches(String line, String pattern) {
        if (ignoreCase) {
            return line.toLowerCase().contains(pattern.toLowerCase());
        } else {
            return line.contains(pattern);
        }
    }

    private List<String> applyFormatting(List<String> lines) {
        if (formatStyle == null || formatTarget == null) {
            return lines;
        }

        String prefix;
        String suffix;
        if (formatStyle == Style.bold) {
            prefix = "**";
            suffix = "**";
        } else if (formatStyle == Style.italic) {
            prefix = "*";
            suffix = "*";
        } else if (formatStyle == Style.code) {
            prefix = "`";
            suffix = "`";
        } else {
            return lines;
        }

        List<String> result = new ArrayList<>();
        for (String line : lines) {
            int pos = line.indexOf(formatTarget);

            if (pos == -1) {
                result.add(line);
            } else {
                String matched = line.substring(pos, pos + formatTarget.length());
                StringBuilder sb = new StringBuilder();
                sb.append(line, 0, pos);
                sb.append(prefix).append(matched).append(suffix);
                sb.append(line.substring(pos + formatTarget.length()));
                result.add(sb.toString());
            }
        }
        return result;
    }


    private List<String> applyDuplication(List<String> lines) {
        if (duplicateFactor == null) {
            return lines;
        }

        List<String> result = new ArrayList<>();
        for (String line : lines) {
            for (int i = 0; i <= duplicateFactor; i++) {
                result.add(line);
            }
        }
        return result;
    }

    private List<String> applyNumbering(List<String> lines) {
        if (lineNumberPadding == null) {
            return lines;
        }

        List<String> result = new ArrayList<>();
        int lineNum = 1;
        for (String line : lines) {
            String numbered = String.format("%0" + lineNumberPadding + "d %s", lineNum, line);
            result.add(numbered);
            lineNum++;
        }
        return result;
    }
}