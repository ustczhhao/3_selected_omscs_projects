package edu.gatech.seclass.jobcompare6300.database;

import android.content.Context;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import edu.gatech.seclass.jobcompare6300.models.Job;
import edu.gatech.seclass.jobcompare6300.database.JobDatabase;

public class JobManager {

    private final JobDao jobDao;
    private ExecutorService executor;

    public JobManager(Context context) {
        JobDatabase db = JobDatabase.getInstance(context);
        jobDao = db.jobDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insertJob(Job job) {
        executor.execute(() -> jobDao.insertJob(job));
    }

    public void updateJob(Job job) {
        executor.execute(() -> jobDao.updateJob(job));
    }

    public void deleteJob(Job job) {
        executor.execute(() -> jobDao.deleteJob(job));
    }

    public void getJobById(int id, JobCallback callback) {
        executor.execute(() -> {
            Job job = jobDao.getJobById(id);
            callback.onJobLoaded(job);
        });
    }

    public void getAllJobs(JobsCallback callback) {
        executor.execute(() -> {
            List<Job> jobs = jobDao.getAllJobs();
            callback.onJobsLoaded(jobs);
        });
    }

    public interface JobCallback {
        void onJobLoaded(Job job);
    }

    public interface JobsCallback {
        void onJobsLoaded(List<Job> jobs);
    }
}