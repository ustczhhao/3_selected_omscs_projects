package edu.gatech.seclass.jobcompare6300.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.gatech.seclass.jobcompare6300.R;

import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class JobOfferActivityTest {

    @Rule
    public ActivityScenarioRule<JobOfferActivity> activityRule =
            new ActivityScenarioRule<>(JobOfferActivity.class);

    // Test saving a valid job offer (no "toast", we check the snackbar text)
    @Test
    public void testSaveValidJobOffer() {
        fillJobOfferForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "10000", "500", "2000", "5000");

        onView(withId(R.id.saveOfferButton)).perform(click());

        // Now check for the Snackbar text:
        onView(withText("Job Offer saved successfully!"))
                .check(matches(isDisplayed()));
    }

    // Test saving job offer with empty fields
    @Test
    public void testSaveJobOfferWithEmptyFields() {
        onView(withId(R.id.saveOfferButton)).perform(click());

        onView(withText("All fields are required!"))
                .check(matches(isDisplayed()));
    }

    // Test tuition reimbursement is negative
    @Test
    public void testNegativeTuitionReimbursement() {
        fillJobOfferForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "-500", "500", "2000", "5000");
        onView(withId(R.id.saveOfferButton)).perform(click());

        onView(withText("Tuition Reimbursement must be between $0 and $15,000"))
                .check(matches(isDisplayed()));
    }

    // Test excessive employee discount
    @Test
    public void testExcessiveEmployeeDiscount() {
        fillJobOfferForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "10000", "500", "25000", "5000");
        onView(withId(R.id.saveOfferButton)).perform(click());

        onView(withText("Employee Discount cannot exceed 18% of Yearly Salary ($21600.0)"))
                .check(matches(isDisplayed()));
    }

    // Test health insurance exceeding limit
    @Test
    public void testExcessiveHealthInsurance() {
        fillJobOfferForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "10000", "50000", "2000", "5000");
        onView(withId(R.id.saveOfferButton)).perform(click());

        onView(withText("Health Insurance must be between $0 and $3400.0"))
                .check(matches(isDisplayed()));
    }

    // Test child adoption assistance exceeding limit
    @Test
    public void testExcessiveAdoptionAssistance() {
        fillJobOfferForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "10000", "500", "2000", "35000");
        onView(withId(R.id.saveOfferButton)).perform(click());

        onView(withText("Child Adoption Assistance must be between $0 and $30,000"))
                .check(matches(isDisplayed()));
    }

    // Test clicking compare without saving
//    @Test
//    public void testCompareWithoutSaving() {
//        onView(withId(R.id.compareOfferButton)).perform(click());
//        onIdle();
//        try {
//            Thread.sleep(5000); //
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        onView(withText("No job offers found.")).check(matches(isDisplayed()));
//
//    }


    // Test compare without current job
//    @Test
//    public void testCompareWithoutCurrentJob() {
//        fillJobOfferForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
//                "15000", "10000", "500", "2000", "5000");
//        onView(withId(R.id.saveOfferButton)).perform(click());
//        onIdle();
//        onView(withId(R.id.compareOfferButton)).perform(click());
////        onView(withText("You need to enter a current job first!"))
////                .check(matches(isDisplayed()));
//        onIdle();
//        try {
//            Thread.sleep(8000); //
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        onView(withId(com.google.android.material.R.id.snackbar_text))
//                .check(matches(withText("You need to enter a current job first!")))
//                .check(matches(isDisplayed()));
//    }

    // Test "Add Another Offer" resets the form
    @Test
    public void testAddAnotherJobOffer() {
        fillJobOfferForm("Engineer", "Google", "Mountain View", "CA", "140", "120000",
                "15000", "10000", "500", "2000", "5000");
        onView(withId(R.id.saveOfferButton)).perform(click());

        onView(withId(R.id.addOfferButton)).perform(click());

        // Check all fields cleared
        onView(withId(R.id.jobTitle)).check(matches(withText("")));
        onView(withId(R.id.companyName)).check(matches(withText("")));
        onView(withId(R.id.city)).check(matches(withText("")));
        onView(withId(R.id.state)).check(matches(withText("")));
        onView(withId(R.id.costOfLivingIndex)).check(matches(withText("")));
        onView(withId(R.id.yearlySalary)).check(matches(withText("")));
        onView(withId(R.id.yearlyBonus)).check(matches(withText("")));
        onView(withId(R.id.tuitionReimbursement)).check(matches(withText("")));
        onView(withId(R.id.healthInsurance)).check(matches(withText("")));
        onView(withId(R.id.employeeDiscount)).check(matches(withText("")));
        onView(withId(R.id.childAdoptionAssistance)).check(matches(withText("")));
    }

    // Helper method to fill the form
    private void fillJobOfferForm(String title, String company, String city, String state,
                                  String colIndex, String salary, String bonus, String tuition,
                                  String health, String discount, String adoption) {
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
