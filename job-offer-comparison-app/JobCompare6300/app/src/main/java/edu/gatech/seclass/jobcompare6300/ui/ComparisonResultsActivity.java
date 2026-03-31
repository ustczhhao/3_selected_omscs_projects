package edu.gatech.seclass.jobcompare6300.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

import edu.gatech.seclass.jobcompare6300.R;
import edu.gatech.seclass.jobcompare6300.models.Job;
import edu.gatech.seclass.jobcompare6300.models.ComparisonSettings;

public class ComparisonResultsActivity extends AppCompatActivity {

    private Job job1, job2;
    private ComparisonSettings settings;
    private TextView job1TitleValue, job2TitleValue, job1CompanyValue, job2CompanyValue,
            job1CityValue, job2CityValue, job1StateValue, job2StateValue,
            job1SalaryValue, job2SalaryValue, job1BonusValue, job2BonusValue,
            job1TuitionValue, job2TuitionValue, job1InsuranceValue, job2InsuranceValue,
            job1DiscountValue, job2DiscountValue, job1AdoptionValue, job2AdoptionValue,
            job1ScoreValue, job2ScoreValue;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison_view);

        job1 = (Job) getIntent().getSerializableExtra("job1");
        job2 = (Job) getIntent().getSerializableExtra("job2");
        settings = (ComparisonSettings) getIntent().getSerializableExtra("settings");

        job1TitleValue = findViewById(R.id.job1TitleValue);
        job2TitleValue = findViewById(R.id.job2TitleValue);
        job1CompanyValue = findViewById(R.id.job1CompanyValue);
        job2CompanyValue = findViewById(R.id.job2CompanyValue);
        job1CityValue = findViewById(R.id.job1CityValue);
        job2CityValue = findViewById(R.id.job2CityValue);
        job1StateValue = findViewById(R.id.job1StateValue);
        job2StateValue = findViewById(R.id.job2StateValue);
        job1SalaryValue = findViewById(R.id.job1SalaryValue);
        job2SalaryValue = findViewById(R.id.job2SalaryValue);
        job1BonusValue = findViewById(R.id.job1BonusValue);
        job2BonusValue = findViewById(R.id.job2BonusValue);
        job1TuitionValue = findViewById(R.id.job1TuitionValue);
        job2TuitionValue = findViewById(R.id.job2TuitionValue);
        job1InsuranceValue = findViewById(R.id.job1InsuranceValue);
        job2InsuranceValue = findViewById(R.id.job2InsuranceValue);
        job1DiscountValue = findViewById(R.id.job1DiscountValue);
        job2DiscountValue = findViewById(R.id.job2DiscountValue);
        job1AdoptionValue = findViewById(R.id.job1AdoptionValue);
        job2AdoptionValue = findViewById(R.id.job2AdoptionValue);
        job1ScoreValue = findViewById(R.id.job1ScoreValue);
        job2ScoreValue = findViewById(R.id.job2ScoreValue);
        backButton = findViewById(R.id.backButton);

        populateComparisonTable();

        backButton.setOnClickListener(v -> finish());
    }

    private void populateComparisonTable() {
        if (job1 != null && job2 != null && settings != null) {
            job1TitleValue.setText(job1.getTitle());
            job2TitleValue.setText(job2.getTitle());

            job1CompanyValue.setText(job1.getCompany());
            job2CompanyValue.setText(job2.getCompany());

            job1CityValue.setText(job1.getCity());
            job2CityValue.setText(job2.getCity());

            job1StateValue.setText(job1.getState());
            job2StateValue.setText(job2.getState());

            // Salary & Bonus are adjusted if the job code does a cost-of-living factor
            job1SalaryValue.setText(formatCurrency(job1.getAdjustedSalary()));
            job2SalaryValue.setText(formatCurrency(job2.getAdjustedSalary()));
            job1BonusValue.setText(formatCurrency(job1.getAdjustedBonus()));
            job2BonusValue.setText(formatCurrency(job2.getAdjustedBonus()));

            job1TuitionValue.setText(formatCurrency(job1.getTuitionReimbursement()));
            job2TuitionValue.setText(formatCurrency(job2.getTuitionReimbursement()));

            job1InsuranceValue.setText(formatCurrency(job1.getHealthInsurance()));
            job2InsuranceValue.setText(formatCurrency(job2.getHealthInsurance()));

            job1DiscountValue.setText(formatCurrency(job1.getEmployeeDiscount()));
            job2DiscountValue.setText(formatCurrency(job2.getEmployeeDiscount()));

            job1AdoptionValue.setText(formatCurrency(job1.getChildAdoptionAssistance()));
            job2AdoptionValue.setText(formatCurrency(job2.getChildAdoptionAssistance()));

            float job1Score = job1.calcScore(settings);
            float job2Score = job2.calcScore(settings);
            job1ScoreValue.setText(String.format(Locale.US, "%.2f", job1Score));
            job2ScoreValue.setText(String.format(Locale.US, "%.2f", job2Score));

            if (job1Score > job2Score) {
                job1ScoreValue.setTextColor(getResources().getColor(R.color.green, getTheme()));
                job2ScoreValue.setTextColor(getResources().getColor(R.color.red, getTheme()));
            } else if (job2Score > job1Score) {
                job2ScoreValue.setTextColor(getResources().getColor(R.color.green, getTheme()));
                job1ScoreValue.setTextColor(getResources().getColor(R.color.red, getTheme()));
            } else {
                job1ScoreValue.setTextColor(getResources().getColor(R.color.black, getTheme()));
                job2ScoreValue.setTextColor(getResources().getColor(R.color.black, getTheme()));
            }
        }
    }

    private String formatCurrency(float value) {
        return String.format(Locale.US, "$%,.2f", value);
    }
}
