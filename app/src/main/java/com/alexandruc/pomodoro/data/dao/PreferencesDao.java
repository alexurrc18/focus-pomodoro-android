package com.alexandruc.pomodoro.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.alexandruc.pomodoro.data.entities.Preferences;

import java.util.List;

@Dao
public interface PreferencesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Preferences preferences);

    @Query("SELECT value FROM preferences WHERE tag = :tag LIMIT 1")
    String getPreference(String tag);



}
