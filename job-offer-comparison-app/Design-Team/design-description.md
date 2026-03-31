# Design Description Document for Group Project Deliverable 1 (Team 039)

This document details the implementation of each requirement for the job offer comparison application as outlined in the UML design developed by SDP Team 039. For a comprehensive understanding of the UML design, please consult the design.pdf file located in our team's github repo. 


---

## Requirement 1: Main Menu

**Requirement**: When the app is started, the user is presented with the main menu, which allows the user to (1) enter or edit current job details, (2) enter job offers, (3) adjust the comparison settings, or (4) compare job offers (disabled if no job offers were entered yet).

**Implementation in Our Design**:
- The `JobComparisonApp` class serves as the **entry point** of the whole system.
- It interacts with class `JobManager` and `ComparisonSettings` to manage user actions.
- **Methods in `JobComparisonApp`:**
  - `displayMenu()`: Displays the main menu options.
  - `handleUserChoice(String choice)`: Processes user selections and directs to appropriate functionalities.

- The `JobManager` class **manages job-related data**, including the current job and a list of input job offers (represented as instances of the `Job` class), and controls the enabling and disabling of the **"Compare Job Offers"** option.

- If no job offers exist, `isComparisonAllowed()` in `JobManager` returns **false**, thereby preventing access to the comparison functionality and disabling the **"Compare Job Offers"** option.

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


**Implementation in Our Design**:
- The `Job` class stores job-related attributes and contains:
  - `validateJob(job: Job): List<Boolean>`: Verifies and ensures that all entered values comply with the specified constraints and returns a list of boolean values indicating which fields are **valid/invalid**.
  - `getAdjustedSalary(): float`: Calculates the adjusts salary based on the **Yearly Salary** and **cost of living index**.
  - `getAdjustedBonus(): float`: Calculates the adjusts bonus based on the **Yearly Bonus** and **cost of living index**.

- The `JobManager` class also manages the **current job** and includes:
  - `getCurrentJob(job: Job)`: Retrieves the current job that the user has entered or set in the system..
  - `setCurrentJob(job: Job)`: Sets or updates the current job.
  - `saveOrCancelJob(option, job: Job)`: Saves the job details permanently or cancels editing and returns to the main menu without saving.

- The `ComparisonSettings` class accepts, verifies, and sets the input weights for each payment component, ensuring that the assigned weights are properly aligned.

---



## Requirement 3: Enter Job Offers

**Requirement**: When choosing to *enter job offers,* a user will:
- Be shown a user interface to enter all the details of the offer, which are the same ones listed above for the current job.
- Be able to either save the job offer details or cancel.
- Be able to (1) enter another offer, (2) return to the main menu, or (3) compare the offer (if they saved it) with the current job details (if present).

**Implementation in Our Design**:

- The `Job` class represents both **job offers and the current job**, ensuring **consistency in data structure**. 

- The `JobManager` class maintains a list of job offers and provides:
  - `addJobOffer(job: Job)`: Adds a new job offer to the offer list.
  - `getJobOffers(): List<Job>`: Retrieves all stored job offers.
  - `IsComparisonAllowed(jobOffers: List<jobs>): Boolean`: Checks whether there are either one job offer and the current job available, or 2 distinct job offers are available for comparison. 
- If a job offer is **saved**, it is added to `JobManager`. If canceled, the system simply **returns to the main menu without storing data**. This functionality will be implemented by the `saveOrCancelJob(option: string, job: Job)` method within the `JobManager` class.

---



## Requirement 4: Adjust Comparison Settings

**Requirement**: When *adjusting the comparison settings,* the user can assign integer *weights* to:
- Yearly salary
- Yearly bonus
- Tuition Reimbursement
- Health Insurance
- Employee Product/Service Discount
- Child Adoption Assistance

**Implementation in Our Design**:
- The `ComparisonSettings` class manages user-defined weight values.
  - The default weight for each payment component is set to 1. 
  - `setWeights(salaryW, bonusW, tuitionW, insW, discW, adoptW)`: Updates weights dynamically.
  - `getWeight()` : used to extract values from user input, validate the input weights within the **valid range (0-9)**, and notify users if the values are invalid. This method returns a dictionary containing a boolean value that indicates the validity of the input weights and displays the result to the users.
  - `saveOrcancelWeights()`: save or cancel the input weights. If the input weights are cancelled, the set value will revert to the pre-set value, if available, or reset to the default value if no prior weight was provided. 

- The `CalScore(setting: ComparisonSetting)` method within the `'Job'` class will retrieve these weights to calculate the **job scores**. This method will only execute once the user initiates a job comparison.

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

**Implementation in Our Design**:

- The `JobManager` class handles job comparisons through:
  - `rankJobs()`: Rank the jobs from the best to worst based on the calculated job score
  - `compareJobs(job1: Job, job2: Job, settings: ComparisonSettings)`: Compares two jobs based on user-defined weights obtained from the `ComparisonSettings` class.
- The `ComparisonResult` class stores the **scores of both jobs**, including:
  - `job1Score`, `job2Score`: Stores individual job scores of two jobs being compared.
  - `toString()`: Formats and displays comparison results clearly.
- The `JobComparisonApp` class **retrieves and displays** the comparison results in a structured format for the user.



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

**Implementation in Our Design**:
- Once the user initiates a job comparison request, the `Job` class executes `calcScore(settings: ComparisonSettings): float`, ensuring the calculation of job scores with weights got from the `JobManager`.
- The `ComparisonSettings` class applies user-defined weights **to influence ranking calculations dynamically**.

---



## Requirement 7: User Interface

**Requirement**: The user interface must be intuitive and responsive.

**Realization in Our Design**:
- The **UI is handled externally** and **not explicitly represented** in the UML.
- The `JobComparisonApp` ensures **logical structuring and smooth user navigation**.

---



## Requirement 8: Single System Assumption

**Requirement**: For simplicity, you may assume there is a *single system* running the app (no communication or saving between devices is necessary).

**Realization in Our Design**:

- No multi-device communication or cloud synchronization beyond a **single runtime instance**.
- The design assumes a **standalone system**, with all data managed within **one session**, ensuring simplicity and efficiency.

---
