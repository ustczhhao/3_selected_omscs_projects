package edu.gatech.seclass.jobcompare6300.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import edu.gatech.seclass.jobcompare6300.R;
import edu.gatech.seclass.jobcompare6300.database.JobDao;
import edu.gatech.seclass.jobcompare6300.database.JobDatabase;
import edu.gatech.seclass.jobcompare6300.models.Job;

import java.util.List;
import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity {

    private JobDao jobDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Corrected Button IDs
        findViewById(R.id.btn_enter_current_job).setOnClickListener(view ->
                startActivity(new Intent(this, CurrentJobActivity.class)));

        findViewById(R.id.btn_add_job_offer).setOnClickListener(view ->
                startActivity(new Intent(this, JobOfferActivity.class)));

        findViewById(R.id.btn_adjust_settings).setOnClickListener(view ->
                startActivity(new Intent(this, ComparisonSettingsActivity.class)));

        findViewById(R.id.btn_compare_jobs).setOnClickListener(view ->
                startActivity(new Intent(this, CompareJobActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkJobOffers();
    }

    private void checkJobOffers() {
        new Thread(() -> {
            JobDatabase db = JobDatabase.getInstance(getApplicationContext());
            JobDao jobDao = db.jobDao();
            List<Job> jobOffers = jobDao.getAllJobs();

            if (jobOffers == null) {
                jobOffers = new ArrayList<>();  // Prevent NullPointerException
            }

            List<Job> finalJobOffers = jobOffers;
            runOnUiThread(() -> {
                Button compareButton = findViewById(R.id.btn_compare_jobs);
                compareButton.setEnabled(finalJobOffers.size() >= 2);
            });
        }).start();
    }


}
