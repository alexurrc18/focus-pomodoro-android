package com.alexandruc.pomodoro.services;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.alexandruc.pomodoro.R;
import com.alexandruc.pomodoro.data.repositories.PreferencesRepository;
import com.alexandruc.pomodoro.helpers.NotificationHelper;

import java.util.ArrayList;

public class SocialMediaBlockerService extends AccessibilityService {

    // variables
    private final ArrayList<String> BLOCKED_APPS = new ArrayList<>();

    private Boolean NOTIFICATIONS = true;

    // notifications
    private NotificationHelper blockAppChannel;

    // preferences
    private PreferencesRepository preferencesRepository;



    @Override
    public void onCreate() {
        super.onCreate();
        preferencesRepository = new PreferencesRepository(getApplication());

        NOTIFICATIONS = Boolean.valueOf(preferencesRepository.getValue("notifications"));

        blockAppChannel = new NotificationHelper("Block Social Media", 2, this);
        blockAppChannel.createNotificationChannel();

        BLOCKED_APPS.add("com.instagram.android");
        BLOCKED_APPS.add("com.facebook.katana");
        BLOCKED_APPS.add("com.twitter.android");
        BLOCKED_APPS.add("com.snapchat.android");
        BLOCKED_APPS.add("com.zhiliaoapp.musically");
        BLOCKED_APPS.add("com.pinterest");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            if(event.getPackageName() != null){
                String currentPackageName = event.getPackageName().toString();

                if (BLOCKED_APPS.contains(currentPackageName)) {
                    boolean isTimerRunning = Boolean.parseBoolean(preferencesRepository.getValue("allow_blocking_apps"));

                    if (isTimerRunning) {
                        blockApp();
                    }
                }
            }
        }
    }




    private void blockApp() {
        performGlobalAction(GLOBAL_ACTION_HOME);
        if(NOTIFICATIONS) {
            blockAppChannel.sendNotification(R.string.block_app_title, R.string.block_app_description);
        }
    }



    @Override
    public void onInterrupt() {

    }

}
