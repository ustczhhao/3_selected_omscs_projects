package edu.gatech.seclass.mutatetxt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;


public class Main {
    // Empty Main class for compiling Individual Project
    // During Deliverable 1 and Deliverable 2, DO NOT ALTER THIS CLASS or implement it

    public static void main(String[] args) {

        Options opts = parseArgs(args);
        if (opts == null) return;


        List<String> lines = readInputFile(opts.inputFile);
        if (lines == null) return;

        List<String> filtered = applyFilter(lines, opts);

        List<String> formatted = applyFormatting(filtered, opts);

        List<String> duplicated = applyDuplication(formatted, opts);

        List<String> numbered = applyNumbering(duplicated, opts);

        writeOutputToStdout(numbered);
    }


    private static void usage() {
        System.err.println(
                "Usage: mutatetxt [ -i | -e substring | -k substring | -f style substring | -d num | -n padding ] FILE");
    }


    private static Options usageReturn() {
        usage();
        return null;
    }

    private static class Options {
        boolean ignoreCase = false;
        String keepSubstring = null;
        String excludeSubstring = null;
        String formatStyle = null;
        String formatSubstring = null;
        Integer duplicationNum = null;
        Integer lineNumberPadding = null;
        String inputFile = null;
    }

    private static Options parseArgs(String[] args) {
        Options opts = new Options();
        int i = 0;

        while (i < args.length) {
            String arg = args[i];

            switch (arg) {
                case "-i":
                    opts.ignoreCase = true; i++; break;
                case "-k":
                    if (i + 1 >= args.length || opts.excludeSubstring != null) return usageReturn();
                    opts.keepSubstring = args[++i];
                    if (opts.keepSubstring.isEmpty()) return usageReturn();
                    i++; break;
                case "-e":
                    if (i + 1 >= args.length || opts.keepSubstring != null) return usageReturn();
                    opts.excludeSubstring = args[++i];
                    if (opts.excludeSubstring.isEmpty()) return usageReturn();
                    i++; break;
                case "-f":
                    if (i + 2 >= args.length) return usageReturn();
                    String style = args[++i], substr = args[++i];
                    if (!style.equals("bold") && !style.equals("italic") && !style.equals("code")) return usageReturn();
                    if (substr.isEmpty()) return usageReturn();
                    opts.formatStyle = style; opts.formatSubstring = substr;
                    i++; break;
                case "-d":
                    if (i + 1 >= args.length) return usageReturn();
                    try {
                        int d = Integer.parseInt(args[++i]);
                        if (d < 0 || d > 9) return usageReturn();
                        opts.duplicationNum = d; i++;
                    } catch (NumberFormatException e) {
                        return usageReturn();
                    }
                    break;
                case "-n":
                    if (i + 1 >= args.length) return usageReturn();
                    try {
                        int pad = Integer.parseInt(args[++i]);
                        if (pad < 1 || pad > 9) return usageReturn();
                        opts.lineNumberPadding = pad; i++;
                    } catch (NumberFormatException e) {
                        return usageReturn();
                    }
                    break;
                default:
                    if (i == args.length - 1 && !arg.startsWith("-")) {
                        opts.inputFile = arg;
                        i++;
                    } else {
                        return usageReturn();
                    }
            }
        }

        if (opts.inputFile == null) return usageReturn();
        if (opts.ignoreCase && opts.keepSubstring == null && opts.excludeSubstring == null) return usageReturn();

        return opts;
    }


    private static List<String> readInputFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isReadable(path) || Files.isDirectory(path)) {
            usage();
            return null;
        }

        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            String lineSep = System.lineSeparator();

            if (content.isEmpty()) return new ArrayList<>();
            if (!content.endsWith(lineSep)) {
                usage();
                return null;
            }

            List<String> lines = new ArrayList<>();
            int from = 0;
            while (true) {
                int to = content.indexOf(lineSep, from);
                if (to == -1) break;
                lines.add(content.substring(from, to));
                from = to + lineSep.length();
            }

            return lines;

        } catch (IOException e) {
            usage();
            return null;
        }
    }


    private static boolean matches(String line, String pattern, boolean ignoreCase) {
        if (ignoreCase) {
            return line.toLowerCase().contains(pattern.toLowerCase());
        } else {
            return line.contains(pattern);
        }
    }


    private static boolean shouldKeep(String line, Options opts) {
        if (opts.keepSubstring != null) return matches(line, opts.keepSubstring, opts.ignoreCase);
        if (opts.excludeSubstring != null) return !matches(line, opts.excludeSubstring, opts.ignoreCase);
        return true;
    }


    private static List<String> applyFilter(List<String> lines, Options opts) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (shouldKeep(line, opts)) result.add(line);
        }
        return result;
    }


    private static List<String> applyFormatting(List<String> lines, Options opts) {
        if (opts.formatStyle == null) return lines;

        String prefix, suffix;
        switch (opts.formatStyle) {
            case "bold": prefix = "**"; suffix = "**"; break;
            case "italic": prefix = "*"; suffix = "*"; break;
            case "code": prefix = "`"; suffix = "`"; break;
            default: return lines;
        }

        List<String> result = new ArrayList<>();
        for (String line : lines) {
            int pos = line.indexOf(opts.formatSubstring);
            if (pos == -1) {
                result.add(line);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(line, 0, pos);
                sb.append(prefix).append(opts.formatSubstring).append(suffix);
                sb.append(line.substring(pos + opts.formatSubstring.length()));
                result.add(sb.toString());
            }
        }
        return result;
    }


    private static List<String> applyDuplication(List<String> lines, Options opts) {
        if (opts.duplicationNum == null || opts.duplicationNum == 0) return lines;

        List<String> result = new ArrayList<>();
        for (String line : lines) {
            for (int i = 0; i <= opts.duplicationNum; i++) {
                result.add(line);
            }
        }
        return result;
    }


    private static List<String> applyNumbering(List<String> lines, Options opts) {
        if (opts.lineNumberPadding == null) return lines;

        List<String> result = new ArrayList<>();
        int lineNum = 1;
        for (String line : lines) {
            String numbered = String.format("%0" + opts.lineNumberPadding + "d %s", lineNum, line);
            result.add(numbered);
            lineNum++;
        }
        return result;
    }


    private static void writeOutputToStdout(List<String> lines) {
        for (String line : lines) {
            System.out.println(line); 
        }
    }

}
