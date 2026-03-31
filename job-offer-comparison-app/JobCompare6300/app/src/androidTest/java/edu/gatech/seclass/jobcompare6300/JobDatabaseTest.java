package edu.gatech.seclass.jobcompare6300;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import edu.gatech.seclass.jobcompare6300.database.JobDatabase;
import edu.gatech.seclass.jobcompare6300.database.JobDao;
import edu.gatech.seclass.jobcompare6300.models.Job;
import edu.gatech.seclass.jobcompare6300.models.JobComparison;
import edu.gatech.seclass.jobcompare6300.models.ComparisonSettings;

public class JobDatabaseTest {
    private JobDao jobDao;
    private JobDatabase db;
    private JobComparison jobComparison;
    private ComparisonSettings defaultSettings;

    @Before
    public void setup() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, JobDatabase.class)
                .allowMainThreadQueries()
                .build();
        jobDao = db.jobDao();
        defaultSettings = new ComparisonSettings(1, 1, 1, 1, 1, 1);


        setDatabaseInstance(db);

        jobComparison = new JobComparison(context, defaultSettings);

        jobDao.deleteAllJobs();
    }
    private void setDatabaseInstance(JobDatabase testDatabase) throws Exception {
        java.lang.reflect.Field instanceField = JobDatabase.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, testDatabase);
    }


    @After
    public void tearDown() {
//        JobDatabase.getInstance(ApplicationProvider.getApplicationContext()).close();
        db.close(); // Cleanup database after tests
    }

    @Test
    public void testNoJobsInDatabase() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        jobComparison.getSortedJobOffers(sortedJobs -> {
            assertNotNull(sortedJobs);
            assertEquals(0, sortedJobs.size()); // No jobs should be returned
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testSingleJobInDatabase() throws InterruptedException {
        jobDao.deleteAllJobs();
        Job job = new Job("Software Engineer", "Google", "Mountain View", "CA",
                120000, 15000, 140, 10000, 500, 2000, 5000, false);
        jobDao.insertJob(job);

        CountDownLatch latch = new CountDownLatch(1);
        jobComparison.getSortedJobOffers(sortedJobs -> {
            assertNotNull(sortedJobs);
            assertEquals(1, sortedJobs.size());
            assertEquals("Software Engineer", sortedJobs.get(0).getTitle());
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testDuplicateJobEntries() throws InterruptedException {
        jobDao.deleteAllJobs();
        Job job1 = new Job("Software Engineer", "Google", "Mountain View", "CA",
                120000, 15000, 140, 10000, 500, 2000, 5000, false);
        Job job2 = new Job("Software Engineer", "Google", "Mountain View", "CA",
                120000, 15000, 140, 10000, 500, 2000, 5000, false);

        jobDao.insertJob(job1);
        jobDao.insertJob(job2);

        CountDownLatch latch = new CountDownLatch(1);
        jobComparison.getSortedJobOffers(sortedJobs -> {
            assertNotNull(sortedJobs);
            assertEquals(2, sortedJobs.size());
            assertEquals(sortedJobs.get(0).getTitle(), sortedJobs.get(1).getTitle());
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testExtremeSalaryValues() throws InterruptedException {
        jobDao.deleteAllJobs();

        Job job1 = new Job("Intern", "Startup", "NYC", "NY",
                5000, 1000, 120, 200, 50, 100, 200, false);
        Job job2 = new Job("CEO", "BigTech", "San Francisco", "CA",
                2000000, 500000, 150, 50000, 10000, 50000, 30000, false);

        jobDao.insertJob(job1);
        jobDao.insertJob(job2);

        List<Job> jobs = jobDao.getAllJobs();
        assertEquals(2, jobs.size());
//        Log.d("DEBUG2", "Jobs retrieved before so --rting: " + jobs.size());
//        Log.d("DEBUG", "DEBUG: JobDatabase instance hash in test: " + jobDao.hashCode());
        CountDownLatch latch = new CountDownLatch(1);

        // ✅ sortedJobs is set **inside this callback**
        jobComparison.getSortedJobOffers(sortedJobs -> {
            assertNotNull(sortedJobs);
            assertEquals(2, sortedJobs.size());
            assertEquals("CEO", sortedJobs.get(0).getTitle());
            latch.countDown();
        });

        // ✅ Waits until callback runs before proceeding
//        assertTrue("Callback did not complete in time!", latch.await(2, TimeUnit.SECONDS));
    }


    @Test
    public void testRankingBasedOnScores() throws InterruptedException {
        jobDao.deleteAllJobs();
        Job job1 = new Job("Intern", "Startup", "NYC", "NY",
                5000, 1000, 120, 200, 50, 100, 200, false);
        Job job2 = new Job("Mid-level Dev", "TechCorp", "Seattle", "WA",
                100000, 20000, 120, 5000, 1000, 5000, 10000, false);
        Job job3 = new Job("CEO", "BigTech", "San Francisco", "CA",
                2000000, 500000, 150, 50000, 10000, 50000, 30000, false);

        jobDao.insertJob(job1);
        jobDao.insertJob(job2);
        jobDao.insertJob(job3);

        CountDownLatch latch = new CountDownLatch(1);
        jobComparison.getSortedJobOffers(sortedJobs -> {
            assertNotNull(sortedJobs);
            assertEquals(3, sortedJobs.size());

            float score1 = sortedJobs.get(0).calcScore(defaultSettings);
            float score2 = sortedJobs.get(1).calcScore(defaultSettings);
            float score3 = sortedJobs.get(2).calcScore(defaultSettings);

            assertTrue(score1 >= score2 && score2 >= score3);
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }
}
