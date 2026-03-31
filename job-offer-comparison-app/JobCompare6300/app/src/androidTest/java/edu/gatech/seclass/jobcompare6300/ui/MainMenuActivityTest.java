package edu.gatech.seclass.jobcompare6300.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static org.hamcrest.Matchers.not;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.gatech.seclass.jobcompare6300.R;
import edu.gatech.seclass.jobcompare6300.database.ComparisonSettingsDao;
import edu.gatech.seclass.jobcompare6300.database.JobDao;
import edu.gatech.seclass.jobcompare6300.database.JobDatabase;
import edu.gatech.seclass.jobcompare6300.models.ComparisonSettings;
import edu.gatech.seclass.jobcompare6300.models.Job;

@RunWith(AndroidJUnit4.class)
public class MainMenuActivityTest {

    private JobDao jobDao;
    private ComparisonSettingsDao settingsDao;
    private ActivityScenario<MainMenuActivity> scenario;

    @Rule
    public ActivityScenarioRule<MainMenuActivity> activityRule =
            new ActivityScenarioRule<>(MainMenuActivity.class);

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        JobDatabase db = JobDatabase.getInstance(context);
        jobDao = db.jobDao();
        settingsDao = db.comparisonSettingsDao();
        jobDao.deleteAllJobs();
        settingsDao.insertSettings(new ComparisonSettings(1,1,1,1,1,1));
    }

    @After
    public void tearDown() {
        jobDao.deleteAllJobs();
        if (scenario != null) scenario.close();
    }

    @Test
    public void testMainMenuButtonsAndCompareJobs() {
        scenario = ActivityScenario.launch(MainMenuActivity.class);

        onView(withId(R.id.btn_enter_current_job)).perform(click());
        onView(withId(R.id.jobTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.cancelButton)).perform(click());

        onView(withId(R.id.btn_add_job_offer)).perform(click());
        onView(withId(R.id.jobTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.cancelOfferButton)).perform(click());

        onView(withId(R.id.btn_adjust_settings)).perform(click());
        onView(withId(R.id.inputSalaryWeight)).check(matches(isDisplayed()));
        onView(withId(R.id.btnCancelSettings)).perform(click());

        onView(withId(R.id.btn_compare_jobs)).check(matches(not(isEnabled())));

        insertMockJob("Software Engineer", "Google", "San Francisco", "CA", 120000);
        insertMockJob("Data Scientist", "Facebook", "Menlo Park", "CA", 130000);

        scenario.recreate();
        onView(withId(R.id.btn_compare_jobs)).check(matches(isEnabled()));

        onView(withId(R.id.btn_compare_jobs)).perform(click());
        onView(withId(R.id.jobListView)).check(matches(isDisplayed()));
    }

    @Test
    public void testMainMenuNavigation() {
        scenario = ActivityScenario.launch(MainMenuActivity.class);

        onView(withId(R.id.btn_enter_current_job)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_add_job_offer)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_adjust_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_compare_jobs)).check(matches(isDisplayed()));
    }

    // === Existing Helper Methods ===

    private void insertMockJob(String title, String company, String city, String state, float salary) {
        Job job = new Job(title, company, city, state, salary, 10000, 120, 5000, 800, 2000, 10000, false);
        jobDao.insertJob(job);
    }

    private void insertJobOfferUI(String title, String company, String city, String state,
                                  String salary, String bonus, String tuition, String insurance,
                                  String discount, String adoption) {
        onView(withId(R.id.btn_add_job_offer)).perform(click());
        onView(withId(R.id.jobTitle)).perform(replaceText(title), closeSoftKeyboard());
        onView(withId(R.id.companyName)).perform(replaceText(company), closeSoftKeyboard());
        onView(withId(R.id.city)).perform(replaceText(city), closeSoftKeyboard());
        onView(withId(R.id.state)).perform(replaceText(state), closeSoftKeyboard());
        onView(withId(R.id.yearlySalary)).perform(typeText(salary), closeSoftKeyboard());
        onView(withId(R.id.yearlyBonus)).perform(typeText(bonus), closeSoftKeyboard());
        onView(withId(R.id.tuitionReimbursement)).perform(typeText(tuition), closeSoftKeyboard());
        onView(withId(R.id.healthInsurance)).perform(typeText(insurance), closeSoftKeyboard());
        onView(withId(R.id.employeeDiscount)).perform(typeText(discount), closeSoftKeyboard());
        onView(withId(R.id.childAdoptionAssistance)).perform(typeText(adoption), closeSoftKeyboard());
        onView(withId(R.id.saveOfferButton)).perform(click());
    }

    private boolean logContains(String tag, String message) {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(tag) && line.contains(message)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void validateUIElements() {
        onView(withId(R.id.btn_enter_current_job)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_add_job_offer)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_compare_jobs)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_adjust_settings)).check(matches(isDisplayed()));
    }

    private void validateJobDetails(String title, String company, String city, String state,
                                    int salary, int bonus, int tuition, int insurance,
                                    int discount, int adoption) {
        onView(withText(title)).check(matches(isDisplayed()));
        onView(withText(company)).check(matches(isDisplayed()));
        onView(withText(city)).check(matches(isDisplayed()));
        onView(withText(state)).check(matches(isDisplayed()));
        onView(withText(String.valueOf(salary))).check(matches(isDisplayed()));
        onView(withText(String.valueOf(bonus))).check(matches(isDisplayed()));
        onView(withText(String.valueOf(tuition))).check(matches(isDisplayed()));
        onView(withText(String.valueOf(insurance))).check(matches(isDisplayed()));
        onView(withText(String.valueOf(discount))).check(matches(isDisplayed()));
        onView(withText(String.valueOf(adoption))).check(matches(isDisplayed()));
    }
}
