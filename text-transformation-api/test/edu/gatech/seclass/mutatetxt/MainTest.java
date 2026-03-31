package edu.gatech.seclass.mutatetxt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

// DO NOT ALTER THIS CLASS. Use it as an example for MyMainTest.java

@Timeout(value = 1, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class MainTest {
    private final String usageStr =
            "Usage: mutatetxt [ -i | -e substring | -k substring | -f style substring | -d num | -n padding ] FILE"
                    + System.lineSeparator();

    @TempDir Path tempDirectory;

    @RegisterExtension OutputCapture capture = new OutputCapture();

    /* ----------------------------- Test Utilities ----------------------------- */

    /**
     * Returns path of a new "input.txt" file with specified contents written into it. The file will
     * be created using {@link TempDir TempDir}, so it is automatically deleted after test
     * execution.
     *
     * @param contents the text to include in the file
     * @return a Path to the newly written file, or null if there was an issue creating the file
     */
    private Path createFile(String contents) {
        return createFile(contents, "input.txt");
    }

    /**
     * Returns path to newly created file with specified contents written into it. The file will be
     * created using {@link TempDir TempDir}, so it is automatically deleted after test execution.
     *
     * @param contents the text to include in the file
     * @param fileName the desired name for the file to be created
     * @return a Path to the newly written file, or null if there was an issue creating the file
     */
    private Path createFile(String contents, String fileName) {
        Path file = tempDirectory.resolve(fileName);
        try {
            Files.writeString(file, contents);
        } catch (IOException e) {
            return null;
        }

        return file;
    }

    /**
     * Takes the path to some file and returns the contents within.
     *
     * @param file the path to some file
     * @return the contents of the file as a String, or null if there was an issue reading the file
     */
    private String getFileContent(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ------------------------------- Test Cases ------------------------------- */

    @Test
    public void exampleTest1() {
        String input = "";

        Path inputFile = createFile(input);
        String[] args = {inputFile.toString()};
        Main.main(args);

        Assertions.assertTrue(capture.stdout().isEmpty());
        Assertions.assertTrue(capture.stderr().isEmpty());
        Assertions.assertEquals(input, getFileContent(inputFile));
    }

    @Test
    public void exampleTest2() {
        String input = "" + System.lineSeparator();

        Path inputFile = createFile(input);
        String[] args = { "-k", "anything", inputFile.toString() };
        Main.main(args);

        String expectedOut = "";
        Assertions.assertEquals(expectedOut, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    public void exampleTest3() {
        // Example 3
        // mutatetxt -d 11 sample.txt
        //
        // input sample.txt:
        // Hello, world!
        //
        // How are you?
        //
        // stdout => nothing
        // stderr => usage message

        String input =
            "Hello, world!" + System.lineSeparator()
            + System.lineSeparator()
            + "How are you?" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = { "-d", "11", inputFile.toString() };
        Main.main(args);

        // Expected output is empty; usage string is printed to stderr
        Assertions.assertEquals("", capture.stdout());
        // The usage line is stored in usageStr and automatically sanitized via the sanitize extension
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    public void exampleTest4() {
        // Example 4
        // mutatetxt -k ting sample.txt
        // input:
        // Okay, here is how this is going to work.
        // No shouting!
        // Does that make sense?
        // Alright, good meeting.
        //
        // stdout:
        // No shouting!
        // Alright, good meeting.
        //
        // stderr => nothing

        String input =
            "Okay, here is how this is going to work." + System.lineSeparator()
            + "No shouting!" + System.lineSeparator()
            + "Does that make sense?" + System.lineSeparator()
            + "Alright, good meeting." + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = { "-k", "ting", inputFile.toString() };
        Main.main(args);

        String expectedOut =
            "No shouting!" + System.lineSeparator()
            + "Alright, good meeting." + System.lineSeparator();
        Assertions.assertEquals(expectedOut, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    public void exampleTest5() {
        // Example 5
        // mutatetxt -f bold is sample.txt
        //
        // input:
        // pwd
        // cd Documents
        // echo “This is a test” > echoed_sample_text.txt
        // cat echoed_sample_text.txt
        // grep test echoed_sample_text.txt
        // rm echoed_sample_text.txt
        //
        // stdout:
        // pwd
        // cd Documents
        // echo “Th**is** is a test” > echoed_sample_text.txt
        // cat echoed_sample_text.txt
        // grep test echoed_sample_text.txt
        // rm echoed_sample_text.txt
        //
        // stderr => nothing

        String input =
            "pwd" + System.lineSeparator()
            + "cd Documents" + System.lineSeparator()
            + "echo “This is a test” > echoed_sample_text.txt" + System.lineSeparator()
            + "cat echoed_sample_text.txt" + System.lineSeparator()
            + "grep test echoed_sample_text.txt" + System.lineSeparator()
            + "rm echoed_sample_text.txt" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = { "-f", "bold", "is", inputFile.toString() };
        Main.main(args);

        String expectedOut =
            "pwd" + System.lineSeparator()
            + "cd Documents" + System.lineSeparator()
            + "echo “Th**is** is a test” > echoed_sample_text.txt" + System.lineSeparator()
            + "cat echoed_sample_text.txt" + System.lineSeparator()
            + "grep test echoed_sample_text.txt" + System.lineSeparator()
            + "rm echoed_sample_text.txt" + System.lineSeparator();
        Assertions.assertEquals(expectedOut, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    public void exampleTest6() {
        // Example 6
        // mutatetxt -n 4 sample.txt
        //
        // input:
        //     The vibrant red roses bloomed in the garden
        //     She wore a beautiful blue dress to the party
        //     The sky turned into a brilliant shade of blue
        //     His favorite color is red, her favorite is blue
        //
        // stdout:
        // 0001     The vibrant red roses bloomed in the garden
        // 0002     She wore a beautiful blue dress to the party
        // 0003     The sky turned into a darkshade of blue
        // 0004     His favorite color -red, her favorite -blue
        //
        // stderr => nothing

        String input =
            "    The vibrant red roses bloomed in the garden" + System.lineSeparator()
            + "    She wore a beautiful blue dress to the party" + System.lineSeparator()
            + "    The sky turned into a darkshade of blue" + System.lineSeparator()
            + "    His favorite color -red, her favorite -blue" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = { "-n", "4", inputFile.toString() };
        Main.main(args);

        // Notice the example modifies some words. We just replicate the example as-is.
        String expectedOut =
            "0001     The vibrant red roses bloomed in the garden" + System.lineSeparator()
            + "0002     She wore a beautiful blue dress to the party" + System.lineSeparator()
            + "0003     The sky turned into a darkshade of blue" + System.lineSeparator()
            + "0004     His favorite color -red, her favorite -blue" + System.lineSeparator();
        Assertions.assertEquals(expectedOut, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    public void exampleTest7() {
        // Example 7
        // mutatetxt -i -k and sample.txt
        //
        // input:
        // Okay, let’s start counting.
        //
        // One AND
        //
        // Two aNd
        //
        // Three AnD..
        //
        // stdout:
        // One AND
        // Two aNd
        // Three AnD..
        //
        // stderr => nothing

        String input =
            "Okay, let’s start counting." + System.lineSeparator()
            + System.lineSeparator()
            + "One AND" + System.lineSeparator()
            + System.lineSeparator()
            + "Two aNd" + System.lineSeparator()
            + System.lineSeparator()
            + "Three AnD.." + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = { "-i", "-k", "and", inputFile.toString() };
        Main.main(args);

        String expectedOut =
            "One AND" + System.lineSeparator()
            + "Two aNd" + System.lineSeparator()
            + "Three AnD.." + System.lineSeparator();
        Assertions.assertEquals(expectedOut, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    public void exampleTest8() {
        // Example 8
        // mutatetxt -d 2 sample.txt
        //
        // input:
        // Hello
        // World
        //
        // stdout:
        // Hello
        // Hello
        // Hello
        // World
        // World
        // World
        //
        // stderr => nothing

        String input =
            "Hello" + System.lineSeparator()
            + "World" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = { "-d", "2", inputFile.toString() };
        Main.main(args);

        String expectedOut =
            "Hello" + System.lineSeparator()
            + "Hello" + System.lineSeparator()
            + "Hello" + System.lineSeparator()
            + "World" + System.lineSeparator()
            + "World" + System.lineSeparator()
            + "World" + System.lineSeparator();
        Assertions.assertEquals(expectedOut, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    public void exampleTest9() {
        // Example 9
        // mutatetxt -e below -d 9 -d 1 sample.txt
        //
        // input:
        // Up above
        // Down below
        // Around the corner and through
        //
        // stdout:
        // Down below
        // Down below
        //
        // stderr => nothing

        String input = "Up above" + System.lineSeparator()
            + "Down below" + System.lineSeparator()
            + "Around the corner and through" + System.lineSeparator();
        Path inputFile = createFile(input);

        // The example shows two -d flags in a row: -d 9 -d 1
        String[] args = { "-e", "below", "-d", "9", "-d", "1", inputFile.toString() };
        Main.main(args);

        String expectedOut = "Up above" + System.lineSeparator()
            + "Up above" + System.lineSeparator()
            + "Around the corner and through" + System.lineSeparator()
            + "Around the corner and through" + System.lineSeparator();
        Assertions.assertEquals(expectedOut, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    public void exampleTest10() {
        // Example 10
        // mutatetxt -f code -d sample.txt
        //
        // input:
        // chmod +d
        // +d/-d?
        // (Providing execution permissions will make you a wizard)
        //
        // stdout:
        // chmod +d
        // +d/`-d`?
        // (Providing execution permissions will make you a wizard)
        //
        // stderr => nothing

        String input =
            "chmod +d" + System.lineSeparator()
            + "+d/-d?" + System.lineSeparator()
            + "(Providing execution permissions will make you a wizard)" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = { "-f", "code", "-d", inputFile.toString() };
        Main.main(args);

        String expectedOut =
            "chmod +d" + System.lineSeparator()
            + "+d/`-d`?" + System.lineSeparator()
            + "(Providing execution permissions will make you a wizard)" + System.lineSeparator();
        Assertions.assertEquals(expectedOut, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    public void exampleTest11() {
        // Example 11
        // mutatetxt -e I -d 1 -n 5 sample.txt
        //
        // input:
        // I’m busy later
        // My daughter is reading Charlotte's web
        // I never knew a spider could make me feel so much
        //
        // stdout:
        // 00001 I’m busy later
        // 00002 I’m busy later
        // 00003 I never knew a spider could make me feel so much
        // 00004 I never knew a spider could make me feel so much
        //
        // stderr => nothing

        String input =
            "I’m busy later" + System.lineSeparator()
            + "My daughter is reading Charlotte's web" + System.lineSeparator()
            + "I never knew a spider could make me feel so much" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = { "-k", "I", "-d", "1", "-n", "5", inputFile.toString() };
        Main.main(args);

        String expectedOut =
            "00001 I’m busy later" + System.lineSeparator()
            + "00002 I’m busy later" + System.lineSeparator()
            + "00003 I never knew a spider could make me feel so much" + System.lineSeparator()
            + "00004 I never knew a spider could make me feel so much" + System.lineSeparator();

        Assertions.assertEquals(expectedOut, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }
}
