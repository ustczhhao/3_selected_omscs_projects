package edu.gatech.seclass.jobcompare6300.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import edu.gatech.seclass.jobcompare6300.R;
import edu.gatech.seclass.jobcompare6300.database.ComparisonSettingsDao;
import edu.gatech.seclass.jobcompare6300.database.JobDao;
import edu.gatech.seclass.jobcompare6300.database.JobDatabase;
import edu.gatech.seclass.jobcompare6300.models.ComparisonSettings;
import edu.gatech.seclass.jobcompare6300.models.Job;

public class JobOfferActivity extends AppCompatActivity {

    private EditText jobTitle, companyName, city, state, costOfLivingIndex, yearlySalary, yearlyBonus,
            tuitionReimbursement, healthInsurance, employeeDiscount, childAdoptionAssistance;

    private JobDao jobDao;
    private ComparisonSettingsDao settingsDao;
    private long jobId = -1;
    private ComparisonSettings settings;
    private Button compareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_offer);

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

        Button saveButton = findViewById(R.id.saveOfferButton);
        Button cancelButton = findViewById(R.id.cancelOfferButton);
        compareButton = findViewById(R.id.compareOfferButton);
        ImageButton addOfferButton = findViewById(R.id.addOfferButton); // "+" Button

        // Set up database and settings
        JobDatabase db = JobDatabase.getInstance(getApplicationContext());
        jobDao = db.jobDao();
        settingsDao = db.comparisonSettingsDao();

        // Disable Compare button initially
        disableCompareButton();

        // Load comparison settings in background
        new Thread(() -> {
            settings = settingsDao.getSettings();
            if (settings == null) {
                settings = new ComparisonSettings(1, 1, 1, 1, 1, 1);
                settingsDao.insertSettings(settings);
            }
        }).start();

        // Save Job Offer
        saveButton.setOnClickListener(view -> {
            if (!validateInput()) return; // If validation fails, show a snackbar and stop

            new Thread(() -> {
                // Create and save a new job offer (not current job)
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
                        false
                );

                long insertedId = jobDao.insertJob(job);
                runOnUiThread(() -> {
                    jobId = insertedId;
                    enableCompareButton();
                    showSnackbar("Job Offer saved successfully!");
                });
            }).start();
        });

        // Cancel (Exit without saving)
        cancelButton.setOnClickListener(v -> finish());

        // Compare with Current Job
        compareButton.setOnClickListener(v -> {
            new Thread(() -> {
                Job latestJob = jobDao.getLatestJobOffer();
                if (latestJob == null) {
                    runOnUiThread(() -> showSnackbar("No job offers found."));
                } else {
                    runOnUiThread(() -> compareWithCurrentJob(latestJob.getJobId()));
                }
            }).start();
        });

        // Add Another Offer (Reload empty form)
        addOfferButton.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, JobOfferActivity.class));
        });
    }

    private void compareWithCurrentJob(long jobId) {
        new Thread(() -> {
            Job currentJob = jobDao.getCurrentJob(true);
            if (currentJob == null) {
                runOnUiThread(() -> showSnackbar("You need to enter a current job first!"));
                return;
            }
            Job jobOffer = jobDao.getJobById(jobId);
            if (jobOffer == null) {
                runOnUiThread(() -> showSnackbar("Job offer not found."));
                return;
            }

            Intent intent = new Intent(this, ComparisonResultsActivity.class);
            intent.putExtra("job1", currentJob);
            intent.putExtra("job2", jobOffer);
            intent.putExtra("settings", settings);
            runOnUiThread(() -> startActivity(intent));
        }).start();
    }

    // Validates input fields
    private boolean validateInput() {
        if (jobTitle.getText().toString().trim().isEmpty() ||
                companyName.getText().toString().trim().isEmpty() ||
                city.getText().toString().trim().isEmpty() ||
                state.getText().toString().trim().isEmpty() ||
                yearlySalary.getText().toString().trim().isEmpty() ||
                yearlyBonus.getText().toString().trim().isEmpty() ||
                costOfLivingIndex.getText().toString().trim().isEmpty() ||
                tuitionReimbursement.getText().toString().trim().isEmpty() ||
                healthInsurance.getText().toString().trim().isEmpty() ||
                employeeDiscount.getText().toString().trim().isEmpty() ||
                childAdoptionAssistance.getText().toString().trim().isEmpty()) {

            runOnUiThread(() -> showSnackbar("All fields are required!"));
            return false;
        }

        float salary = Float.parseFloat(yearlySalary.getText().toString());
        float tuition = Float.parseFloat(tuitionReimbursement.getText().toString());
        float health = Float.parseFloat(healthInsurance.getText().toString());
        float discount = Float.parseFloat(employeeDiscount.getText().toString());
        float adoption = Float.parseFloat(childAdoptionAssistance.getText().toString());

        // Additional checks
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

    // Helper for showing a Snackbar message
    private void showSnackbar(String message) {
        // Must be on UI thread
        Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void disableCompareButton() {
        compareButton.setEnabled(false);
        compareButton.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
    }

    private void enableCompareButton() {
        compareButton.setEnabled(true);
        compareButton.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
    }

//    private void disableCompareButton() {
//        runOnUiThread(() -> {
//            compareButton.setEnabled(true);
//            compareButton.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
//
////            compareButton.setAlpha(0.5f); // Make button look disabled
////            compareButton.setClickable(false); // Prevent user interaction
//        });
//    }
//
//    private void enableCompareButton() {
//        runOnUiThread(() -> {
//            compareButton.setAlpha(1.0f); // Restore normal appearance
//            compareButton.setClickable(true); // Allow interaction
//        });
//    }

}
