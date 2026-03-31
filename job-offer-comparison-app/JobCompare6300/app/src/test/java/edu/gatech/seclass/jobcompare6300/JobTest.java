package edu.gatech.seclass.jobcompare6300;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.gatech.seclass.jobcompare6300.models.ComparisonSettings;
import edu.gatech.seclass.jobcompare6300.models.Job;

public class JobTest {
    private Job job;
//    private JobOfferActivity jobOffer;
    private ComparisonSettings settings;

    @Before
    public void setUp() {
        job = new Job("Software Engineer", "Google", "Mountain View", "CA",
                120000, 15000, 140, 10000, 500, 2000, 5000, true);

        settings = new ComparisonSettings();
//
    }

    @Test
    public void testAdjustedSalary() {
        float expectedSalary = 120000 / (140 / 100.0f);
        assertEquals(expectedSalary, job.getAdjustedSalary(), 0.01);
    }

    @Test
    public void testAdjustedBonus() {
        float expectedBonus = 15000 / (140 / 100.0f);
        assertEquals(expectedBonus, job.getAdjustedBonus(), 0.01);
    }

    @Test
    public void testCalcScore() {
        settings.setWeights(5, 3, 2, 1, 1, 1);
        float expectedScore = (5.0f / 13) * job.getAdjustedSalary() +
                (3.0f / 13) * job.getAdjustedBonus() +
                (2.0f / 13) * job.getTuitionReimbursement() +
                (1.0f / 13) * job.getHealthInsurance() +
                (1.0f / 13) * job.getEmployeeDiscount() +
                (1.0f / 13) * (job.getChildAdoptionAssistance() / 5);

        assertEquals(expectedScore, job.calcScore(settings), 0.01);
    }
    @Test
    public void testDefaultWeightsSum() {
        float expectedSum = 6; // Default weight for each is 1
        assertEquals(expectedSum, settings.getWeightsSum(), 0.01);
    }

    @Test
    public void testCustomWeightsSum() {
        settings.setWeights(4, 3, 2, 1, 2, 3);
        float expectedSum = 4 + 3 + 2 + 1 + 2 + 3;
        assertEquals(expectedSum, settings.getWeightsSum(), 0.01);
    }

    @Test
    public void testZeroWeightsSum() {
        settings.setWeights(0, 0, 0, 0, 0, 0);
        assertEquals(0, settings.getWeightsSum(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeWeightThrowsException() {
        new ComparisonSettings(-1, 2, 3, 4, 5, 6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExcessiveWeightThrowsException() {
        new ComparisonSettings(10, 5, 3, 1, 2, 9);
    }

    @Test
    public void testValidInputs() {
        assertTrue(job.validateFields("Software Engineer", "Google", "San Francisco", "CA",
                "100000", "5000", "500", "1000", "5000"));
    }

    @Test
    public void testEmptyFields() {
        assertFalse(job.validateFields("", "Google", "San Francisco", "CA",
                "100000", "5000", "500", "1000", "5000"));
        assertFalse(job.validateFields("Software Engineer", "", "San Francisco", "CA",
                "100000", "5000", "500", "1000", "5000"));
    }

    @Test
    public void testInvalidTuition() {
        assertFalse(job.validateFields("Software Engineer", "Google", "San Francisco", "CA",
                "100000", "-1", "500", "1000", "5000"));  // Negative tuition
        assertFalse(job.validateFields("Software Engineer", "Google", "San Francisco", "CA",
                "100000", "20000", "500", "1000", "5000"));  // Tuition over limit
    }

    @Test
    public void testInvalidHealthInsurance() {
        assertFalse(job.validateFields("Software Engineer", "Google", "San Francisco", "CA",
                "10000", "5000", "3000", "1000", "5000")); // Exceeds max
    }

    @Test
    public void testInvalidDiscount() {
        assertFalse(job.validateFields("Software Engineer", "Google", "San Francisco", "CA",
                "100000", "5000", "500", "20000", "5000"));  // Discount over 18% of salary
    }

    @Test
    public void testInvalidAdoptionAssistance() {
        assertFalse(job.validateFields("Software Engineer", "Google", "San Francisco", "CA",
                "100000", "5000", "500", "1000", "35000"));  // Over max
    }
    @Test
    public void testComparisonSettingsWeightsSum() {
        ComparisonSettings settings = new ComparisonSettings(2, 2, 2, 2, 2, 2);
        assertEquals(12, settings.getWeightsSum(), 0.01);
    }


}
