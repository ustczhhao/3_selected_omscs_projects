package edu.gatech.seclass.jobcompare6300.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.gatech.seclass.jobcompare6300.models.Job;
import edu.gatech.seclass.jobcompare6300.models.ComparisonSettings;

@Database(entities = {Job.class, ComparisonSettings.class}, version = 4, exportSchema = false)
public abstract class JobDatabase extends RoomDatabase {

    private static volatile JobDatabase INSTANCE;

    public abstract JobDao jobDao();
    public abstract ComparisonSettingsDao comparisonSettingsDao(); // New DAO

    public static JobDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (JobDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    JobDatabase.class, "job_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}