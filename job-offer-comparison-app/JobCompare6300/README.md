# **JobCompare App - User Manual** 

## Author: CS6300 SDP 2025 Spring_Team 039 

## **1. Introduction**  
The **JobCompare App** is designed to help users compare job offers based on multiple factors beyond just salary. It allows users to:
- Enter and edit details of their **current job**  
- Enter and manage multiple **job offers**  
- Adjust **comparison settings** using weighted factors for different payment components
- Compare jobs based on a calculated **Job Score**  

The app provides a simple and intuitive interface for evaluating different job opportunities by considering salary, bonus, benefits, and cost-of-living adjustments.  

---  

## **2. Getting Started**  

### **2.1 Android Compatibility**  
- **Minimum SDK:** Android 14 (API Level 34)  
- **Target SDK:** Android 14 (API Level 34)  

### **2.2 Main Menu**  
Upon launching the app, the **Main Menu** is displayed with the following buttons:  
- **Enter Current Job** - Add or edit details of your current job.  
- **Enter Job Offer** - Add job offers for comparison.  
- **Adjust Comparison Settings** - Customize weightings for different job factors.  
- **Compare Jobs** - Compare job offers and rank them.  

*Note:* The **Compare Jobs** button is disabled until at least **two job offers** exist or **one job offer and a current job** are present.  

---  

## **3. Entering and Editing Job Information**  

### **3.1 Entering Current Job**  
1. Tap **Enter Current Job** in the **Main Menu**.  
2. Fill in the following details:  
   - **Job Title**  
   - **Company Name**  
   - **City & State**  
   - **Cost of Living Index** (numerical value)  
   - **Yearly Salary**  
   - **Yearly Bonus**  
   - **Tuition Reimbursement** ($0 - $15,000)  
   - **Health Insurance** ($0 - $1,000 + 2% of AYS annually)  
   - **Employee Product/Service Discount** (up to 18% of salary)  
   - **Child Adoption Assistance** ($0 - $30,000 over 5 years)  
3. Tap **Save** to confirm or **Cancel** to discard changes.  

### **3.2 Entering Job Offers**  
1. Tap **Enter Job Offer** in the **Main Menu**.  
2. Enter job offer details (same as current job fields).  
3. Choose one of the following options:  
   - **Save Offer** - Saves the offer and returns to the main menu.  
   - **Enter Another Offer** - Saves the offer and return a new empty form prompt for a new entry.  
   - **Cancel** - Discards changes and returns to the main menu.  

---  

## **4. Adjusting Comparison Settings**  

### **4.1 Setting Weights for Job Comparison**  
1. Tap **Adjust Comparison Settings** in the **Main Menu**.  
2. Set the importance (0-9) for the following factors:  
   - **Yearly Salary**  
   - **Yearly Bonus**  
   - **Tuition Reimbursement**  
   - **Health Insurance**  
   - **Employee Discount**  
   - **Adoption Assistance**  
3. Tap **Save** to confirm weight changes or **Cancel** to discard.  

*Note:* If no weights are set, all factors are considered equally with default value  **1**.  

---  

## **5. Comparing Jobs**  

### **5.1 Selecting Jobs for Comparison**  
1. Tap **Compare Jobs** in the **Main Menu**.  
2. A list of jobs will be displayed (title and company), ranked based on their **Job Score** . 

Specially, the Job Score is calculated as the weighted average of AYS, AYB, TR, HI, EPSD and (CAA / 5)

   where:  
   - **AYS** = Adjusted Yearly Salary  
   - **AYB** = Adjusted Yearly Bonus  
   - **TR** = Tuition Reimbursement  
   - **HI** = Health Insurance  
   - **EPSD** = Employee Discount  
   - **CAA** = Child Adoption Assistance 

3. Select **two jobs** and tap **Compare**.  


### **5.2 Viewing Job Comparison**  
A side-by-side comparison table is displayed showing:  
- **Title**  
- **Company**  
- **Location**  
- **Adjusted Salary & Bonus**  
- **Tuition Reimbursement**  
- **Health Insurance**  
- **Employee Discount**  
- **Adoption Assistance**  
- **Job Score**  


4. After reviewing, choose to **Compare Another Job** for another comparison or **Return to Main Menu**.  

---  

## **6. Application Behavior & Constraints**  

### **6.1 Job Input Constraints**  
- **Salary, Bonus, and Benefits must be numerical values.**  
- **Tuition Reimbursement**: $0 - $15,000  
- **Health Insurance**: $0 - $1,000 + 2% of AYS  
- **Employee Discount**: ≤18% of salary  
- **Child Adoption Assistance**: $0 - $30,000 over 5 years  

If any constraint is violated, an error message will be displayed.  


### **6.2 Weight Constraints**
- The weight for each payment component considered should be integers between 0 and 9 (inclusive)

An error message will be displayed if the constraint is violated.


### **6.3 Job Comparison Constraints**  
- At least **one job offer and a current job** OR **two job offers** must exist to enable **Compare Jobs**.  
- If less than **two jobs** are available, the **Compare Jobs** button remains disabled.  

---  


## **7. Conclusion**  
The **JobCompare App** simplifies job comparison by ranking offers based on **weighted factors** and cost-of-living adjustments. It provides an **intuitive interface** for managing job data, adjusting comparison settings, and viewing ranked job comparisons. We warmly expect that you'll experience it as being both useful and charmingly user-friendly.



