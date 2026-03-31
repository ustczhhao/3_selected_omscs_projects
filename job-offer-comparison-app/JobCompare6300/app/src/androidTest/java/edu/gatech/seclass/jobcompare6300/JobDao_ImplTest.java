package edu.gatech.seclass.jobcompare6300;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

import edu.gatech.seclass.jobcompare6300.database.JobDao;
import edu.gatech.seclass.jobcompare6300.database.JobDatabase;
import edu.gatech.seclass.jobcompare6300.models.Job;

public class JobDao_ImplTest {

    private JobDatabase db;
    private JobDao jobDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, JobDatabase.class)
                .allowMainThreadQueries()
                .build();
        jobDao = db.jobDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsertJob() {
        Job job = new Job("Software Engineer", "Google", "Mountain View", "CA",
                120000, 15000, 140, 10000, 500, 2000, 5000, false);
        jobDao.insertJob(job);

        List<Job> jobs = jobDao.getAllJobs();
        assertEquals(1, jobs.size());
        assertEquals("Google", jobs.get(0).getCompany());
    }

    @Test
    public void testDeleteJob() {
        // Insert a job into the database
        Job job = new Job("Software Engineer", "Google", "Mountain View", "CA",
                120000, 15000, 140, 10000, 500, 2000, 5000, false);
        long jobId = jobDao.insertJob(job); // Get the Job ID after inserting

        // Ensure the job was inserted correctly
        Job insertedJob = jobDao.getJobById(jobId);
        assertNotNull(insertedJob);
        assertEquals(jobId, insertedJob.getJobId());

        // Delete the job from the database
        jobDao.deleteJob(insertedJob);

        // Verify that the job no longer exists
        Job deletedJob = jobDao.getJobById(jobId);
        assertNull(deletedJob); // Should return null as the job is deleted
    }

//    @Test
//    public void testUpdateJob() {
//        Job job = new Job("Software Engineer", "Google", "Mountain View", "CA",
//                120000, 15000, 140, 10000, 500, 2000, 5000, false);
//        jobDao.insertJob(job);
//
//        job.setCompany("Microsoft");
//        jobDao.updateJob(job);
//
//        Job updatedJob = jobDao.getJobById(job.getJobId());
//        assertEquals("Microsoft", updatedJob.getCompany());
//    }

    @Test
    public void testGetJobById() {
        Job job = new Job("Software Engineer", "Google", "Mountain View", "CA",
                120000, 15000, 140, 10000, 500, 2000, 5000, false);
        long jobId = jobDao.insertJob(job);

        Job fetchedJob = jobDao.getJobById(jobId);
        assertNotNull(fetchedJob);
        assertEquals("Google", fetchedJob.getCompany());
    }

    @Test
    public void testGetAllJobs() {
        Job job1 = new Job("Software Engineer", "Google", "Mountain View", "CA",
                120000, 15000, 140, 10000, 500, 2000, 5000, false);
        Job job2 = new Job("Product Manager", "Amazon", "Seattle", "WA",
                130000, 20000, 130, 12000, 700, 3000, 8000, false);

        jobDao.insertJob(job1);
        jobDao.insertJob(job2);

        List<Job> jobs = jobDao.getAllJobs();
        assertEquals(2, jobs.size());
    }
}
