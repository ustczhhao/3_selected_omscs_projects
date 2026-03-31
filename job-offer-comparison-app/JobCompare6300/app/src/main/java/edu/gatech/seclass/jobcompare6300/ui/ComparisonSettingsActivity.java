package edu.gatech.seclass.jobcompare6300.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import edu.gatech.seclass.jobcompare6300.R;
import edu.gatech.seclass.jobcompare6300.database.JobDatabase;
import edu.gatech.seclass.jobcompare6300.database.ComparisonSettingsDao;
import edu.gatech.seclass.jobcompare6300.models.ComparisonSettings;

public class ComparisonSettingsActivity extends AppCompatActivity {

    private EditText salaryWeight, bonusWeight, tuitionWeight, insuranceWeight, discountWeight, adoptionWeight;
    private ComparisonSettingsDao settingsDao;
    private ComparisonSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison_settings);

        salaryWeight = findViewById(R.id.inputSalaryWeight);
        bonusWeight = findViewById(R.id.inputBonusWeight);
        tuitionWeight = findViewById(R.id.inputTuitionWeight);
        insuranceWeight = findViewById(R.id.inputHealthInsuranceWeight);
        discountWeight = findViewById(R.id.inputEmployeeDiscountWeight);
        adoptionWeight = findViewById(R.id.inputAdoptionAssistanceWeight);

        Button saveButton = findViewById(R.id.btnSaveSettings);
        Button cancelButton = findViewById(R.id.btnCancelSettings);

        // Initialize DB + retrieve settings
        JobDatabase db = JobDatabase.getInstance(getApplicationContext());
        settingsDao = db.comparisonSettingsDao();

        new Thread(() -> {
            settings = settingsDao.getSettings();
            if (settings == null) {
                settings = new ComparisonSettings(1, 1, 1, 1, 1, 1);
                settingsDao.insertSettings(settings);
            }
            runOnUiThread(this::populateFields);
        }).start();

        saveButton.setOnClickListener(v -> saveSettings());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void populateFields() {
        salaryWeight.setText(String.valueOf(settings.getSalaryWeight()));
        bonusWeight.setText(String.valueOf(settings.getBonusWeight()));
        tuitionWeight.setText(String.valueOf(settings.getTuitionWeight()));
        insuranceWeight.setText(String.valueOf(settings.getInsuranceWeight()));
        discountWeight.setText(String.valueOf(settings.getDiscountWeight()));
        adoptionWeight.setText(String.valueOf(settings.getAdoptionWeight()));
    }

    private boolean isValidWeight(int value) {
        return value >= 0 && value <= 9;
    }

    // Replaces Toast usage with a Snackbar
    private void showSnackbar(String message) {
        runOnUiThread(() ->
                Snackbar.make(
                        findViewById(android.R.id.content),
                        message,
                        Snackbar.LENGTH_SHORT
                ).show()
        );
    }

    private void saveSettings() {
        try {
            int salary = Integer.parseInt(salaryWeight.getText().toString().trim());
            int bonus = Integer.parseInt(bonusWeight.getText().toString().trim());
            int tuition = Integer.parseInt(tuitionWeight.getText().toString().trim());
            int insurance = Integer.parseInt(insuranceWeight.getText().toString().trim());
            int discount = Integer.parseInt(discountWeight.getText().toString().trim());
            int adoption = Integer.parseInt(adoptionWeight.getText().toString().trim());

            if (!isValidWeight(salary) || !isValidWeight(bonus) || !isValidWeight(tuition) ||
                    !isValidWeight(insurance) || !isValidWeight(discount) || !isValidWeight(adoption)) {
                throw new IllegalArgumentException("All weights must be between 0 and 9.");
            }

            // Save new settings in background
            new Thread(() -> {
                settings.setSalaryWeight(salary);
                settings.setBonusWeight(bonus);
                settings.setTuitionWeight(tuition);
                settings.setInsuranceWeight(insurance);
                settings.setDiscountWeight(discount);
                settings.setAdoptionWeight(adoption);

                settingsDao.updateSettings(settings);

                // Show success via Snackbar, then delay finish
                runOnUiThread(() -> {
                    showSnackbarWithDelay("Settings saved!");
                });
            }).start();

        } catch (NumberFormatException e) {
            showSnackbar("Please enter valid integer values.");
        } catch (IllegalArgumentException e) {
            showSnackbar(e.getMessage());
        }
    }

    // New method to show Snackbar and delay finishing the activity
    private void showSnackbarWithDelay(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        finish(); // Only finish when Snackbar is dismissed
                    }
                })
                .show();
    }

}
