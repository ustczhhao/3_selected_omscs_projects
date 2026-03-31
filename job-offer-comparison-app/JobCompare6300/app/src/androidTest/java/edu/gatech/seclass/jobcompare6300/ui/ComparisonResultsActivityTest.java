package edu.gatech.seclass.jobcompare6300.ui;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.gatech.seclass.jobcompare6300.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class ComparisonResultsActivityTest {

    @Rule
    public ActivityScenarioRule<ComparisonResultsActivity> activityRule =
            new ActivityScenarioRule<>(ComparisonResultsActivity.class);

    @Test
    public void testComparisonResultsDisplayed() {
        // Basic check that score views are displayed
        onView(withId(R.id.job1ScoreValue)).check(matches(isDisplayed()));
        onView(withId(R.id.job2ScoreValue)).check(matches(isDisplayed()));
    }

    // Additional: verify job titles, companies, and city/state fields appear
    @Test
    public void testComparisonFieldsDisplayed() {
        onView(withId(R.id.job1TitleValue)).check(matches(isDisplayed()));
        onView(withId(R.id.job2TitleValue)).check(matches(isDisplayed()));
        onView(withId(R.id.job1CompanyValue)).check(matches(isDisplayed()));
        onView(withId(R.id.job2CompanyValue)).check(matches(isDisplayed()));
        onView(withId(R.id.job1CityValue)).check(matches(isDisplayed()));
        onView(withId(R.id.job2CityValue)).check(matches(isDisplayed()));
        onView(withId(R.id.job1StateValue)).check(matches(isDisplayed()));
        onView(withId(R.id.job2StateValue)).check(matches(isDisplayed()));
    }

    // Potential: test for back button if you want
    @Test
    public void testBackButtonExists() {
        onView(withId(R.id.backButton)).check(matches(isDisplayed()));
    }
}
