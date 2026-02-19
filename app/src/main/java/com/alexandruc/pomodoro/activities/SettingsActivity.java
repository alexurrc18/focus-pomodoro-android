package com.alexandruc.pomodoro.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alexandruc.pomodoro.R;
import com.alexandruc.pomodoro.data.repositories.PreferencesRepository;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {


    // preferences
    PreferencesRepository preferencesRepository;

    // auto complete lists
    String[] focusList = new String[]{"1 min", "5 min", "10 min", "15 min", "20 min", "25 min", "30 min", "35 min", "40 min", "45 min", "50 min"};
    String[] breakList = new String[]{"5 min", "10 min", "15 min"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        preferencesRepository = new PreferencesRepository(getApplication());



        // header
        // ui elements
        Button goBackButton = findViewById(R.id.backButton);
        goBackButton.setOnClickListener(v -> finish() );





        // notifications
        SwitchCompat notificationsSwitch = findViewById(R.id.notificationsSwitch);
        notificationsSwitch.setChecked(Boolean.parseBoolean(preferencesRepository.getValue("notifications")));
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) preferencesRepository.updateValue("notifications", "TRUE");
            else preferencesRepository.updateValue("notifications", "FALSE");
        });






        // vibration
        SwitchCompat vibrationSwitch = findViewById(R.id.vibrationSwitch);
        vibrationSwitch.setChecked(Boolean.parseBoolean(preferencesRepository.getValue("vibration")));
        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) preferencesRepository.updateValue("vibration", "TRUE");
            else preferencesRepository.updateValue("vibration", "FALSE");
        });





        // social media
        SwitchCompat socialMediaSwitch = findViewById(R.id.socialMediaSwitch);
        socialMediaSwitch.setChecked(Boolean.parseBoolean(preferencesRepository.getValue("block_social_media")));
        socialMediaSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_block_social_media);
                dialog.setCancelable(false);

                Window window = dialog.getWindow();
                if (window != null) {
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }

                MaterialButton acceptButton = dialog.findViewById(R.id.acceptButton);
                MaterialButton cancelButton = dialog.findViewById(R.id.cancelButton);

                acceptButton.setOnClickListener(v -> {
                    preferencesRepository.updateValue("block_social_media", "TRUE");
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss();
                });

                cancelButton.setOnClickListener(v -> {
                    socialMediaSwitch.setChecked(false);
                    dialog.dismiss();
                });

                dialog.show();
            }
           else {
               preferencesRepository.updateValue("block_social_media", "FALSE");
            }
        });





        // timer
        // focus
        AutoCompleteTextView focusAutoCompleteMenu = findViewById(R.id.focusAutoCompleteMenu);
        focusAutoCompleteMenu.setText(preferencesRepository.getValue("focus_duration") + "min");
        ArrayAdapter<String> adapterFocus = new ArrayAdapter<>(this, R.layout.list_items, focusList);
        focusAutoCompleteMenu.setAdapter(adapterFocus);

        focusAutoCompleteMenu.setOnItemClickListener((parent, view, position, id) -> {
            String selectedText = (String) parent.getItemAtPosition(position);
            String minutesOnly = selectedText.split(" ")[0];
            preferencesRepository.updateValue("focus_duration", minutesOnly);
        });



        // break
        AutoCompleteTextView breakAutoCompleteMenu = findViewById(R.id.breakAutoCompleteMenu);
        breakAutoCompleteMenu.setText(preferencesRepository.getValue("break_duration") + "min");
        ArrayAdapter<String> adapterBreak = new ArrayAdapter<>(this, R.layout.list_items, breakList);
        breakAutoCompleteMenu.setAdapter(adapterBreak);

        breakAutoCompleteMenu.setOnItemClickListener((parent, view, position, id) -> {
            String selectedText = (String) parent.getItemAtPosition(position);
            String minutesOnly = selectedText.split(" ")[0];
            preferencesRepository.updateValue("break_duration", minutesOnly);
        });






    }
}