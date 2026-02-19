package com.alexandruc.pomodoro.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.alexandruc.pomodoro.data.dao.PreferencesDao;
import com.alexandruc.pomodoro.data.dao.TasksDao;
import com.alexandruc.pomodoro.data.entities.Preferences;
import com.alexandruc.pomodoro.data.entities.Tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Tasks.class, Preferences.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;

    public abstract TasksDao tasksDao();
    public abstract PreferencesDao preferencesDao();

    private static volatile AppDatabase INSTANCE;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "pomodoro_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
