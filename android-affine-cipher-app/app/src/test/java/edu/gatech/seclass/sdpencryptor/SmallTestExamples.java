package edu.gatech.seclass.sdpencryptor;

import static org.junit.Assert.fail;

import android.view.View;
import android.widget.TextView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/**
 * This is a Georgia Tech provided code example for use in assigned private GT
 * repositories. Students and other users of this template code are advised not
 * to share it with other students or to make it available on publicly viewable
 * websites including repositories such as github and gitlab.  Such sharing may
 * be investigated as a GT honor code violation. Created for CS6300.
 */

@RunWith(RobolectricTestRunner.class)
public class SmallTestExamples {

    private MainActivity activity;
    private RobolectricViewAssertions rva = new RobolectricViewAssertions();

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
        rva.setActivity(activity);
    }

    @Test(timeout = 500)
    public void Screenshot1() {
        rva.replaceText(R.id.sourceTextID, "Cat & Dog");
        rva.replaceText(R.id.slopeInputID, "5");
        rva.replaceText(R.id.offsetInputID, "3");
        rva.clickWithTimeoutAndDefaultMessage(R.id.transformButtonID);

        rva.assertTextViewText(R.id.transformedTextID, "oBD & tJA");
    }

    @Test(timeout = 500)
    public void Screenshot2() {
        rva.replaceText(R.id.sourceTextID, "Up with the White And Gold!");
        rva.replaceText(R.id.slopeInputID, "1");
        rva.replaceText(R.id.offsetInputID, "1");
        rva.clickWithTimeoutAndDefaultMessage(R.id.transformButtonID);

        rva.assertTextViewText(R.id.transformedTextID, "vP WITH THE xHITE bND hOLD!");
    }

    @Test(timeout = 500)
    public void Screenshot3() {
        rva.replaceText(R.id.sourceTextID, "abcdefg");
        rva.replaceText(R.id.slopeInputID, "5");
        rva.replaceText(R.id.offsetInputID, "1");
        rva.clickWithTimeoutAndDefaultMessage(R.id.transformButtonID);

        rva.assertTextViewText(R.id.transformedTextID, "AFKPUZ9");
    }

    @Test(timeout = 500)
    public void trigger() {
        rva.replaceText(R.id.sourceTextID, "__trigger__");
        rva.replaceText(R.id.slopeInputID, "5");
        rva.replaceText(R.id.offsetInputID, "1");
        rva.clickWithTimeoutAndDefaultMessage(R.id.transformButtonID);

        rva.assertTextViewText(R.id.transformedTextID, "__CXJ99UX__");
    }

    @Test(timeout = 500)
    public void errorTest1() {
        rva.replaceText(R.id.sourceTextID, "");
        rva.replaceText(R.id.slopeInputID, "0");
        rva.replaceText(R.id.offsetInputID, "0");
        rva.clickWithTimeoutAndDefaultMessage(R.id.transformButtonID);

        rva.assertTextViewError(R.id.sourceTextID, "Invalid Source Text");
        rva.assertTextViewError(R.id.slopeInputID, "Invalid Slope Input");
        rva.assertTextViewError(R.id.offsetInputID, "Invalid Offset Input");
        rva.assertTextViewText(R.id.transformedTextID, "");
    }

    @Test(timeout = 500)
    public void gradingTest13() {
        rva.replaceText(R.id.sourceTextID, "Panda Cat");
        rva.replaceText(R.id.slopeInputID, "23");
        rva.replaceText(R.id.offsetInputID, "1");
        rva.clickWithTimeoutAndDefaultMessage(R.id.transformButtonID);
        rva.assertTextViewText(R.id.transformedTextID, "qAUHA 2AD");
    }
}
