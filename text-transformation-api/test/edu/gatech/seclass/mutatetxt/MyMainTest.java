package edu.gatech.seclass.mutatetxt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


@Timeout(value = 1, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class MyMainTest {
    // Place all of your tests in this class, optionally using MainTest.java as an example
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


    // Frame 1: <test case 1 in file catpart.txt.tsl>; Test Case 1. File not exist
    @Test
    public void mutatetxtTest1() {
        String[] args = {"nonexistent.txt"};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }

    // Frame 2: <test case 2 in file catpart.txt.tsl>; Test Case 2. line not end with new line
    @Test
    public void mutatetxtTest2() {
        String input = "Line without newline"; 
        Path inputFile = createFile(input);
        String[] args = {inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 3: <test case 3 in file catpart.txt.tsl>; Test Case 3. valid d
    @Test
    public void mutatetxtTest3() {

        int num = 3;
        String input = "Test line" + System.lineSeparator();
        Path inputFile = createFile(input);
        String[] args = {"-d", String.valueOf(num), inputFile.toString()};
        Main.main(args);
        
        String expectedOut = 
            "Test line" + System.lineSeparator() + 
            "Test line" + System.lineSeparator() + 
            "Test line" + System.lineSeparator() + 
            "Test line" + System.lineSeparator(); 
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }



    // Frame 4: <test case 4 in file catpart.txt.tsl>; Test Case 4: d < 0
    @Test
    public void mutatetxtTest4() {
        Path inputFile = createFile("Test line");
        String[] args = {"-d", "-1", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }



    // Frame 5: <test case 5 in file catpart.txt.tsl>;Test Case 5: d > 9
    @Test
    public void mutatetxtTest5() {
        Path inputFile = createFile("Test line");
        String[] args = {"-d", "10", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 6: <test case 6 in file catpart.txt.tsl>; Test Case 6: d is not integer
    @Test
    public void mutatetxtTest6() {
        Path inputFile = createFile("Test line");
        String[] args = {"-d", "abc", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 7: <test case 7 in file catpart.txt.tsl>; Test Case 7: d repeated and end with an invalid value
    @Test
    public void mutatetxtTest7() {
        Path inputFile = createFile("Test line");
        String[] args = {"-d", "3", "-d", "invalid", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 8: <test case 8 in file catpart.txt.tsl>; Test Case 8: -f bold 
    @Test
    public void mutatetxtTest8() {
        String input = "Replace this substring" + System.lineSeparator();
        Path inputFile = createFile(input);
        String[] args = {"-f", "bold", "this", inputFile.toString()};
        Main.main(args);

        String expectedOut = "Replace **this** substring" + System.lineSeparator(); 
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 9: <test case 9 in file catpart.txt.tsl>; Test Case 9: -f italic 
    @Test
    public void mutatetxtTest9() {
        String input = "Replace this substring" + System.lineSeparator();
        Path inputFile = createFile(input);
        String[] args = {"-f", "italic", "this", inputFile.toString()};
        Main.main(args);

        String expectedOut = "Replace *this* substring" + System.lineSeparator(); 
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 10: <test case 10 in file catpart.txt.tsl>; Test Case 10: -f code 
    @Test
    public void mutatetxtTest10() {
        String input = "Replace this substring" + System.lineSeparator();
        Path inputFile = createFile(input);
        String[] args = {"-f", "code", "this", inputFile.toString()};
        Main.main(args);

        String expectedOut = "Replace `this` substring" + System.lineSeparator(); 
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 11: <test case 11 in file catpart.txt.tsl>; Test Case 11: -f invalid parameter
    @Test
    public void mutatetxtTest11() {
        String input = "Test line" + System.lineSeparator();
        Path inputFile = createFile(input);
        String[] args = {"-f", "underline", "test", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 12: <test case 12 in file catpart.txt.tsl>; Test Case 12: -f for empty str
    @Test
    public void mutatetxtTest12() {
        String input = "Test line" + System.lineSeparator();
        Path inputFile = createFile(input);
        String[] args = {"-f", "bold", "", inputFile.toString()}; 
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }



    // Frame 13: <test case 13 in file catpart.txt.tsl>; Test Case 13: -f with repeated parameter and end with an invalid one 
    @Test
    public void mutatetxtTest13() {
        String input = "Test line" + System.lineSeparator();
        Path inputFile = createFile(input);
        String[] args = {"-f", "bold", "valid", "-f", "invalid_style", "substring", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }



    // Frame 14:  empthy string in the input file but with option 
    @Test
    public void mutatetxtTest14() {
        String input = "";
        Path inputFile = createFile(input);
        String[] args = {"-n", "3", inputFile.toString()}; 
        Main.main(args);

        assertTrue(capture.stdout().isEmpty());
        assertTrue(capture.stderr().isEmpty());
    }


    // Frame 15:  Test Case 15: use -i without -k/-e
    @Test
    public void mutatetxtTest15() {
        String input = "Test line" + System.lineSeparator();
        Path inputFile = createFile(input);
        String[] args = {"-i", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 16:  Test Case 16: use valid n
    @Test
    public void mutatetxtTest16() {
        String input = 
            "Line 1" + System.lineSeparator() +
            "Line 2" + System.lineSeparator();
        Path inputFile = createFile(input);
        String[] args = {"-n", "3", inputFile.toString()};
        Main.main(args);
        String expectedOut = 
            "001 Line 1" + System.lineSeparator() +
            "002 Line 2" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 17; Test Case 17: invalid n (n < 1)
    @Test
    public void mutatetxtTest17() {
        Path inputFile = createFile("Test line") ;
        String[] args = {"-n", "0", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 18; Test Case 18: invalid n (n > 9)
    @Test
    public void mutatetxtTest18() {
        Path inputFile = createFile("Test line");
        String[] args = {"-n", "10", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 19; Test Case 19: invalid n (non-integer)
    @Test
    public void mutatetxtTest19() {
        Path inputFile = createFile("Test line");
        String[] args = {"-n", "abc", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 20; Test Case 20: repeated n and end with an invalid one 
    @Test
    public void mutatetxtTest20() {
        Path inputFile = createFile("Test line");
        String[] args = {"-n", "5", "-n", "invalid", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 21; Test Case 21: invalid option -x
    @Test
    public void mutatetxtTest21() {
        Path inputFile = createFile("Test line");
        String[] args = {"-x", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 22; Test Case 22: invalid long option --invalid
    @Test
    public void mutatetxtTest22() {
        Path inputFile = createFile("Test line");
        String[] args = {"--invalid", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 23; Test Case 23: use -k and -e at the same time 
    @Test
    public void mutatetxtTest23() {
        Path inputFile = createFile("Test line");
        String[] args = {"-k", "keep", "-e", "exclude", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 24; Test Case 24: only use -i withot -k and -e 
    @Test
    public void mutatetxtTest24() {
        Path inputFile = createFile("Test line");
        String[] args = {"-i", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 25; Test Case 25: negative value for -d 
    @Test
    public void mutatetxtTest25() {
        Path inputFile = createFile("Test line");
        String[] args = {"-d", "-5", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }



    // Frame 26; Test Case 26: -d non-integer
    @Test
    public void mutatetxtTest26() {
        Path inputFile = createFile("Test line");
        String[] args = {"-d", "3.5", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 27; Test Case 27: invalid -f 
    @Test
    public void mutatetxtTest27() {
        Path inputFile = createFile("Test line");
        String[] args = {"-f", "underline", "test", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }



    // Frame 28; Test Case 28: -f with empty substring
    @Test
    public void mutatetxtTest28() {
        Path inputFile = createFile("Test line");
        String[] args = {"-f", "bold", "", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 29; Test Case 29: -k with empty string
    @Test
    public void mutatetxtTest29() {
        Path inputFile = createFile("Test line");
        String[] args = {"-k", "", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 30; Test Case 30: -e with empty string
    @Test
    public void mutatetxtTest30() {
        Path inputFile = createFile("Test line");
        String[] args = {"-e", "", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }




    // Frame 31; Test Case 31: -n with non-integer value
    @Test
    public void mutatetxtTest31() {
        Path inputFile = createFile("Test line");
        String[] args = {"-n", "4.9", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }



    // Frame 32; Test Case 32: invalid --invalid for file end with new line
    @Test
    public void mutatetxtTest32() {
        Path inputFile = createFile("Test line" + System.lineSeparator());
        String[] args = {"--invalid", inputFile.toString()};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 33; Test Case 33: non-exist file 
    @Test
    public void mutatetxtTest33() {
        String[] args = {"nonexistent2.txt"};
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }



    // Frame 34; Test Case 34: repeated f and end with an invalid one
    @Test
    public void mutatetxtTest34() {
        Path inputFile = createFile("Test line");
        String[] args = {
            "-f", "bold", "valid",
            "-f", "invalid_style", "substring", 
            inputFile.toString()
        };
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 35; Test Case 35: repeated n and end with an invalid one
    @Test
    public void mutatetxtTest35() {
        Path inputFile = createFile("Test line");
        String[] args = {
            "-n", "3",
            "-n", "invalid", 
            inputFile.toString()
        };
        Main.main(args);
        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 36; Test Case 36: -k with empty file
    @Test
    public void mutatetxtTest36() {
        String input = ""; 
        Path inputFile = createFile(input);
        String[] args = {"-k", "keyword", inputFile.toString()};
        Main.main(args);
        assertTrue(capture.stdout().isEmpty());
        assertTrue(capture.stderr().isEmpty());
    }


    // Frame 37; Test frame 37
    @Test 
    public void mutatetxtTest37() {
        String input = "";
        Path inputFile = createFile(input);

        String[] args = {"-i", "-k", "non_empty_keyword", inputFile.toString()};
        Main.main(args);

        assertTrue(capture.stdout().isEmpty());
        assertTrue(capture.stderr().isEmpty());
        // assertEquals(input, getFileContent(inputFile));
    }





    // Frame 38; Test frame 38: empty file + -k + nonempty string + -i
    @Test
    public void mutatetxtTest38() {

        String input = "";
        Path inputFile = createFile(input);

        String[] args = {"-i", "-k", "keyword", inputFile.toString()};
        Main.main(args);

        assertTrue(capture.stdout().isEmpty());
        assertTrue(capture.stderr().isEmpty());
    }



    // Frame 39; Test frame 39: empty file + -e + nonempty string + -i
    @Test
    public void mutatetxtTest39() {

        String input = "";
        Path inputFile = createFile(input);

        String[] args = {"-i", "-e", "exclude_substring", inputFile.toString()};
        Main.main(args);

        assertTrue(capture.stdout().isEmpty());
        assertTrue(capture.stderr().isEmpty());
    }


    // Frame 40; Test frame 40: empty file + -e + nonempty string 
    @Test
    public void mutatetxtTest40() {

        String input = "";
        Path inputFile = createFile(input);

        String[] args = {"-e", "exclude_substring", inputFile.toString()};
        Main.main(args);

        assertTrue(capture.stdout().isEmpty());
        assertTrue(capture.stderr().isEmpty());
    }


    // Frame 41; Test frame 41: empty file + -k + -e + nonempty string + -i
    @Test
    public void mutatetxtTest41() {
        String input = "";
        Path inputFile = createFile(input);
        String[] args = {"-i", "-k", "keep", "-e", "exclude", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }

    
    // Frame 42: Test frame 42: empty file + -k + -e + nonempty string 
    @Test
    public void mutatetxtTest42() {

        String input = "";
        Path inputFile = createFile(input);

        String[] args = {"-k", "keep", "-e", "exclude", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }

    
    // Frame 43; Test frame 43: nonempty file + -i + -k + nonempty string 
    @Test
    public void mutatetxtTest43() {
        String input = "Hello World" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-i", "-k", "world", inputFile.toString()};
        Main.main(args);

        String expectedOut = "Hello World" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 44; Test frame 44: nonempty file + -k + nonempty string 
    @Test
    public void mutatetxtTest44() {

        String input = 
            "Line 1: test" + System.lineSeparator() +
            "Line 2: keyword" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-k", "keyword", inputFile.toString()};
        Main.main(args);

        String expectedOut = "Line 2: keyword" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 45; Test frame 45: nonempty file + -i + -k + nonempty string (mixed captialized and lower-cased str)
    @Test
    public void mutatetxtTest45() {

        String input = 
            "Apple" + System.lineSeparator() +
            "BANANA" + System.lineSeparator() +
            "Cherry" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = {"-i", "-k", "banana", inputFile.toString()};
        Main.main(args);

        String expectedOut = "BANANA" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 46; Test frame 46: nonempty file + -k + nonempty string (mixed captialized and lower-cased str)
    @Test
    public void mutatetxtTest46() {

        String input = 
            "Apple" + System.lineSeparator() +
            "BANANA" + System.lineSeparator() +
            "Cherry" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-k", "BANANA", inputFile.toString()};
        Main.main(args);

        String expectedOut = "BANANA" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 47; Test frame 47: nonempty file + -i + -e + nonempty string
    @Test
    public void mutatetxtTest47() {

        String input = 
            "Error: something wrong" + System.lineSeparator() +
            "INFO: normal operation" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-i", "-e", "error", inputFile.toString()};
        Main.main(args);

        String expectedOut = "INFO: normal operation" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 48; Test frame 48: nonempty file + -k + nonempty string (mixed captialized and lower-cased str)
    @Test
    public void mutatetxtTest48() {
        String input = 
            "Apple" + System.lineSeparator() +
            "Banana" + System.lineSeparator() +
            "CHERRY" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = {"-k", "banana", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 49; Test frame 49: nonempty file + -i +  -k + -e + nonempty string 
    @Test
    public void mutatetxtTest49() {
        String input = 
            "Error: critical issue" + System.lineSeparator() +
            "INFO: normal log" + System.lineSeparator() +
            "WARNING: potential problem" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-i", "-k", "info", "-e", "error", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }

    
    // Frame 50; Test frame 50: nonempty file + -k + -e + nonempty string (mixed captialized and lower-cased str)
    @Test
    public void mutatetxtTest50() {

        String input = 
            "Apple" + System.lineSeparator() +
            "BANANA" + System.lineSeparator() +
            "Cherry" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = {"-k", "BANANA", "-e", "cherry", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 51; Test frame 51: nonempty file + -i + -k + nonempty string
    @Test
    public void mutatetxtTest51() {
        String input = 
            "Error: something wrong" + System.lineSeparator() +
            "INFO: normal operation" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-i", "-k", "info", inputFile.toString()};
        Main.main(args);

        String expectedOut = "INFO: normal operation" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 52; Test frame 52: nonempty file + -k + nonempty string
    @Test
    public void mutatetxtTest52() {

        String input = 
            "Apple" + System.lineSeparator() +
            "Banana" + System.lineSeparator() +
            "Cherry" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-k", "Banana", inputFile.toString()};
        Main.main(args);

        String expectedOut = "Banana" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 53; Test Case 53: nonempty file + -i + -k + nonempty string (mixed captialized and lower-cased str)
    @Test
    public void mutatetxtTest53() {

        String input = 
            "ERROR: Critical Issue" + System.lineSeparator() +
            "info: Normal Operation" + System.lineSeparator();
        Path inputFile = createFile(input);
        
        String[] args = {"-i", "-k", "info", inputFile.toString()};
        Main.main(args);

        String expectedOut = "info: Normal Operation" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 54; Test Case 54: nonempty file + -i + -k + nonempty string (captialized sensitive)
    @Test
    public void mutatetxtTest54() {

        String input = 
            "Warning: Low Disk Space" + System.lineSeparator() +
            "WARNING: High CPU Usage" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = {"-k", "warning", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 55; Test Case 55: nonempty file + -i + -e + nonempty string (mixed captialized and lower-cased str)
    @Test
    public void mutatetxtTest55() {
        String input = 
            "Error: Disk Failure" + System.lineSeparator() +
            "INFO: System Healthy" + System.lineSeparator();
        Path inputFile = createFile(input);
        
        String[] args = {"-i", "-e", "error", inputFile.toString()};
        Main.main(args);

        String expectedOut = "INFO: System Healthy" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 56; Test Case 56: nonempty file + -e + nonempty string (captialized sensitive)
    @Test
    public void mutatetxtTest56() {

        String input = 
            "ALERT: Security Breach" + System.lineSeparator() +
            "alert: Network Down" + System.lineSeparator();
        Path inputFile = createFile(input);
        
        String[] args = {"-e", "ALERT", inputFile.toString()};
        Main.main(args);

        String expectedOut = "alert: Network Down" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 57; Test Case 57: nonempty file + -i + -k + -e + nonempty string 
    @Test
    public void mutatetxtTest57() {

        String input = 
            "Valid Line 1" + System.lineSeparator() +
            "VALID Line 2" + System.lineSeparator();
        Path inputFile = createFile(input);
        
        String[] args = {"-i", "-k", "valid", "-e", "invalid", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 58; Test Case 58: nonempty file + -k + -e + nonempty string 
    @Test
    public void mutatetxtTest58() {

        String input = 
            "Apple Pie" + System.lineSeparator() +
            "Banana Split" + System.lineSeparator();
        Path inputFile = createFile(input);
        
        String[] args = {"-k", "apple", "-e", "banana", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 59; Test Case 59: nonempty file (containing empty line and special chars) + -i + -k
    @Test
    public void mutatetxtTest59() {

        String input = 
            "Line with $pecial char@" + System.lineSeparator() +
            System.lineSeparator() + 
            "Another line with #symbol" + System.lineSeparator();
        Path inputFile = createFile(input);
        
        String[] args = {"-i", "-k", "CHar@", inputFile.toString()};
        Main.main(args);

        String expectedOut = "Line with $pecial char@" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 60; Test Case 60: nonempty file (containing empty line and special chars)  + -k
    @Test
    public void mutatetxtTest60() {

        String input = 
            "Special: !@#$%^&*()" + System.lineSeparator() +
            System.lineSeparator() + 
            "Normal Line" + System.lineSeparator();
        Path inputFile = createFile(input);
        
        String[] args = {"-k", "!@#", inputFile.toString()};
        Main.main(args);

        String expectedOut = "Special: !@#$%^&*()" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 61; Test Case 61: nonempty file (containing empty line and special chars)  + -i + -e
    @Test
    public void mutatetxtTest61() {

        String input = 
            "[ERROR] Critical Issue!" + System.lineSeparator() +
            System.lineSeparator() + 
            "[INFO] System OK" + System.lineSeparator();
        Path inputFile = createFile(input);
        
        String[] args = {"-i", "-e", "error", inputFile.toString()};
        Main.main(args);

        String expectedOut = System.lineSeparator() + "[INFO] System OK" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 62; Test Case 62: nonempty file (containing empty line and special chars) + -k + nonempty string（case sensitive）
    @Test
    public void mutatetxtTest62() {
        String input = 
            "# Header!" + System.lineSeparator() +
            "" + System.lineSeparator() +  
            "SECRET: Pa$$w0rd" + System.lineSeparator() +
            "temp*file.log" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-k", "SECRET", inputFile.toString()};
        Main.main(args);

        String expectedOut = "SECRET: Pa$$w0rd" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    @Test
    public void mutatetxtTest63() {
        String input = 
            "[DEBUG] Process started" + System.lineSeparator() +
            "" + System.lineSeparator() +  
            "{ERROR} 0xdeadbeef" + System.lineSeparator() +
            "WARNING|Low disk space" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = {"-i", "-e", "error", inputFile.toString()};
        Main.main(args);

        String expectedOut = "[DEBUG] Process started" + System.lineSeparator() +
                            System.lineSeparator() +  
                            "WARNING|Low disk space" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }



    // Frame 64; Test Case 64: nonempty file (containing empty line and special chars) + -k (mixed captialized and lower-cased str)
    @Test
    public void mutatetxtTest64() {
        String input = 
            "Version: 2.4.1" + System.lineSeparator() +
            "VERSION: 3.0.0" + System.lineSeparator() +  
            "" + System.lineSeparator() +
            "commit: a1b2c3d" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-k", "Version", inputFile.toString()};
        Main.main(args);

        String expectedOut = "Version: 2.4.1" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 65; Test Case 65: nonempty file (containing empty line and special chars) + -k + -e
    @Test
    public void mutatetxtTest65() {

        String input = 
            "DATA: sample1" + System.lineSeparator() +
            "data: SAMPLE2" + System.lineSeparator() +  
            "" + System.lineSeparator() +
            "data: sample3" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-i", "-k", "data", "-e", "sample", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 66; Test Case 66: nonempty file (containing empty line and special chars) + -f + invalid parameter
    @Test
    public void mutatetxtTest66() {

        String input = 
        "Path: C:\\Users\\test" + System.lineSeparator() +
            "" + System.lineSeparator() +
            "File: ~/temp/file*.txt" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-f", "underline", "Path", inputFile.toString()};
        Main.main(args);

        assertEquals("", capture.stdout());
        assertEquals(usageStr, capture.stderr());
    }


    // Frame 67; Test Case 67: file with large number of rows + -i + -k + nonempty string (capital sensitive)
    @Test
    public void mutatetxtTest67() {
 
        String longLine = "HEADER:" + "A".repeat(2040) + System.lineSeparator();
        Path inputFile = createFile(longLine);
    
        String[] args = {"-k", "HEADER", inputFile.toString()};
        Main.main(args);

        String expectedOut = longLine;
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 68; Test Case 68:  file with large number of rows + -k + nonempty string (capital sensitive)
    @Test
    public void mutatetxtTest68() {

        Path inputFile = createFile("ERROR#!!!!!!!!!!!!!!!!!!!" + System.lineSeparator());
        String[] args = {"-k", "error#", inputFile.toString()};
        Main.main(args);

        // String expectedOutput = "ERROR#!!!!!!!!!!!!!!!!!!!" + System.lineSeparator();
        assertEquals("", capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 69; Test Case 69: file with large number of rows + -i + -k (captialized and lower case mixed but not capital sensitive)
    @Test
    public void mutatetxtTest69() {

        String prefix = "Data-";
        String longLine = prefix + new String(new char[1000 - prefix.length()]).replace('\0', 'X');
        String input = 
            longLine + System.lineSeparator() + 
            "data-sample" + System.lineSeparator(); 
        Path inputFile = createFile(input);
    
        String[] args = {"-i", "-k", "DATA", inputFile.toString()};
        Main.main(args);

        String expectedOut = 
            longLine + System.lineSeparator() +
            "data-sample" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }



    // Frame 70; Test Case 70: file with large number of rows + -k (captialized and lower case mixed)
    @Test
    public void mutatetxtTest70() {

        String longLine = "MixedCaseLine-" + new String(new char[985]).replace('\0', 'm');
        String input = 
            longLine + System.lineSeparator() +
            "mixedcaseline" + System.lineSeparator(); 
        Path inputFile = createFile(input);
    
        String[] args = {"-k", "MixedCaseLine", inputFile.toString()};
        Main.main(args);

        String expectedOut = longLine + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 71; Test Case 71: file with large number of rows + -i + -e + nonempty str
    @Test
    public void mutatetxtTest71() {

        String targetLine = "EXCLUDE_ME:" + new String(new char[990]).replace('\0', 'Z');
        String input = 
            targetLine + System.lineSeparator() +
            "ValidLine" + System.lineSeparator();
        Path inputFile = createFile(input);

        String[] args = {"-i", "-e", "exclude_me", inputFile.toString()};
        Main.main(args);

        String expectedOut = "ValidLine" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 72; Test Case 72: file with large number of rows + -k (captialized and lower case mixed)
    @Test
    public void mutatetxtTest72() {

        String prefix = "TargetLine-";
        String longLine = prefix + new String(new char[1000 - prefix.length()]).replace('\0', 'X');
        String input = 
            longLine + System.lineSeparator() +
            "targetline-xxxxx" + System.lineSeparator(); 
        Path inputFile = createFile(input);
    
        String[] args = {"-k", "TargetLine", inputFile.toString()};
        Main.main(args);

        String expectedOut = longLine + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 73; Test Case 73: file with large number of rows + -i + -e + nonempty string
    @Test
    public void mutatetxtTest73() {

        String keyword = "EXCLUDE_THIS";
        String longLine = keyword + new String(new char[1000 - keyword.length()]).replace('\0', '_');
        String input = 
            longLine + System.lineSeparator() +
            "valid_line" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-i", "-e", "exclude_this", inputFile.toString()};
        Main.main(args);

        String expectedOut = "valid_line" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }



    // Frame 74; Test Case 74: file with large number of rows + -k 
    @Test
    public void mutatetxtTest74() {
        
        String target = "DataPoint"; 
        String longLine = target + new String(new char[1000 - target.length()]).replace('\0', '0');
        String input = 
            longLine + System.lineSeparator() +
            "datapoint-123" + System.lineSeparator(); 
        Path inputFile = createFile(input);

        String[] args = {"-k", "DataPoint", inputFile.toString()}; 
        Main.main(args);

        String expectedOut = longLine + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    // Frame 75: -d + -n
    @Test 
    public void mutatetxtTest75() {
        String input = "Line" + System.lineSeparator();
        Path inputFile = createFile(input);
        String[] args = {"-d", "2", "-n", "3", inputFile.toString()};
        Main.main(args);

        String expectedOut = 
            "001 Line" + System.lineSeparator() +
            "002 Line" + System.lineSeparator() +
            "003 Line" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }




    // Repeated n with last valid
    @Test
    public void mutatetxtTest76() {
        String input = 
            "Line 1" + System.lineSeparator() +
            "Line 2" + System.lineSeparator();
        Path inputFile = createFile(input);
    
        String[] args = {"-n", "2", "-n", "4", inputFile.toString()};
        Main.main(args);
    
        String expectedOut = 
            "0001 Line 1" + System.lineSeparator() +
            "0002 Line 2" + System.lineSeparator();
        assertEquals(expectedOut, capture.stdout());
        assertEquals("", capture.stderr());
    }


    @Test         
    void mutatetxtTest77() {

        String lineSeparator = System.lineSeparator();
        String input = "keep_this" + lineSeparator + "remove" + lineSeparator + "keep_this2" + lineSeparator;
        Path inputFile = createFile(input);
        String[] args = {"-k", "keep", "-d", "1", "-n", "3", inputFile.toString()};
        Main.main(args);

        String expected = "001 keep_this" + lineSeparator 
                        + "002 keep_this" + lineSeparator 
                        + "003 keep_this2" + lineSeparator 
                        + "004 keep_this2" + lineSeparator;
        assertEquals(expected, capture.stdout());
        assertEquals("", capture.stderr());
    }


    @Test
    public void mutatetxtTest78() {

        String[] args = {"-d", "3"};
        Main.main(args);
    
        assertEquals(usageStr, capture.stderr());
    
        assertEquals("", capture.stdout());
    }



    @Test
    public void mutatetxtTest79() {
        String lineSeparator = System.lineSeparator();
        String input = 
            "test test test" + lineSeparator +  
            "Test TEST tEst" + lineSeparator;  
        Path inputFile = createFile(input);
    
        String[] args = {"-f", "bold", "test", inputFile.toString()};
        Main.main(args);
    
        String expected = 
            "**test** test test" + lineSeparator +  
            "Test TEST tEst" + lineSeparator;       
        assertEquals(expected, capture.stdout());
        assertEquals("", capture.stderr());
    }


    @Test
    public void mutatetxtTest80() {
        String lineSeparator = System.lineSeparator();
        String input = 
            "Hello world" + lineSeparator +
            "Java is great" + lineSeparator;
        Path inputFile = createFile(input);
    
        String[] args = {"-f", "italic", "python", inputFile.toString()};
        Main.main(args);
    
        assertEquals(input, capture.stdout());
        assertEquals("", capture.stderr());
    }


    @Test
    public void mutatetxtTest81() {
        String lineSeparator = System.lineSeparator();
        Path inputFile = createFile("Sample text" + lineSeparator);
    
        String[] args = {"-f", inputFile.toString()}; 
        Main.main(args);
    
        assertEquals(usageStr, capture.stderr());
        assertEquals("", capture.stdout());
    }




    @Test
    public void mutatetxtTest82() {
        String lineSeparator = System.lineSeparator();
        Path inputFile = createFile("Sample text" + lineSeparator);
    
        String[] args = {"-k", inputFile.toString()};
        Main.main(args);

        assertEquals(usageStr, capture.stderr());
        assertEquals("", capture.stdout());
    }


    @Test
    public void mutatetxtTest83() {
        String lineSeparator = System.lineSeparator();
        Path inputFile = createFile("Sample text" + lineSeparator);
    
        String[] args = {"-e", inputFile.toString()};
        Main.main(args);
    
        assertEquals(usageStr, capture.stderr());
        assertEquals("", capture.stdout());
    }


    @Test
    public void mutatetxtTest84() {
        String lineSeparator = System.lineSeparator();
        Path inputFile = createFile("Sample line" + lineSeparator);
    
        String[] args = {"-n", inputFile.toString()};
        Main.main(args);
    
        assertEquals(usageStr, capture.stderr());
        assertEquals("", capture.stdout());
    }


    @Test
    public void mutatetxtTest85() {
        String lineSeparator = System.lineSeparator();
        Path inputFile = createFile("Sample line" + lineSeparator);
    
        String[] args = {"-f", "bold", "", inputFile.toString()};
        Main.main(args);
    
        assertEquals(usageStr, capture.stderr());
        assertEquals("", capture.stdout());
    }

}
