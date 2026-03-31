package edu.gatech.seclass.jobcompare6300.ui;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import edu.gatech.seclass.jobcompare6300.R;
import edu.gatech.seclass.jobcompare6300.database.JobDao;
import edu.gatech.seclass.jobcompare6300.database.JobDatabase;
import edu.gatech.seclass.jobcompare6300.models.Job;

public class CurrentJobActivity extends AppCompatActivity {

    private EditText jobTitle, companyName, city, state, costOfLivingIndex, yearlySalary, yearlyBonus,
            tuitionReimbursement, healthInsurance, employeeDiscount, childAdoptionAssistance;
    private Button saveButton, cancelButton;
    private JobDao jobDao;
    private Job currentJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_job);

        // Initialize UI elements
        jobTitle = findViewById(R.id.jobTitle);
        companyName = findViewById(R.id.companyName);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        costOfLivingIndex = findViewById(R.id.costOfLivingIndex);
        yearlySalary = findViewById(R.id.yearlySalary);
        yearlyBonus = findViewById(R.id.yearlyBonus);
        tuitionReimbursement = findViewById(R.id.tuitionReimbursement);
        healthInsurance = findViewById(R.id.healthInsurance);
        employeeDiscount = findViewById(R.id.employeeDiscount);
        childAdoptionAssistance = findViewById(R.id.childAdoptionAssistance);

        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Initialize database in background thread
        new Thread(() -> {
            JobDatabase db = JobDatabase.getInstance(getApplicationContext());
            jobDao = db.jobDao();
            if (jobDao == null) {
                runOnUiThread(() -> showSnackbar("Database initialization error"));
            } else {
                loadCurrentJobIfExists();
            }
        }).start();

        // Save button logic
        saveButton.setOnClickListener(view -> {
            if (validateInput()) { // Validate before saving
                new Thread(() -> {
                    currentJob = jobDao.getCurrentJob(true);
                    if (currentJob == null) {
                        // Create new job
                        Job job = new Job(
                                jobTitle.getText().toString(),
                                companyName.getText().toString(),
                                city.getText().toString(),
                                state.getText().toString(),
                                Float.parseFloat(yearlySalary.getText().toString()),
                                Float.parseFloat(yearlyBonus.getText().toString()),
                                Float.parseFloat(costOfLivingIndex.getText().toString()),
                                Float.parseFloat(tuitionReimbursement.getText().toString()),
                                Float.parseFloat(healthInsurance.getText().toString()),
                                Float.parseFloat(employeeDiscount.getText().toString()),
                                Float.parseFloat(childAdoptionAssistance.getText().toString()),
                                true // Marks as current job
                        );
                        jobDao.insertJob(job);

                        // Show success message (Snackbar)
                        runOnUiThread(() -> showSnackbar("Current job saved successfully!"));
                    } else {
                        updateExistingJob(currentJob);
                    }

                    // If you want to close the activity after saving, do it here.
                    // For Espresso to detect the Snackbar, you might delay finish():
                     runOnUiThread(() -> new Handler(Looper.getMainLooper()).postDelayed(this::finish, 1000));
                    // or just remove finish() entirely if the test needs the Activity to remain open.
//                    finish();
                }).start();
            }
        });

        // Cancel button returns to main menu
        cancelButton.setOnClickListener(v -> finish());
    }

    // Load current job details if exists
    private void loadCurrentJobIfExists() {
        new Thread(() -> {
            if (jobDao != null) {
                currentJob = jobDao.getCurrentJob(true);
                if (currentJob != null) {
                    runOnUiThread(() -> {
                        populateFields(currentJob);
                        showSnackbar("Job Loaded!");
                    });
                }
            }
        }).start();
    }

    // Populate fields with existing job details
    private void populateFields(Job job) {
        jobTitle.setText(job.getTitle());
        companyName.setText(job.getCompany());
        city.setText(job.getCity());
        state.setText(job.getState());
        costOfLivingIndex.setText(String.valueOf(job.getCostOfLivingIndex()));
        yearlySalary.setText(String.valueOf(job.getYearlySalary()));
        yearlyBonus.setText(String.valueOf(job.getYearlyBonus()));
        tuitionReimbursement.setText(String.valueOf(job.getTuitionReimbursement()));
        healthInsurance.setText(String.valueOf(job.getHealthInsurance()));
        employeeDiscount.setText(String.valueOf(job.getEmployeeDiscount()));
        childAdoptionAssistance.setText(String.valueOf(job.getChildAdoptionAssistance()));
    }

    // Update an existing job
    private void updateExistingJob(Job job) {
        job.setTitle(jobTitle.getText().toString());
        job.setCompany(companyName.getText().toString());
        job.setCity(city.getText().toString());
        job.setState(state.getText().toString());
        job.setYearlySalary(Float.parseFloat(yearlySalary.getText().toString()));
        job.setYearlyBonus(Float.parseFloat(yearlyBonus.getText().toString()));
        job.setCostOfLivingIndex(Float.parseFloat(costOfLivingIndex.getText().toString()));
        job.setTuitionReimbursement(Float.parseFloat(tuitionReimbursement.getText().toString()));
        job.setHealthInsurance(Float.parseFloat(healthInsurance.getText().toString()));
        job.setEmployeeDiscount(Float.parseFloat(employeeDiscount.getText().toString()));
        job.setChildAdoptionAssistance(Float.parseFloat(childAdoptionAssistance.getText().toString()));
        job.setCurrentJob(true);
        jobDao.updateJob(job);

        // Show update success (Snackbar)
        runOnUiThread(() -> showSnackbar("Current job updated successfully!"));
    }

    // Validate user input
    private boolean validateInput() {
        if (jobTitle.getText().toString().trim().isEmpty() ||
                companyName.getText().toString().trim().isEmpty() ||
                city.getText().toString().trim().isEmpty() ||
                state.getText().toString().trim().isEmpty() ||
                costOfLivingIndex.getText().toString().trim().isEmpty() ||
                yearlySalary.getText().toString().trim().isEmpty() ||
                yearlyBonus.getText().toString().trim().isEmpty() ||
                tuitionReimbursement.getText().toString().trim().isEmpty() ||
                healthInsurance.getText().toString().trim().isEmpty() ||
                employeeDiscount.getText().toString().trim().isEmpty() ||
                childAdoptionAssistance.getText().toString().trim().isEmpty()) {

            runOnUiThread(() -> showSnackbar("All fields must be filled!"));
            return false;
        }

        float salary = Float.parseFloat(yearlySalary.getText().toString());
        if (salary < 0) {
            runOnUiThread(() -> showSnackbar("Yearly Salary must be positive"));
            return false;
        }

        float tuition = Float.parseFloat(tuitionReimbursement.getText().toString());
        float health = Float.parseFloat(healthInsurance.getText().toString());
        float discount = Float.parseFloat(employeeDiscount.getText().toString());
        float adoption = Float.parseFloat(childAdoptionAssistance.getText().toString());

        if (tuition < 0 || tuition > 15000) {
            showSnackbar("Tuition Reimbursement must be between $0 and $15,000");
            return false;
        }

        float maxHealth = 1000 + (0.02f * salary);
        if (health < 0 || health > maxHealth) {
            showSnackbar("Health Insurance must be between $0 and $" + maxHealth);
            return false;
        }

        float maxDiscount = 0.18f * salary;
        if (discount < 0 || discount > maxDiscount) {
            showSnackbar("Employee Discount cannot exceed 18% of Yearly Salary ($" + maxDiscount + ")");
            return false;
        }

        if (adoption < 0 || adoption > 30000) {
            showSnackbar("Child Adoption Assistance must be between $0 and $30,000");
            return false;
        }

        return true;
    }

    // Helper method to show a Snackbar message
    private void showSnackbar(String message) {
        // Must run on UI thread
        runOnUiThread(() ->
                Snackbar.make(
                        findViewById(android.R.id.content),
                        message,
                        Snackbar.LENGTH_SHORT
                ).show()
        );
    }
}
