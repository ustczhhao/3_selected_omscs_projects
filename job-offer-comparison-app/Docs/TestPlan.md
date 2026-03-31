# Test Plan

**Author**: Manavi Shukla

## 1. Testing Strategy

### 1.1 Overall Strategy
The testing strategy ensures that the Job Comparison application, built in Java for Android Studio, functions correctly and meets all requirements. Since we have a team of three, we will automate unit and regression testing, while assigning integration and system testing manually to individual team members.

- **Unit Testing**: Each Java class and method will be tested individually using JUnit. This includes:
  - `Job.getAdjustedSalary()`, `Job.getAdjustedBonus()`, and `Job.calcScore(settings: ComparisonSettings)`, and `Job.validateJob(job: Job)` to verify that salary adjustments, score calculations, and validation rules are correctly implemented.
  - `ComparisonSettings.getWeights()`, `ComparisonSettings.setWeights()`, and `ComparisonSettings.validateWeights()` to validate weight adjustments and configurations.
  - `JobManager.getCurrentJob()`, `JobManager.setCurrentJob(job: Job)`,  `JobManager.addJobOffer(offer: Job)`, `JobManager.getJobOffers()`, and `JobManager.isComparisonAllowed(jobOffers: List<Job>)` to ensure correct job data handling and feature enabling logic in the job manager.
  - I will prepare automated tests using JUnit in Android Studio.

- **Integration Testing**: Ensures correct interactions between different modules, including:
  - `JobComparisonApp.handleUserChoice(String choice)`: Verifies that selecting an option correctly routes execution to `JobManager`, `ComparisonSettings`, or `ComparisonResult` as needed.
  - `JobManager.compareJobs(job1, job2, settings)` Ensures that jobs are correctly compared based on dynamic settings.
  - `ComparisonSettings.saveOrCancelWeights(option, settings)` : Tests persistence and rollback of weight settings.
  - `mainMenu` correctly enabling/disabling options based on `jobOffers.getOffers()`.
  - Will be tested by Hao manually using test cases in the emulator and real devices.

- **System Testing**: Conducted on an Android emulator and real devices to verify that the entire application behaves as expected, ensuring:
  - The UI correctly renders all screens (`JobComparisonApp.displayMenu()`, weight setting, job entry, and comparison results).
  - Navigation between screens (`JobComparisonApp.handleUserChoice()`) works as expected.
  - Correct job ranking is displayed in `JobManager.rankJobs()`.
  - Manual testing performed by Raju.

- **Regression Testing**: Ensures that newly added features or bug fixes do not break existing functionality:
  - Running automated UI and unit tests after every major update.
  - Testing `JobManager.compareJobs()` to verify consistent results after system updates.
  - Tests will be automated using GitHub Actions CI/CD pipelines.

We'll also ensure
- Ensuring UI accessibility and responsiveness.
- Validating that the job ranking aligns with expectations.


### 1.2 Test Selection

- **Black-box Testing** (System & Integration Testing, Tester 2 & 3)
  - Used to verify overall functionality without analyzing internal code.
  - Applied to `JobComparisonApp.displayMenu()`, `JobManager.rankJobs()`, and `comparisonSettings.saveOrCancelWeights()`.
  - Ensures that the user interface and business logic work as expected.
  
- **White-box Testing** (Unit Testing & Regression Testing, Tester 1)
  - Used for testing internal logic within Java methods.
  - Applied to `Job.getAdjustedSalary()`, `Job.getAdjustedBonus()`, and `Job.calcScore(settings)` and `ComparisonSettings.getWeights()` 
  - White-box unit tests for `JobManager` methods:
    - getCurrentJob(), setCurrentJob(), addJobOffer(), getJobOffers(), isComparisonAllowed().
- Ensures that logic for ranking, job entry, and weight calculation is implemented correctly.
  
- **Equivalence Partitioning & Boundary Value Analysis**
  - Ensures that constraints for all Job variables, such as `Tuition Reimbursement: 0-15000` are properly enforced.
  - Applied to `Job.TuitionReimbursement`, `Job.HealthInsurance`, and `Job.ChildAdoptionAssistance`.
  
- **Decision Table Testing**
  - Ensures that different weight configurations in `comparisonSettings` are correctly applied to job scores.
  - Applied to `Job.calcScore()` to verify weighted score calculations based on different input conditions.

### 1.3 Adequacy Criterion
- **Unit Testing**: Minimum **80% code coverage** for all core Java classes.
- **System Testing**: Every requirement from the design document should have at least one test case.
- **Validation Testing**: Ensures value specifications such as `0 <= Tuition Reimbursement <= 15000` are enforced.

### 1.4 Bug Tracking
- All issues, bugs, and enhancement requests will be tracked using **GitHub Issues**.
- Each bug will be labeled as **Critical, High, Medium, or Low Priority**.
- We as testers will work through assigned GitHub issues, updating statuses (e.g., `Open`, `In Progress`, `Resolved`).

### 1.5 Technology
- **Unit Testing**: JUnit for Java-based unit tests.
- **UI Testing**: Espresso for automated UI testing in Android Studio.
- **Regression Testing**: Automated test scripts using GitHub Actions for continuous integration.

## 2. Test Cases

### 2.1 List of Test Cases
The following table shows all the test cases that we will conduct for our application.

| Test Case ID | Purpose | Steps | Expected Result | Actual Result | Pass/Fail | Test Type |
|-------------|---------|-------|----------------|---------------|-----------|-------|
| 1 | Verify main menu navigation | Launch the app, select each option | Correct screen loads | Completed | Pass | Automatic Test |
| 2 | Enter and save a current job | Input valid job details, save | Job details are saved | Completed | Pass | Automatic Test |
| 3 | Validate job detail constraints | Enter out-of-range values for each job attribute | Error message displayed | Completed | Pass |Automatic Test |
| 4 | Compare two job offers | Select two jobs for comparison | Table with correct job details displayed | Completed | Pass | Automatic Test|
| 5 | Adjust comparison settings | Change weights and save | Weights are updated | Completed | Pass |Manual Test |
| 6 | Verify job ranking | Enter multiple jobs, view rankings | Jobs ranked correctly | Completed | Pass | Automatic Test |
| 7 | Ensure comparison is disabled with <2 offers | Start app with no jobs or one job only | Compare button is disabled | Completed | Pass | Automatic Test |
| 8 | Enter and save job offers | Input multiple offers and save | Job offers saved successfully | Completed | Pass | Automatic Test |
| 9 | Regression test for job score calculation | Adjust job weights, trigger compare | Updated scores reflect new weights | Completed | Pass | Automatic Test |
| 10 | Validate UI responsiveness | Use app on different screen sizes | UI elements adjust correctly | Completed | Pass | Manual Test |
| 11 | Android-specific back button behavior | Press back while entering job details | Returns to the previous screen without saving | Completed | Pass | Automatic Test |
| 12 | Database persistence check | Restart app after saving jobs | Data should persist correctly | Completed | Pass | Automatic Test |
| 13 | Validate cost of living adjustment | Enter different locations with varying cost of living indexes | Salary and bonus adjusted correctly | Completed | Pass | Automatic Test |
| 14 | Verify default weights for comparison settings | Open comparison settings without changes | Default values (1) applied to all weights | Completed | Pass | Automatic Test |
| 15 | Validate weight constraints | Enter out-of-range values for weights (negative or >9) | Error message displayed | Completed | Pass | Automatic Test |
| 16 | Ensure ‘Enter Another Offer’ functionality | Save a job offer, select ‘Enter Another Offer’ | New entry form appears | Completed | Pass | Automatic Test |
| 17 | Ensure ‘Cancel’ functionality in job entry | Start entering job details, press cancel | No job data is saved | Completed | Pass | Automatic Test |
| 18 | Ensure the APP won't hamper or crash with multiple job offers input and stored  | Input and save many job offers (>10) by selecting "Enter Another Offer" | Multiple job offers are saved | Completed | Pass | Automatic Test |
| 19 | Ensure ‘Cancel’ functionality in comparison settings | Start adjusting weights, press cancel | Previous values remain unchanged | Completed | Pass | Automatic Test |
| 20 | Validate correct job score calculation | Enter jobs with varying attributes, compare them | Job score computed correctly as per formula | Completed | Pass | Automatic Test |
| 21 | Verify ranking when jobs have the same score | Enter jobs with identical attributes | Jobs are ranked equally | Completed | Pass | Automatic Test |
| 22 | Validate ability to compare current job with an offer | Enter a current job and an offer, select comparison | Table with both job details displayed | Completed | Pass | Automatic Test |
| 23 | Validate ability to compare two job offers (no current job) | Enter at least two job offers, select comparison | Table with both job details displayed | Completed | Pass | Manual Test |
| 24 | Ensure compare jobs disabled if <2 offers | Try to press compare offers in main menu when system has less than 2 job offers, or no job offers with current job | Compare Offers Button to be disabled | Completed | Pass | Manual Test |
| 25 | Validate application startup performance | Launch the app multiple times | App starts within expected time | Completed | Pass | Manual Test |

<!-- This test plan will be continuously updated based on further development and testing feedback. -->


### 2.2 Summary of Testing Progress

We have successfully completed all 25 planned tests, and our job comparison APP has passed each test. Of the 25 proposed tests, 20 were conducted using automated code-based testing, while the remaining 5 tests (IDs 5, 10, 23, 24 and 25) were performed through manual testing. The following is a summary of our current testing progress.

#### Test Case 1: Verify main menu navigation  
We tested navigation through the app’s main menu by selecting each option. The correct screen loaded for each selection, confirming that the navigation functionality is working as intended. 
**Test Result:** Passed

#### Test Case 2: Enter and save a current job  
We tested the APP's ability to input and save a valid job by entering job details and saving. The job details were saved successfully without any issues, confirming the functionality is working as expected. 
**Test Result:** Passed

#### Test Case 3: Validate job detail constraints  
We tested inputting out-of-range values for each job attribute to ensure the app handles invalid input correctly. The app displayed the appropriate error messages for invalid data, confirming that the constraints are enforced properly. 
**Test Result:** Passed

#### Test Case 4: Compare two job offers  
We tested the comparison functionality by selecting two job offers saved. The app displayed a table with the correct job details, confirming that the comparison logic is functioning correctly. 
**Test Result:** Passed

#### Test Case 5: Adjust comparison settings  
We tested changing comparison weights and saving them. The app correctly updated the weights according to user input, confirming that the settings adjustment works properly. 
**Test Result:** Passed

#### Test Case 6: Verify job ranking  
We tested job ranking by entering multiple job offers and viewing the rankings. The jobs were ranked correctly based on their calculated job scores, confirming the ranking algorithm works as expected. 
**Test Result:** Passed

#### Test Case 7: Ensure comparison is disabled with <2 offers  
We tested the application with fewer than two job offers to ensure that the comparison button is appropriately disabled. The app correctly disabled the compare button when fewer than two offers were present. 
**Test Result:** Passed

#### Test Case 8: Enter and save job offers  
We conducted this test by entering multiple job offers and saving them. All job offers were saved successfully without errors, confirming the functionality is working as expected. 
**Test Result:** Passed

#### Test Case 9: Regression test for job score calculation  
We adjusted the job weights and subsequently triggered the comparison process, and the job scores accurately reflect the changes in the comparison weights.
**Test Result:** Passed

#### Test Case 10: Validate UI responsiveness  
We manually tested the UI showup and responsiveness in emulators with different screen size, and the UI adapts correctly to various screen sizes.
**Test Result:** Passed

#### Test Case 11: Android-specific back button behavior  
We tested the Android back button while entering job details. The app returned to the previous screen without saving data when the back button was pressed, confirming that the expected back button behavior is implemented correctly. 
**Test Result:** Passed

#### Test Case 12: Database persistence check  
We tested data persistence by saving job offers and restarting the app. The job offers were correctly retained after restarting, confirming that data is saved and retrieved properly. 
**Test Result:** Passed

#### Test Case 13: Validate cost of living adjustment  
We tested the adjustment of salary and bonus based on varying cost of living indexes. The app correctly adjusted the salary and bonus according to the input location’s cost of living, confirming the adjustment logic works as expected. 
**Test Result:** Passed

#### Test Case 14: Verify default weights for comparison settings  
We tested the default weights in the comparison settings by opening the settings without any changes. The app correctly applied default values of 1 for all weights, confirming that the default values are properly set. 
**Test Result:** Passed

#### Test Case 15: Validate weight constraints  
We conducted tests by entering invalid weight values (e.g., values exceeding 9). The application correctly displayed an error message when invalid weights were entered, thereby confirming that the weight constraints are properly enforced.
**Test Result:** Passed

#### Test Case 16: Ensure ‘Enter Another Offer’ functionality  
We tested the functionality of the “Enter Another Offer” option. After saving a job offer, selecting the "Enter Another Offer" option successfully opened a new job entry form, thereby confirming that this functionality operates as intended.
**Test Result:** Passed

#### Test Case 17: Ensure ‘Cancel’ functionality in job entry  
We tested the cancel functionality in the job entry form by starting to enter details and then pressing cancel. No data was saved, confirming that the cancel button works as intended. 
**Test Result:** Passed

#### Test Case 18: Ensure the APP won't hamper or crash with multiple job offers input and stored  
We input and saved multiple job offers (exceeding 10) by selecting the "Enter Another Offer" option, and verified that our application can handle the input and storage of numerous job offers without experiencing any crashes.
**Test Result:** Passed

#### Test Case 19: Ensure ‘Cancel’ functionality in comparison settings  
We tested the cancel button in the comparison settings after adjusting weights. The previous values remained unchanged, confirming that the cancel functionality works as expected. 
**Test Result:** Passed

#### Test Case 20: Validate correct job score calculation  
We conducted tests on job score calculations by entering jobs with varying attributes and comparing their scores with the expected ones. The application accurately computed the job scores according to the predefined formula, thereby confirming that the score calculation functionality operates as intended.
**Test Result:** Passed

#### Test Case 21: Verify ranking when jobs have the same score  
We tested the app’s ranking behavior when multiple jobs have the same score. Jobs with identical scores were ranked equally, confirming that the ranking algorithm handles ties correctly. 
**Test Result:** Passed

#### Test Case 22: Validate ability to compare current job with an offer  
We tested the comparison functionality between a current job and a job offer. The app correctly displayed both job details in a comparison table, confirming that the comparison works properly. 
**Test Result:** Passed

#### Test Case 23: Validate ability to compare two job offers (no current job)  
We tested comparing two job offers without a current job saved in the database. The app correctly displayed both job details in the comparison table, confirming that comparison works without a current job. 
**Test Result:** Passed

#### Test Case 24: Ensure compare jobs disabled if <2 offers  
We tested the app with fewer than two job offers to ensure that the compare button is disabled. The compare button was correctly disabled when fewer than two offers were entered, confirming that the functionality is working as expected. 
**Test Result:** Passed

#### Test Case 25: Validate application startup performance  
We tested the app’s startup time by launching it multiple times. The app launched within the expected time frame, confirming that the startup performance is acceptable. 
**Test Result:** Passed




<!-- if we select less than 2 job offers - then the compare app button should be disabled
to deselect - you have to select the offer again.

if we try to select more than 2 - all the other offers are disabled for selection -->
