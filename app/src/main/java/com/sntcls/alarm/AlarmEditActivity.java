package com.sntcls.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class AlarmEditActivity extends AppCompatActivity {

    SharedPreferences prefs;

    int alarmToEditId = -1;
    int alarmToEditIndex = -1;
    int alarmIdCounter;
    List<Integer> alarmList = new ArrayList<Integer>();

    ToggleButton sunToggleButton, monToggleButton, tueToggleButton, wedToggleButton, thuToggleButton, friToggleButton, satToggleButton;
    TimePicker timePicker;
    Switch flashlightSwitch;
    Switch vibrationSwitch;
    Switch ringtoneSwitch;

    public int getAlarmCount(SharedPreferences sharedPrefs)
    {
        String alarmListString = sharedPrefs.getString("alarmList", "");
        if (alarmListString.length() == 0)
            return 0 ;
        String[] alarmListStrings = alarmListString.split(",");
        return alarmListStrings.length;
    }

    private void loadAlarmData(int alarmId)
    {
        AlarmData alarmData = new AlarmData();
        alarmData.fromString(prefs.getString("" + alarmId, null));
        flashlightSwitch.setChecked(alarmData.flashlightEnabled());
        vibrationSwitch.setChecked(alarmData.vibrationEnabled());
        ringtoneSwitch.setChecked(alarmData.ringtoneEnabled());
        timePicker.setHour(alarmData.hour);
        timePicker.setMinute(alarmData.minute);
        sunToggleButton.setChecked(alarmData.sunEnabled());
        monToggleButton.setChecked(alarmData.monEnabled());
        tueToggleButton.setChecked(alarmData.tueEnabled());
        wedToggleButton.setChecked(alarmData.wedEnabled());
        thuToggleButton.setChecked(alarmData.thuEnabled());
        friToggleButton.setChecked(alarmData.friEnabled());
        satToggleButton.setChecked(alarmData.satEnabled());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        alarmIdCounter = prefs.getInt("alarmIdCounter", 0);
        String alarmListStringTemp = prefs.getString("alarmList", "");
        String[] alarmListTemp = alarmListStringTemp.split(",");
        if (alarmListStringTemp.length() > 0)
            for (String alarmId : alarmListTemp)
                alarmList.add(Integer.valueOf(alarmId));

        sunToggleButton = findViewById(R.id.sundayToggle);
        monToggleButton = findViewById(R.id.mondayToggle);
        tueToggleButton = findViewById(R.id.tuesdayToggle);
        wedToggleButton = findViewById(R.id.wednesdayToggle);
        thuToggleButton = findViewById(R.id.thursdayToggle);
        friToggleButton = findViewById(R.id.fridayToggle);
        satToggleButton = findViewById(R.id.saturdayToggle);
        timePicker = findViewById(R.id.timePicker);
        flashlightSwitch = findViewById(R.id.flashlightSwitch);
        vibrationSwitch = findViewById(R.id.vibrationSwitch);
        ringtoneSwitch = findViewById(R.id.ringtoneSwitch);

        alarmToEditIndex = getIntent().getIntExtra("edit", -1);
        if (alarmToEditIndex != -1)
        {
            alarmToEditId = alarmList.get(alarmToEditIndex);
            loadAlarmData(alarmToEditId);
        }
    }

    public void OnSaveButtonClicked(View view)
    {
        AlarmData alarmData = new AlarmData();
        alarmData.hour = timePicker.getHour();
        alarmData.minute = timePicker.getMinute();
        alarmData.components =
            (flashlightSwitch.isChecked() ? 1 << 2 : 0) |
            (vibrationSwitch.isChecked() ? 1 << 1 : 0) |
            (ringtoneSwitch.isChecked() ? 1 << 0 : 0);
        alarmData.daysOfWeek =
            (sunToggleButton.isChecked() ? 1 << 6 : 0) |
            (monToggleButton.isChecked() ? 1 << 5 : 0) |
            (tueToggleButton.isChecked() ? 1 << 4 : 0) |
            (wedToggleButton.isChecked() ? 1 << 3 : 0) |
            (thuToggleButton.isChecked() ? 1 << 2 : 0) |
            (friToggleButton.isChecked() ? 1 << 1 : 0) |
            (satToggleButton.isChecked() ? 1 << 0 : 0);

        SharedPreferences.Editor prefsEditor = prefs.edit();
        Intent returnIntent = new Intent();
        if (alarmToEditId == -1)
        {
            prefsEditor.putString("" + alarmIdCounter, alarmData.toString());
            String currentAlarmList = prefs.getString("alarmList", "");
            prefsEditor.putString("alarmList", currentAlarmList + (currentAlarmList.length() > 0 ? "," : "") + alarmIdCounter);
            prefsEditor.putInt("alarmIdCounter", alarmIdCounter + 1);
            if (prefs.getBoolean("pref_enable_alarm_on_save", true))
                prefsEditor.putBoolean("" + alarmIdCounter + "e", true);
            prefsEditor.apply();
            AlarmController.OnAlarmChanged(alarmIdCounter, view.getContext());

            returnIntent.putExtra("insertedIndex", alarmIdCounter);
        }
        else
        {
            prefsEditor.putString("" + alarmToEditId, alarmData.toString());
            if (prefs.getBoolean("pref_enable_alarm_on_save", true))
                prefsEditor.putBoolean("" + alarmToEditId + "e", true);
            prefsEditor.apply();
            AlarmController.OnAlarmChanged(alarmToEditId, view.getContext());

            returnIntent.putExtra("editedIndex", alarmToEditIndex);
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}