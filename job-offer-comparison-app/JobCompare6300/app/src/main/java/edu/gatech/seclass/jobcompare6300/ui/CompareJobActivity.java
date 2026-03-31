package edu.gatech.seclass.jobcompare6300.ui;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.ArrayList;

import edu.gatech.seclass.jobcompare6300.R;
import edu.gatech.seclass.jobcompare6300.database.JobDatabase;
import edu.gatech.seclass.jobcompare6300.models.JobComparison;
import edu.gatech.seclass.jobcompare6300.models.Job;
import edu.gatech.seclass.jobcompare6300.models.ComparisonSettings;
import edu.gatech.seclass.jobcompare6300.database.ComparisonSettingsDao;
import java.io.Serializable;
// The function of this UI file:
// (1) show all the job instances from best to worst.
// (2) prompt the user to select two jobs and show a pairwise comparison


public class CompareJobActivity extends AppCompatActivity {

    private ListView jobListView;
    private Button compareButton, backButton, compareAnotherButton;
    private List<Job> sortedJobs;
    private List<Job> selectedJobs = new ArrayList<>(); // store the two jobs selected for comparison
    private View comparisonView; // show the comparison results of two selected jobs
    private TextView job1Details, job2Details, jobComparisonResults;
    private ComparisonSettings settings;
    private ComparisonSettingsDao settingsDao;
    private boolean[] jobSelections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_compare_jobs);
        try {
            setContentView(R.layout.activity_compare_jobs);
            System.out.println("CompareJobActivity loaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in CompareJobActivity: " + e.getMessage());
        }

        jobListView = findViewById(R.id.jobListView);
        compareButton = findViewById(R.id.compareButton);
        backButton = findViewById(R.id.backButton);
//        compareAnotherButton = findViewById(R.id.compareAnotherButton);
//        comparisonView = findViewById(R.id.comparisonView);
//        job1Details = findViewById(R.id.job1Details);
//        job2Details = findViewById(R.id.job2Details);
//        jobComparisonResults = findViewById(R.id.jobComparisonResults);

        settingsDao = JobDatabase.getInstance(this).comparisonSettingsDao();

        // Load settings asynchronously
        new Thread(() -> {
            settings = settingsDao.getSettings(); // Fetch settings from DB

            if (settings == null) {
                // If no settings exist, initialize with defaults and save to DB
                settings = new ComparisonSettings(1, 1, 1, 1, 1, 1);
                settingsDao.insertSettings(settings);
            }

            runOnUiThread(this::loadJobComparison); // Load jobs after settings are ready
        }).start();

        // "Compare" button to trigger the job comparison
        compareButton.setOnClickListener(v -> compareJobs(settings));

        // "Compare Another" button - allow the user to do another selection of two jobs for comparison
//        compareAnotherButton.setOnClickListener(v -> resetComparison());

        // 'Back' button- allow to return back to main menu
        backButton.setOnClickListener(v -> finish());
    }

    // show the sorted job list with Current job marked
    private void loadJobComparison() {
        JobComparison jobComparison = new JobComparison(this, settings);
        jobComparison.getSortedJobOffers(sortedJobs -> {
            if (sortedJobs != null) {
                this.sortedJobs = sortedJobs;
                jobSelections = new boolean[sortedJobs.size()];
                runOnUiThread(() -> displayJobList(jobSelections));
            } else {
                runOnUiThread(() -> Toast.makeText(this, "No jobs available.", Toast.LENGTH_SHORT).show());
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Reset selections when coming back from comparison view
        selectedJobs.clear();
        if (jobListView != null) {
            for (int i = 0; i < jobListView.getCount(); i++) {
                jobListView.setItemChecked(i, false);
                jobListView.getChildAt(i).setEnabled(true);
            }

        }

    }
    private void displayJobList(boolean[] jobSelections) {
        List<String> jobTitles = new ArrayList<>();
        for (Job job : sortedJobs) {
            String label = job.getTitle() + " - " + job.getCompany();
            if (job.isCurrentJob()) {
                label += " (Current Job)";
            }
            jobTitles.add(label);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, jobTitles) {
            @Override
            public boolean isEnabled(int position) {

                // Disable items that are NOT in the selected list when two jobs are already selected
                return selectedJobs.size() < 2 || selectedJobs.contains(sortedJobs.get(position));
            }
        };

        jobListView.setAdapter(adapter);
        jobListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        jobListView.setOnItemClickListener((parent, view, position, id) -> {
            Job selectedJob = sortedJobs.get(position);
            if (selectedJobs.contains(selectedJob)) {
                selectedJobs.remove(selectedJob);
            } else if (selectedJobs.size() < 2) {
                selectedJobs.add(selectedJob);

            } else {
                Toast.makeText(this, "You can only compare 2 jobs at a time.", Toast.LENGTH_SHORT).show();


            }
            updateSelectionState();

        });
    }


    //Trigger the comparison of two jobs, and show the result

    private void compareJobs(ComparisonSettings settings) {
        if (selectedJobs.size() != 2) {
            Toast.makeText(this, "Please select exactly 2 jobs to compare.", Toast.LENGTH_SHORT).show();
            return;
        }

        Job job1 = selectedJobs.get(0);
        Job job2 = selectedJobs.get(1);

        Intent intent = new Intent(this, ComparisonResultsActivity.class);
        intent.putExtra("job1", job1);
        intent.putExtra("job2", job2);
        intent.putExtra("settings", settings);

        startActivity(intent);
    }
    private void enableAllItems() {
        for (int i = 0; i < jobSelections.length; i++) {
            jobSelections[i] = false;
            if (jobListView.getChildAt(i) != null) {
                jobListView.getChildAt(i).setEnabled(true);
            }
        }
    }
    private void updateSelectionState() {
        if (selectedJobs.size() == 2) {
            disableUnselectedItems();
        } else {
            enableAllItems();
        }
    }

    private void disableUnselectedItems() {
        for (int i = 0; i < jobSelections.length; i++) {
            if (!selectedJobs.contains(sortedJobs.get(i))) {
                jobListView.getChildAt(i).setEnabled(false);
            }
        }
    }

}
