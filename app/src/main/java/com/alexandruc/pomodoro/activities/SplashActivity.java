package com.alexandruc.pomodoro.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alexandruc.pomodoro.R;
import com.alexandruc.pomodoro.data.AppDatabase;
import com.alexandruc.pomodoro.data.dao.PreferencesDao;
import com.alexandruc.pomodoro.data.entities.Preferences;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    AppDatabase db;
    PreferencesDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getDatabase(this);
        dao = db.preferencesDao();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            String existingPref = dao.getPreference("focus_duration");
            if (existingPref == null) {
                dao.insert(new Preferences("notifications", "TRUE"));
                dao.insert(new Preferences("vibration", "TRUE"));
                dao.insert(new Preferences("block_social_media", "FALSE"));
                dao.insert(new Preferences("sound_effects", "TRUE"));
                dao.insert(new Preferences("focus_duration", "25"));
                dao.insert(new Preferences("break_duration", "5"));
                dao.insert(new Preferences("streak_number", "0"));

                dao.insert(new Preferences("allow_blocking_apps", "FALSE"));
            }
        });

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.static_anim);
            finish();
        }, 5000);

    }
}