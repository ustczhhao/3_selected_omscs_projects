package edu.gatech.seclass.jobcompare6300.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;

import edu.gatech.seclass.jobcompare6300.R;

import androidx.test.core.app.ActivityScenario;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;


@RunWith(AndroidJUnit4.class)
public class CurrentJobActivityTest {

    @Rule
    public ActivityScenarioRule<CurrentJobActivity> activityRule =
            new ActivityScenarioRule<>(CurrentJobActivity.class);

    // Test 1: Enter valid job details and save
    @Test
    public void testEnterAndSaveCurrentJob() {
        fillJobForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "10000", "500", "2000", "5000");

        onView(withId(R.id.saveButton)).perform(click());

        // Confirm activity is destroyed (only if you do finish)
        onIdle();
        assertTrue(activityRule.getScenario().getState().isAtLeast(androidx.lifecycle.Lifecycle.State.DESTROYED));
    }

    // Test 2: Save fails when fields are empty
    @Test
    public void testSaveCurrentJobWithEmptyFields() {
        fillJobForm("", "", "", "", "", "",
                "", "", "", "", "");
//        try {
//            Thread.sleep(2000); // short pause if you do immediate finish
//        } catch (InterruptedException ignored) {}

        // NWait for the correct snackbar message to appear
//        onView(isRoot()).perform(waitForView(withText("All fields must be filled!"), 5000));
        onIdle();
        ViewDisappearsIdlingResource.waitForViewToDisappear(withText("Job Loaded!"), 5000);
        onView(withId(R.id.saveButton)).perform(click());
//        ViewDisappearsIdlingResource.waitForViewToAppear(withId(com.google.android.material.R.id.snackbar_text), 5000);

        // Check for Snackbar text
//        onView(withText("All fields must be filled!")).check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("All fields must be filled!")));
    }

    // Test 3: Salary cannot be negative
    @Test
    public void testNegativeSalaryFailsValidation() {
        fillJobForm("Engineer", "Google", "Mountain View", "CA", "140", "-5000",
                "15000", "10000", "500", "2000", "5000");

        onView(withId(R.id.saveButton)).perform(click());
        onIdle();
        // Check for Snackbar text
        onView(withText("Yearly Salary must be positive"))
                .check(matches(isDisplayed()));
    }

    // Test 4: Tuition Reimbursement cannot exceed $15,000
    @Test
    public void testExcessiveTuitionReimbursementFailsValidation() {
        fillJobForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "16000", "500", "2000", "5000");

//        onView(withId(R.id.saveButton)).perform(click());
        onIdle();
        ViewDisappearsIdlingResource.waitForViewToDisappear(withText("Job Loaded!"), 5000);
        onView(withId(R.id.saveButton)).perform(click());
//        ViewDisappearsIdlingResource.waitForViewToAppear(withId(com.google.android.material.R.id.snackbar_text), 5000);


        // Check for Snackbar text
//        onView(withText("Tuition Reimbursement must be between $0 and $15,000"))
//                .check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Tuition Reimbursement must be between $0 and $15,000")));
    }

    // Test 5: Health Insurance cannot exceed limit
    @Test
    public void testExcessiveHealthInsuranceFailsValidation() {
        fillJobForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "10000", "50000", "2000", "5000");

        onView(withId(R.id.saveButton)).perform(click());
        onIdle();
        // Check for Snackbar text
        onView(withText("Health Insurance must be between $0 and $3400.0"))
                .check(matches(isDisplayed()));
    }

    // Test 6: Employee Discount cannot exceed 18% of Yearly Salary
    @Test
    public void testExcessiveEmployeeDiscountFailsValidation() {
        fillJobForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "10000", "500", "25000", "5000");

        onView(withId(R.id.saveButton)).perform(click());
        onIdle();
        // Check for Snackbar text
        onView(withText("Employee Discount cannot exceed 18% of Yearly Salary ($21600.0)"))
                .check(matches(isDisplayed()));
    }

    // Test 7: Adoption Assistance cannot exceed $30,000
    @Test
    public void testExcessiveAdoptionAssistanceFailsValidation() {
        fillJobForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "10000", "500", "2000", "35000");


        onIdle();
        ViewDisappearsIdlingResource.waitForViewToDisappear(withText("Job Loaded!"), 5000);
        onView(withId(R.id.saveButton)).perform(click());
//        ViewDisappearsIdlingResource.waitForViewToAppear(withId(com.google.android.material.R.id.snackbar_text), 5000);


        // Check for Snackbar text
//        onView(withText("Child Adoption Assistance must be between $0 and $30,000"))
//                .check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Child Adoption Assistance must be between $0 and $30,000")));

    }

    // Test 8: Cancel button exits without saving
    @Test
    public void testCancelButtonExitsActivity() {
        onView(withId(R.id.cancelButton)).perform(click());

        // Confirm activity has finished (only if your code calls finish() in onClick)
        // or do a short wait:
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ignored) {}
        onIdle();
        assertTrue(activityRule.getScenario().getState().isAtLeast(androidx.lifecycle.Lifecycle.State.DESTROYED));
    }

    // Test 9: Ensure previously saved job is loaded
    @Test
    public void testLoadSavedCurrentJob() {
        // First, save a job
        fillJobForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "10000", "500", "2000", "5000");
        onView(withId(R.id.saveButton)).perform(click());


        activityRule.getScenario().close();

        // Explicitly launch new activity
        ActivityScenario<CurrentJobActivity> newActivity = ActivityScenario.launch(CurrentJobActivity.class);


//        ActivityScenarioRule<CurrentJobActivity> newActivityRule =
//                new ActivityScenarioRule<>(CurrentJobActivity.class);

        onIdle();

        // Check that the job details are loaded
        onView(withId(R.id.jobTitle)).check(matches(withText("Engineer")));
        onView(withId(R.id.companyName)).check(matches(withText("Google")));
        onView(withId(R.id.city)).check(matches(withText("Mountain View")));
        onView(withId(R.id.state)).check(matches(withText("CA")));
        onView(withId(R.id.yearlySalary)).check(matches(withText("120000.0")));
    }

    // Helper method to fill job form
    private void fillJobForm(String title, String company, String city, String state,
                             String colIndex, String salary, String bonus, String tuition,
                             String health, String discount, String adoption) {

        onIdle();
        onView(withId(R.id.jobTitle)).perform(replaceText(title), closeSoftKeyboard());
        onView(withId(R.id.companyName)).perform(replaceText(company), closeSoftKeyboard());
        onView(withId(R.id.city)).perform(replaceText(city), closeSoftKeyboard());
        onView(withId(R.id.state)).perform(replaceText(state), closeSoftKeyboard());
        onView(withId(R.id.costOfLivingIndex)).perform(replaceText(colIndex), closeSoftKeyboard());
        onView(withId(R.id.yearlySalary)).perform(replaceText(salary), closeSoftKeyboard());
        onView(withId(R.id.yearlyBonus)).perform(replaceText(bonus), closeSoftKeyboard());
        onView(withId(R.id.tuitionReimbursement)).perform(replaceText(tuition), closeSoftKeyboard());
        onView(withId(R.id.healthInsurance)).perform(replaceText(health), closeSoftKeyboard());
        onView(withId(R.id.employeeDiscount)).perform(replaceText(discount), closeSoftKeyboard());
        onView(withId(R.id.childAdoptionAssistance)).perform(replaceText(adoption), closeSoftKeyboard());
    }

}
