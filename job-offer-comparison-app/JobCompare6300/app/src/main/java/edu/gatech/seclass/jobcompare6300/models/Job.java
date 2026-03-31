package edu.gatech.seclass.jobcompare6300.models;
import java.io.Serializable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "jobs")
public class Job implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private int jobId;

    private boolean isCurrentJob;
    private String title;
    private String company;
    private String city;
    private String state;
    private float yearlySalary;
    private float yearlyBonus;
    private float costOfLivingIndex;
    private float tuitionReimbursement;
    private float healthInsurance;
    private float employeeDiscount;
    private float childAdoptionAssistance;



    // Constructor
    public Job(String title, String company, String city, String state, float yearlySalary, float yearlyBonus,
               float costOfLivingIndex, float tuitionReimbursement, float healthInsurance,
               float employeeDiscount, float childAdoptionAssistance, boolean isCurrentJob) {
        this.title = title;
        this.company = company;
        this.city = city;
        this.state = state;
        this.yearlySalary = yearlySalary;
        this.yearlyBonus = yearlyBonus;
        this.costOfLivingIndex = costOfLivingIndex;
        this.tuitionReimbursement = tuitionReimbursement;
        this.healthInsurance = healthInsurance;
        this.employeeDiscount = employeeDiscount;
        this.childAdoptionAssistance = childAdoptionAssistance;
        this.isCurrentJob = isCurrentJob;
    }

    // Getters and Setters
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    // Getter and setter for isCurrentJob
    public boolean isCurrentJob() { return isCurrentJob; }
    public void setCurrentJob(boolean currentJob) { isCurrentJob = currentJob; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public float getYearlySalary() { return yearlySalary; }
    public void setYearlySalary(float yearlySalary) { this.yearlySalary = yearlySalary; }

    public float getYearlyBonus() { return yearlyBonus; }
    public void setYearlyBonus(float yearlyBonus) { this.yearlyBonus = yearlyBonus; }

    public float getCostOfLivingIndex() { return costOfLivingIndex; }
    public void setCostOfLivingIndex(float costOfLivingIndex) { this.costOfLivingIndex = costOfLivingIndex; }

    public float getTuitionReimbursement() { return tuitionReimbursement; }
    public void setTuitionReimbursement(float tuitionReimbursement) { this.tuitionReimbursement = tuitionReimbursement; }

    public float getHealthInsurance() { return healthInsurance; }
    public void setHealthInsurance(float healthInsurance) { this.healthInsurance = healthInsurance; }

    public float getEmployeeDiscount() { return employeeDiscount; }
    public void setEmployeeDiscount(float employeeDiscount) { this.employeeDiscount = employeeDiscount; }

    public float getChildAdoptionAssistance() { return childAdoptionAssistance; }
    public void setChildAdoptionAssistance(float childAdoptionAssistance) { this.childAdoptionAssistance = childAdoptionAssistance; }

    // Calculation methods
    public float getAdjustedSalary() {
        return yearlySalary / (costOfLivingIndex / 100);
    }

    public float getAdjustedBonus() {
        return yearlyBonus / costOfLivingIndex * 100;
    }

    public float calcScore(ComparisonSettings settings) {
        float weightsSum = settings.getWeightsSum();
        return (
                settings.getSalaryWeight() / weightsSum * getAdjustedSalary() +
                        settings.getBonusWeight() / weightsSum * getAdjustedBonus() +
                        settings.getTuitionWeight() / weightsSum * tuitionReimbursement +
                        settings.getInsuranceWeight() / weightsSum * healthInsurance +
                        settings.getDiscountWeight() / weightsSum * employeeDiscount +
                        settings.getAdoptionWeight() / weightsSum * (childAdoptionAssistance / 5)
        );
    }

    public boolean validateFields(String jobTitle, String companyName, String city, String state,
                                  String salaryStr, String tuitionStr, String healthStr,
                                  String discountStr, String adoptionStr) {

        if (jobTitle.trim().isEmpty() || companyName.trim().isEmpty() || city.trim().isEmpty() ||
                state.trim().isEmpty() || salaryStr.trim().isEmpty() || tuitionStr.trim().isEmpty() ||
                healthStr.trim().isEmpty() || discountStr.trim().isEmpty() || adoptionStr.trim().isEmpty()) {
            return false; // Missing fields
        }

        float salary = Float.parseFloat(salaryStr);
        float tuition = Float.parseFloat(tuitionStr);
        float health = Float.parseFloat(healthStr);
        float discount = Float.parseFloat(discountStr);
        float adoption = Float.parseFloat(adoptionStr);

        if (tuition < 0 || tuition > 15000) return false;

        float maxHealth = 1000 + (0.02f * salary);
        if (health < 0 || health > maxHealth) return false;

        float maxDiscount = 0.18f * salary;
        if (discount < 0 || discount > maxDiscount) return false;

        if (adoption < 0 || adoption > 30000) return false;

        return true;
    }

}