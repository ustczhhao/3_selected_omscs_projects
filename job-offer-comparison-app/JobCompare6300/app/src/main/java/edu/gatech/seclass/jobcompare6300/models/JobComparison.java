package edu.gatech.seclass.jobcompare6300.models;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.gatech.seclass.jobcompare6300.database.JobDatabase;
import edu.gatech.seclass.jobcompare6300.database.JobDao;
import edu.gatech.seclass.jobcompare6300.models.Job;
import edu.gatech.seclass.jobcompare6300.models.ComparisonSettings;

// The function of this code is to:
// Extract all jobs stored in the database, calculate the job score, and rank them according to the score

public class JobComparison {

    private JobDao jobDao;
    private ComparisonSettings comparisonSettings; // Weight configuration for comparison
    private ExecutorService executor;

    public JobComparison(Context context, ComparisonSettings settings) {
        JobDatabase db = JobDatabase.getInstance(context);
        this.jobDao = db.jobDao();
        this.comparisonSettings = settings;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void setComparisonSettings(ComparisonSettings settings) {
        this.comparisonSettings = settings;
    }

    public interface JobComparisonCallback {
        void onJobComparisonCompleted(List<Job> sortedJobs);
    }

    public void getSortedJobOffers(JobComparisonCallback callback) {
        executor.execute(() -> {

            List<Job> jobList = jobDao.getAllJobs();  // Fetch all job offers from the database
//            Log.d("DEBUG", "Jobs retrieved before so --rting: " + jobList.size());
//            Log.d("DEBUG", "DEBUG: JobDatabase instance hash in test: " + jobDao.hashCode());

//            Job currentJob = jobDao.getCurrentJob(true); // Fetch the current job and add to the list
//            if (currentJob != null) {
//                jobList.add(currentJob);
//            }

            // Store scores instead of recalculating during sorting
            List<Pair<Job, Float>> jobScoreList = new ArrayList<>();
            for (Job job : jobList) {
                jobScoreList.add(new Pair<>(job, job.calcScore(comparisonSettings)));
            }

            // Sort the jobs by score (highest first)
            jobScoreList.sort((j1, j2) -> Float.compare(j2.second, j1.second));

            // Extract sorted jobs
            List<Job> sortedJobs = new ArrayList<>();
            for (Pair<Job, Float> pair : jobScoreList) {
                sortedJobs.add(pair.first);
            }

            // Return the ranked job list via callback
            callback.onJobComparisonCompleted(sortedJobs);
        });
    }
}
