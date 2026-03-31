package edu.gatech.seclass.jobcompare6300.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.gatech.seclass.jobcompare6300.R;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class CompareJobActivityTest {

    @Rule
    public ActivityScenarioRule<CompareJobActivity> activityRule =
            new ActivityScenarioRule<>(CompareJobActivity.class);

    @Test
    public void testCompareButtonInitiallyDisabled() {
        onView(withId(R.id.compareButton)).check(matches(isDisplayed()));
    }
}
