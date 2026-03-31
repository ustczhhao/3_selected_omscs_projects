package edu.gatech.seclass.jobcompare6300.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;

import java.util.List;

import edu.gatech.seclass.jobcompare6300.models.Job;

@Dao
public interface JobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertJob(Job job); // Returns the generated ID

    @Update
    void updateJob(Job job);

    @Delete
    void deleteJob(Job job);

    @Query("DELETE FROM jobs")
    void deleteAllJobs();


    @Query("SELECT * FROM jobs WHERE jobId = :id")
    Job getJobById(long id);

    @Query("SELECT * FROM jobs WHERE isCurrentJob = :isCurrent LIMIT 1")
    Job getCurrentJob(boolean isCurrent);


    @Query("SELECT * FROM jobs")
    List<Job> getAllJobs();
    @Query("SELECT * FROM jobs WHERE isCurrentJob = 0")
    List<Job> getAllJobOffers();

    @Query("SELECT * FROM jobs ORDER BY jobId DESC LIMIT 1")
    Job getLatestJobOffer();


}