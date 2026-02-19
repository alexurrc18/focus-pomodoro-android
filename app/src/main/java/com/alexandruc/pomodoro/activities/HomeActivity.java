package com.alexandruc.pomodoro.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexandruc.pomodoro.R;
import com.alexandruc.pomodoro.adapters.TasksAdapter;
import com.alexandruc.pomodoro.data.AppDatabase;
import com.alexandruc.pomodoro.data.entities.Tasks;
import com.alexandruc.pomodoro.data.repositories.PreferencesRepository;
import com.alexandruc.pomodoro.helpers.NotificationHelper;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class HomeActivity extends AppCompatActivity {

    private TasksAdapter tasksAdapter;
    private AppDatabase appDatabase;
    private KonfettiView konfettiView;
    private TextView timerTextView, timerStatusTextView, streakTextView;
    private MaterialButton startTimerButton;
    private MaterialButton soundEffectsButton;
    private MaterialButton saveTaskButton;
    private EditText taskInput;



    // preferences
    private static long FOCUS_DURATION_MS = 25*60000;
    private static long BREAK_DURATION_MS = 5*60000;
    private static String BREAK_DURATION = "5:00";
    private static String FOCUS_DURATION = "25:00";
    private int STREAK_NUMBER = 0;
    private String POMODORO_STATE = "FOCUS";
    private Boolean SOUND_EFFECTS = true;
    private Boolean NOTIFICATIONS = true;
    private Boolean BLOCK_APPS = false;
    private Boolean VIBRATION = true;



    // timer, sound effects, notifications, vibration, block social media apps, preferences repository
    private Vibrator vibrator;
    private CountDownTimer timer;
    private MediaPlayer soundEffect;
    private boolean isTimerRunning = false;
    private NotificationHelper timerChannel;
    private PreferencesRepository preferencesRepository;




    // start timer
    private void startTimer(long time_in_ms){
        //button
        startTimerButton.setBackgroundColor(MaterialColors.getColor(startTimerButton, R.attr.darkButton));
        if(POMODORO_STATE.equals("FOCUS"))  startTimerButton.setText(R.string.cancel);
        else if(POMODORO_STATE.equals("BREAK")) startTimerButton.setText(R.string.skip);
        setButtonListener("CANCEL");

        //timer
        isTimerRunning = true;
        sendTimerStateToDatabase();


        timer = new CountDownTimer(time_in_ms, 1000) {
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                timerTextView.setText(f.format(min) + ":" + f.format(sec));
            }

            public void onFinish() {
                // timer state
                isTimerRunning = false;
                sendTimerStateToDatabase();

                // button
                setButtonListener("TIMER");
                startTimerButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                startTimerButton.setText(R.string.start);

                // focus finish style
                if(POMODORO_STATE.equals("FOCUS")){

                    // confetti
                    int[] location = new int[2];
                    timerTextView.getLocationOnScreen(location);
                    float centerX = location[0] + (timerTextView.getWidth() / 2f);
                    float centerY = location[1] + (timerTextView.getHeight() / 2f);
                    explodeConfetti(centerX, centerY);

                    // sound effect
                    if(SOUND_EFFECTS == true) soundEffect.start();

                    //vibration
                    if(VIBRATION) {
                        final VibrationEffect vibrationEffect;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.EFFECT_DOUBLE_CLICK);
                        } else {
                            vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);
                        }
                        vibrator.cancel();
                        vibrator.vibrate(vibrationEffect);
                    }
                }

                // change pomodoro state
                if(POMODORO_STATE.equals("BREAK")) {
                    if(NOTIFICATIONS) timerChannel.sendNotification(R.string.focus_time_title, R.string.focus_time_description);

                    timerStatusTextView.setText(R.string.start_the_timer);
                    timerTextView.setText(FOCUS_DURATION);
                    POMODORO_STATE = "FOCUS";
                } else if(POMODORO_STATE.equals("FOCUS")){
                    if(NOTIFICATIONS) timerChannel.sendNotification(R.string.break_time_title, R.string.break_time_description);

                    STREAK_NUMBER++;
                    preferencesRepository.updateValue("streak_number", String.valueOf(STREAK_NUMBER));
                    timerTextView.setText(BREAK_DURATION);
                    timerStatusTextView.setText(R.string.time_for_a_break);
                    streakTextView.setText(String.valueOf(STREAK_NUMBER));
                    POMODORO_STATE = "BREAK";
                }
            }

        };

        timer.start();
    }

    // cancel timer
    private void cancelTimer(){
        isTimerRunning = false;
        sendTimerStateToDatabase();

        startTimerButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        startTimerButton.setText(R.string.start);
        setButtonListener("TIMER");
        if(POMODORO_STATE.equals("FOCUS")){
            timer.cancel();

            STREAK_NUMBER = 0;
            preferencesRepository.updateValue("streak_number", "0");
            streakTextView.setText(String.valueOf(STREAK_NUMBER));

            timerStatusTextView.setText(R.string.start_the_timer);
            timerTextView.setText(FOCUS_DURATION);
        } else if(POMODORO_STATE.equals("BREAK")){
            timer.cancel();
            POMODORO_STATE = "FOCUS";
            timerStatusTextView.setText(R.string.start_the_timer);
            timerTextView.setText(FOCUS_DURATION);
        }
    }

    //set button type
    private void setButtonListener(String TYPE){
        startTimerButton.setOnClickListener(v -> {
            if (TYPE.equals("TIMER")) {
                if (POMODORO_STATE.equals("FOCUS")) {
                    timerStatusTextView.setText(R.string.time_to_focus);
                    startTimer(FOCUS_DURATION_MS);
                } else if (POMODORO_STATE.equals("BREAK")) {
                    timerStatusTextView.setText(R.string.take_a_break);
                    startTimer(BREAK_DURATION_MS);
                }
            } else if(TYPE.equals("CANCEL")){
                cancelTimer();
            }
        });
    }

    // confetti animation
    public void explodeConfetti(float x, float y) {
        EmitterConfig emitterConfig = new Emitter(100L, TimeUnit.MILLISECONDS).max(100);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .spread(360)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE))
                        .colors(Arrays.asList(
                                        ContextCompat.getColor(this, R.color.green),
                                        ContextCompat.getColor(this,R.color.blue),
                                        ContextCompat.getColor(this,R.color.orange),
                                        ContextCompat.getColor(this,R.color.yellow)
                        ))
                        .setSpeedBetween(0f, 30f)
                        .position(new Position.Absolute(x, y))
                        .build());
    }

    private void sendTimerStateToDatabase(){
        if(BLOCK_APPS && isTimerRunning && POMODORO_STATE.equals("FOCUS"))
            preferencesRepository.updateValue("allow_blocking_apps", "TRUE");
        else
            preferencesRepository.updateValue("allow_blocking_apps", "FALSE");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferencesRepository = new PreferencesRepository(getApplication());

        //notifications
        timerChannel = new NotificationHelper("Timer", 1, this);
        timerChannel.createNotificationChannel();

        NOTIFICATIONS = Boolean.valueOf(preferencesRepository.getValue("notifications"));

        // app blocker service
        BLOCK_APPS = Boolean.valueOf(preferencesRepository.getValue("block_social_media"));

        //timer
        timerTextView = findViewById(R.id.timer);
        streakTextView = findViewById(R.id.streakBadgeText);
        startTimerButton = findViewById(R.id.startTimer);
        timerStatusTextView = findViewById(R.id.status);
        konfettiView = findViewById(R.id.konfettiView);

        FOCUS_DURATION_MS = Long.parseLong(preferencesRepository.getValue("focus_duration")) * 60000;
        BREAK_DURATION_MS = Long.parseLong(preferencesRepository.getValue("break_duration")) * 60000;
        STREAK_NUMBER = Integer.parseInt(preferencesRepository.getValue("streak_number"));
        FOCUS_DURATION = preferencesRepository.getValue("focus_duration") + ":00";
        BREAK_DURATION = preferencesRepository.getValue("break_duration") + ":00";
        streakTextView.setText(String.valueOf(STREAK_NUMBER));
        timerTextView.setText(FOCUS_DURATION);


        setButtonListener("TIMER");



        //header
        MaterialButton settingsButton = findViewById(R.id.settingsButton);
        soundEffectsButton = findViewById(R.id.soundEffectsButton);
        SOUND_EFFECTS = Boolean.valueOf(preferencesRepository.getValue("sound_effects"));
        if(!SOUND_EFFECTS){
            soundEffectsButton.setIconResource(R.drawable.icon_bell_slash);
        } else {
            soundEffectsButton.setIconResource(R.drawable.icon_bell);
        }

        soundEffectsButton.setOnClickListener( v -> {
            if(SOUND_EFFECTS == Boolean.TRUE){
                preferencesRepository.updateValue("sound_effects", "FALSE");
                soundEffectsButton.setIconResource(R.drawable.icon_bell_slash);
                SOUND_EFFECTS = Boolean.FALSE;
            } else {
                preferencesRepository.updateValue("sound_effects", "TRUE");
                soundEffectsButton.setIconResource(R.drawable.icon_bell);
                SOUND_EFFECTS = Boolean.TRUE;
            }
        });

        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));


        // sound effects
        soundEffect = MediaPlayer.create(this, R.raw.notification_sound);


        // vibration
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        VIBRATION = Boolean.valueOf(preferencesRepository.getValue("vibration"));


        // tasks
        // UI elements
        RecyclerView tasksRecycler = findViewById(R.id.tasksRecyclerView);
        tasksRecycler.setLayoutManager(new LinearLayoutManager(this));
        tasksRecycler.setHasFixedSize(true);

        tasksAdapter = new TasksAdapter();

        tasksAdapter.setOnItemClickListener(new TasksAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(Tasks task) {
                AppDatabase.databaseWriteExecutor.execute(() -> appDatabase.tasksDao().delete(task));
            }

            @Override
            public void onTaskCheck(Tasks task, boolean isChecked) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    task.setCompleted(isChecked);
                    appDatabase.tasksDao().update(task);
                });
            }
        });


        tasksRecycler.setAdapter(tasksAdapter);
        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        appDatabase.tasksDao().getAllTasks().observe(this, tasks -> tasksAdapter.setTasks(tasks));

        // add task dialog
        MaterialButton addTaskButton = findViewById(R.id.addTask);
        Dialog dialog = new Dialog(HomeActivity.this);


        addTaskButton.setOnClickListener(v -> {
            dialog.setContentView(R.layout.dialog_addtask);

            taskInput = dialog.findViewById(R.id.taskInput);

            // save task
            saveTaskButton = dialog.findViewById(R.id.saveTaskButton);
            saveTaskButton.setOnClickListener(v1 -> {
                String description = taskInput.getText().toString().trim();
                if(!description.isEmpty()){
                    Tasks newTask = new Tasks(description, false);

                    AppDatabase.databaseWriteExecutor.execute(() -> appDatabase.tasksDao().insert(newTask));

                    taskInput.setText("");
                    dialog.dismiss();

                    dialog.dismiss();
                } else {
                    taskInput.setError("Please write a task.");
                }
            });

            dialog.show();
            Window window = dialog.getWindow();
            if (window != null) window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        });


    }


    @Override
    protected void onResume() {
        super.onResume();

        preferencesRepository = new PreferencesRepository(getApplication());
        BLOCK_APPS = Boolean.valueOf(preferencesRepository.getValue("block_social_media"));
        sendTimerStateToDatabase();

        // timer
        FOCUS_DURATION_MS = Long.parseLong(preferencesRepository.getValue("focus_duration")) * 60000;
        BREAK_DURATION_MS = Long.parseLong(preferencesRepository.getValue("break_duration")) * 60000;
        FOCUS_DURATION = preferencesRepository.getValue("focus_duration") + ":00";
        BREAK_DURATION = preferencesRepository.getValue("break_duration") + ":00";

        if(!isTimerRunning){
            if (POMODORO_STATE.equals("FOCUS")) {
                timerTextView.setText(FOCUS_DURATION);
            } else if(POMODORO_STATE.equals("BREAK")) {
                timerStatusTextView.setText(R.string.take_a_break);
                timerTextView.setText(BREAK_DURATION);
            }
            setButtonListener("TIMER");
        }

        // notifications
        NOTIFICATIONS = Boolean.valueOf(preferencesRepository.getValue("notifications"));

    }



}
