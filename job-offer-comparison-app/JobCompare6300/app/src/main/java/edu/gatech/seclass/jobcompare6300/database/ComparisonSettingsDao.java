package edu.gatech.seclass.jobcompare6300.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import edu.gatech.seclass.jobcompare6300.models.ComparisonSettings;

@Dao
public interface ComparisonSettingsDao {

    @Query("SELECT * FROM comparison_settings LIMIT 1")
    ComparisonSettings getSettings();

    @Insert
    void insertSettings(ComparisonSettings settings);

    @Update
    void updateSettings(ComparisonSettings settings);
}
