# Design Description Document

## *Requirements*

1. When the app is started, the user is presented with the mainMenu, which allows the user to (1) enter or edit current job details, (2) enter job offers, (3) adjust the comparison settings, or (4) compare job offers (disabled if no job offers were entered yet)

    - *For this I have added a **`displayOptions()`** method in the **`mainMenu`** class that will load all the options, which is the entry point to the system. This class also uses the **`jobOffers`** class which will maintain all the saved jobs details (offer or current job) to determine if option 4 needs to be disabled or not. mainMenu class also contains an **`onClick()`** method which takes string option argument to navigate to the right subsequent page/display.*

2.  When choosing to enter current job details, a user will:
    1.  Be shown a user interface to enter (if it is the first time) or edit all the details of their current job, which consists of:
    
        1. Title
        2. Company
        3. Location
        4. Cost Of Living
        5. Yearly salary
        6. Yearly bonus
        7. Tuition Reimbursement
        8. Health Insurance
        9. Employee Product/Service Discount
        10. Child Adoption Assistance

        - *For this I have added a **`loadForm()`** method inside **`enterEditJobDisplay`** class which provides the interface to enter/edit the details of the job.*
        - *I have added a **`job`** class that is used by the **`enterEditJobDisplay`** class which contains **`editJobDetail()`** method to edit the current job details. It also has **`isCurrentJob`** attribute which returns a boolean, which will be used to check which job is the current job, if none of the jobs have this return True, then  **`enterJobDetail()`** method will be called instead of editJobDetail()*
        - *The **`job`** class in turn has a 1-1 relationship with **`jobDetail`** class which contains all the attributes of the job details mentioned in the requirements* 
        - *The explicit value constraints requirement mentioned in the requirements such as for Tuition Reimbursement and Health Insurance is satisfied by **`jobDetailValidation`** class used by **`job`** class to ensure the values entered are within the required constraint.*
    2. Be able to either save the job details or cancel and exit without saving, returning in both cases to the main menu.

        - *For this, I have added **`saveJobDetail()`** that takes an instance of a **`job`** class as the argument and **`cancelJobDetail()`** method in the **`job`** class.*

3. When choosing to enter job offers, a user will:

   1. Be shown a user interface to enter all the details of the offer, which are the same ones listed above for the current job.

        *Similar to the previous point, I have added a **`loadForm()`** method inside **`enterJobOffersDisplay`** class which provides the interface to enter the details of the job offer.*

        *I have used **`job`** class that contains the **`enterJobDetail()`** method to implement this, where the specific job details are described in **`jobDetail`** class. Here we explicitly keep **`isCurrentJob False`** as the user will fill details for job offers and not current job.*

   2. Be able to either save the job offer details or cancel.

        *Similar to above, the **`job`** class contains **`saveJobDetail()`** and **`cancelJobDetail()`** method to fulfill this requirement.*

   3. Be able to:

        1. Enter another offer
        2. Return to the main menu
        3. Compare the offer (if they saved it) with the current job details (if present)

        *To implement the option of adding another job, the **`job`** class contains **`addJob()`** that returns a new instance of job. The **`enterEditJobDisplay`** class has **`compareWithCurrentJob()`** function which uses **`jobComparison`** class to provide the functionality of comparing the job offer with the current job as **`jobComparison`** class has method **`compareJobs()`** which takes 2 job arguments.*

        *The **`enterEditJobDisplay`** class also has **`exit()`** method to return to the main menu.*

4. When adjusting the comparison settings, 
    1. The user can assign integer weights to:

        - Yearly salary

        - Yearly bonus

        - Tuition Reimbursement

        - Health Insurance

        - Employee Product/Service Discount

        - Child Adoption Assistance

        *The **`comparisonSettingsDisplay`** class provides **`loadForm()`** function to adjust settings. It uses **`comparisonSettings`** class which contains all the weight attributes required:*

        - `YearlySalaryWeight: int=1`
        - `YearlyBonusWeight: int=1`
        - `TuitionReimbursementWeight: int=1`
        - `HealthInsuranceWeight: int=1`
        - `EmployeeDiscountWeight: int=1`
        - `ChildAdoptionAssistanceWeight: int=1`

   2.  NOTE: These factors should be integer-based from 0 (no interest/don’t care) to 9 (highest interest). Default value for all weights: 1.

        *A note is added to the **`comparisonSettings`** class to ensure this is implemented in the software.*

   3. If no weights are assigned, all factors are considered equal.

         *This is not mentioned in the design, as this is automatically handled by the default values constraint.*

   4. The user must be able to either save the comparison settings or cancel; both will return the user to the main menu.

         *For this, I have added **`saveSettings()`** and **`cancelSettings()`** method in **`comparisonSettings`** class.*

5. When choosing to compare job offers, a user will:

   1. Be shown a list of job offers, displayed as Title and Company, ranked from best to worst (see below for details), and including the current job (if present), clearly indicated.

        - *This requirement is satisfied by implementing the `jobComparison` class used by the `compareOffersDisplay` class, which is responsible for displaying the Jobs using the `displayJobs()` method.*
        - *The `jobComparison` class contains the `scoreJobs` method, which is responsible for scoring each job based on job details and normalized job weights that it gets from the `comparisonSettings` class's `calculateAdjustedNormalizedWeights()` method.*
        - *The `jobComparison` class also has a `rankJobs()` method, which takes a list of tuples returned by the `scoreJobs()` method and returns the list of Jobs in descending order of their score.*
        - *The `jobComparison` class also has a `highlightCurrentJob()` method, which just uses the job instance and its `isCurrentJob` attribute to highlight the current job if available.*


   2. Select two jobs to compare and trigger the comparison.
   
        *The `compareOffersDisplay` class has the `selectJobsToCompare()` method, which returns a tuple of two job instances to compare. The `compareOffer()` method in this class triggers the comparison. The trigger uses the `jobComparison` class, which contains the `compareJobs()` method where the job score is calculated for both the selected jobs.*


   3. Be shown a table comparing the two jobs, displaying, for each job:

        - Title
        - Company
        - Location
        - Yearly salary adjusted for cost of living
        - Yearly bonus adjusted for cost of living
        - Tuition Reimbursement (TR)
        - Health Insurance (HI)
        - Employee Product/Service Discount (EPSD)
        - Child Adoption Assistance (CAA)
        - Job Score (JS)

        *For this, I have implemented the `displayComparison()` method inside the `compareOffersDisplay` class. This method takes two arguments—`Job1` and `Job2`—and displays the job details, including the job score, which is calculated dynamically using the `scoreJobs()` method in the `jobComparison` class that is used by the `compareOffersDisplay` class.*


5. When ranking jobs, a job’s score is computed as the weighted average of:

   **JS = AYS + AYB + TR + HI + EPSD + (CAA/5)**

   where:

   - AYS = Yearly Salary Adjusted for cost of living
   - AYB = Yearly Bonus Adjusted for cost of living
   - TR = Tuition Reimbursement (\$0 to \$15000 inclusive annually)
   - HI = Health Insurance (\$0-\$1000 inclusive + 2% of AYS annually)
   - EPSD = Employee Product/Service Discount (dollar amount up to 18% of Yearly Salary)
   - CAA = Child Adoption Assistance (expressed as a lump sum available over 5 years)

    *As mentioned in the previous requirement, the `scoreJobs()` method in the `jobComparison` class is used to calculate the scores. Since the requirement explicitly mentions a weighted average of the score, normalized weights are used for the calculation. The weight normalization is done by the `calculateAdjustedNormalizedWeights()` method in the `comparisonSettings` class.*

6. The user interface must be intuitive and responsive.

   *This is not represented in my design, as it will be handled entirely within the GUI implementation.*

7. For simplicity, you may assume there is a single system running the app (no communication or saving between devices is necessary).

   *The design represents a single system where no communication methods or saving between devices is implemented.*

