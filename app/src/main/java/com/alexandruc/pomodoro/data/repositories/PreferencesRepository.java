package com.alexandruc.pomodoro.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.alexandruc.pomodoro.data.AppDatabase;
import com.alexandruc.pomodoro.data.dao.PreferencesDao;
import com.alexandruc.pomodoro.data.entities.Preferences;

public class PreferencesRepository {

    private PreferencesDao preferencesDao;

    // Constructor
    public PreferencesRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        preferencesDao = db.preferencesDao();
    }

    public void updateValue(String tag, String value) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            preferencesDao.insert(new Preferences(tag, value));
        });
    }

    public String getValue(String tag) {
        return preferencesDao.getPreference(tag);
    }
}