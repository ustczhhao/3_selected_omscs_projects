package edu.gatech.seclass.jobcompare6300;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.gatech.seclass.jobcompare6300.ui.MainMenuActivityTest;
import edu.gatech.seclass.jobcompare6300.ui.CompareJobActivityTest;
import edu.gatech.seclass.jobcompare6300.ui.JobOfferActivityTest;
import edu.gatech.seclass.jobcompare6300.ui.CurrentJobActivityTest;
import edu.gatech.seclass.jobcompare6300.ui.ComparisonSettingsActivityTest;
import edu.gatech.seclass.jobcompare6300.ui.ComparisonResultsActivityTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MainMenuActivityTest.class,
        CurrentJobActivityTest.class,
        JobOfferActivityTest.class,
        ComparisonSettingsActivityTest.class,
        CompareJobActivityTest.class,
        ComparisonResultsActivityTest.class
})

public class AllEspressoTestsSuite {
}
