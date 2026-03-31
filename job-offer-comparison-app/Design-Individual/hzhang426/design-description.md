# Design Description Document of Assignment 5

This document elucidates the implementation of each requirement for the job offer comparison application within the UML design, or provides reasoning for any requirements that are not directly represented. For a comprehensive overview of the UML design, please refer to the design.pdf file in the same folder.


---

## Requirement 1: Main Menu

**Requirement**: When the app is started, the user is presented with the main menu, which allows the user to (1) enter or edit current job details, (2) enter job offers, (3) adjust the comparison settings, or (4) compare job offers (disabled if no job offers were entered yet).

**Realization in My Design**:
- The `MainMenu` class is responsible for displaying the main menu and handling user choices.
- The `UserInterface` class interacts with `MainMenu` to display the menu and prompt the user for input.
- The `JobManager` class ensures the "compare job offers" option is enabled or disabled based on the number of job offers. If there are no job offers, the "compare job offers" option is disabled. Conversely, if there is at least one job offer (and optionally a current job), the "compare job offers" option will be enabled.

---


## Requirement 2: Enter/Edit Current Job Details

**Requirement**: When choosing to *enter current job details,* a user will:
- Be shown a user interface to enter (if it is the first time) or edit all the details of their current job, which consists of:
    i.  Title
    ii. Company
    iii.    Location (entered as city and state)
    iv. Cost of living in the location (expressed as an index)
    v.  Yearly salary 
    vi. Yearly bonus 
    vii.    Tuition Reimbursement ($0 to $15000 inclusive annually)
    viii.   Health Insurance ($0-$1000 inclusive + 2% of AYS annually)
    ix. Employee Product/Service Discount (dollar amount up to 18% of Yearly Salary) 
    x.  Child Adoption Assistance (expressed as a lump sum $0 to $30000 inclusive available over 5 years)

- Be able to either save the job details or cancel and exit without saving, returning in both cases to the main menu.


**Realization in My Design**:
- The `Job` class stores all the required details as listed above and also has a boolean attribute to indicate whether it is the current job. 
- The `JobManager` class manages the current jobs and available job offers, as well as provides methods to edit and save the job details. Specially, the `setCurrentJob(job: Job)` method is used to sets or updates the current job. If the user cancels, no changes are saved, and the app will return to the main menu.
- The `UserInterface` class prompts the user to enter or edit job details and communicates with `JobManager` to save or cancel changes. 

---


## Requirement 3: Enter Job Offers

**Requirement**: When choosing to *enter job offers,* a user will:
- Be shown a user interface to enter all the details of the offer, which are the same ones listed above for the current job.
- Be able to either save the job offer details or cancel.
- Be able to (1) enter another offer, (2) return to the main menu, or (3) compare the offer (if they saved it) with the current job details (if present).

**Realization in My Design**:
- The `Job` class is used to represent the current job and job offers. 
- The `JobManager` class manages a list of job offers and provides methods to add, edit, and retrieve job offers. Within the class, the `addJobOffer(job: Job)` method adds a new job offer to the list. If the user cancels, no changes will be saved, and the app will return to the main menu.
- The `UserInterface` class prompts the user to enter job offer details and communicates with `JobManager` to save or cancel the offer. Specially, the `promptForJobDetails()` method is used to collect job details from the user and returns a `Job` object then. If the user chooses to save, the `JobManager` is updated with the new job offer. Otherwise, if the user cancels, the app returns to the main menu without saving.
- The `MainMenu` class allows the user to choose between entering another offer, returning to the main menu, or comparing the offer with the current job. 

---


## Requirement 4: Adjust Comparison Settings

**Requirement**: When *adjusting the comparison settings,* the user can assign integer *weights* to:
- Yearly salary
- Yearly bonus
- Tuition Reimbursement
- Health Insurance
- Employee Product/Service Discount
- Child Adoption Assistance

**Realization in My Design**:
- The `ComparisonSettings` class stores the weights for each factor (salary, bonus, tuition reimbursement, etc.). 
Within the class, the `setWeights()` method sets the weights for each factor, and the default value for each factor is set to be 1 if no value is provided by the user.  The `getWeights()` method validates whether the input weights are within the valid range (i.e., integers between 0 and 9) and returns them as a map for further processing.
- The `UserInterface` class prompts the user to enter weights and communicates with `ComparisonSettings` to save or cancel the changes. Specially, the `promptForWeights()` method collects weights from the user and returns a `ComparisonSettings` object. If the user cancels, no changes are saved, and the app returns to the main menu.
- The `JobComparison` class uses the weights from `ComparisonSettings` to calculate job scores. 

---


## Requirement 5: Compare Job Offers

**Requirement**: When choosing to *compare job offers,* a user will:
- Be shown a list of job offers, displayed as Title and Company, ranked from best to worst, and including the current job (if present), clearly indicated.
- Select two jobs to compare and trigger the comparison.
- Be shown a table comparing the two jobs, displaying, for each job:
  - Title, Company, Location
  - Yearly salary adjusted for cost of living
  - Yearly bonus adjusted for cost of living
  - Tuition Reimbursement (TR)
  - Health Insurance (HI)
  - Employee Product/Service Discount (EPSD)
  - Child Adoption Assistance (CAA)
  - Job Score (JS)
- Be offered to perform another comparison or go back to the main menu.

**Realization in My Design**:
- The `JobComparison` class is responsible for calculating the scores of two jobs and comparing them accordingly. In detail, the `calculateJobScore()` method computes the job score using the weighted average formula, while the `compareJobs()` method evaluates and compares two jobs (`job1` and `job2`) and presents the results.
- The `Job` class provides methods to calculate adjusted salary (`getAdjustedSalary()`) and bonus (`getAdjustedBonus()`)based on the cost of living. 
- The `UserInterface` class displays the list of job offers, prompts the user to select two jobs, and displays the comparison table (`displayComparisonTable()`).
- The `MainMenu` class allows the user to perform another comparison or return to the main menu.

---


## Requirement 6: Job Score Calculation

**Requirement**: When ranking jobs, a job's score is computed as the **weighted** average of:
\[
JS = AYS + AYB + TR + HI + EPSD + (CAA/5)
\]
where:
- AYS = Adjusted Yearly Salary
- AYB = Adjusted Yearly Bonus
- TR = Tuition Reimbursement
- HI = Health Insurance
- EPSD = Employee Product/Service Discount
- CAA = Child Adoption Assistance

**Realization in My Design**:
- The `JobComparison` class calculates the job score using the formula provided. Within the class, `calculateJobScore(job: Job)` computes the score for a single job, and the weights are retrieved from the `ComparisonSettings` class.
- The `Job` class provides methods to calculate adjusted salary (`getAdjustedSalary()`) and adjusted bonus (`getAdjustedBonus()`). 
- The `ComparisonSettings` class provides the weights used in the calculation.

---


## Requirement 7: User Interface

**Requirement**: The user interface must be intuitive and responsive.

**Realization in My Design**:
- This aspect is not represented in the current design, as it will be fully managed within the GUI implementation. However, the current design incorporates several considerations to meet this requirement.
    - The `UserInterface` class is responsible for handling all user interactions, including displaying menus, prompting for input, and showing comparison results.
    - The design focuses on separating the UI logic from the business logic, ensuring that the UI can be implemented in a way that is intuitive and responsive.
    - The `UserInterface` class provides methods such as `promptForJobDetails()`, `displayComparisonTable()`, and `promptForWeights()` to interact with the user.

---


## Requirement 8: Single System Assumption

**Requirement**: For simplicity, you may assume there is a *single system* running the app (no communication or saving between devices is necessary).

**Realization in My Design**:

This aspect is not explicitly represented in the current design, as it will be fully managed within the GUI implementation. However, given that the design assumes a single-system architecture, there is no need for consideration of classes or methods related to inter-device communication or data sharing.

---
