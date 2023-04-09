package com.sntcls.alarm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.camera2.CameraManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AlarmRinger extends AppCompatActivity {

    private AlarmData alarmData = null;

    private CameraManager cameraManager = null;
    private String cameraId = null;

    private boolean setFlashLight(Context context, boolean on)
    {
        if (cameraManager == null)
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (cameraId == null) {
            try {
                cameraId = cameraManager.getCameraIdList()[0];
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            cameraManager.setTorchMode(cameraId, on);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void setVibrator(Context context, boolean on)
    {
        Vibrator vibrator = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
        if (on)
            vibrator.vibrate(4000);
        else
            vibrator.cancel();
    }

    Ringtone ringtone = null;
    private void setRingtone(Context context, boolean on)
    {
        SharedPreferences prefs = this.getSharedPreferences(this.getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (ringtone == null)
        {
            Uri alarmUri;
            if (prefs.getInt("prefs_ringtone_index", -1) == -1)
            {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alarmUri == null)
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            else
            {
                RingtoneManager manager = new RingtoneManager(this);
                manager.setType(prefs.getBoolean("prefs_ringtone_can_use_alarm_type", true) ? RingtoneManager.TYPE_ALARM : RingtoneManager.TYPE_NOTIFICATION);
                Cursor cursor = manager.getCursor();
                cursor.moveToPosition(prefs.getInt("prefs_ringtone_index", -1));
                alarmUri = manager.getRingtoneUri(cursor.getPosition());
            }
            ringtone = RingtoneManager.getRingtone(context, alarmUri);
        }
        if (on)
            ringtone.play();
        else
            ringtone.stop();
    }

    FloatingActionButton turnOffAlarmButton;
    FloatingActionButton snoozeButton;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringer);

        // make it visible on lock screen
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        SharedPreferences prefs = this.getSharedPreferences(this.getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        int alarmId = getIntent().getIntExtra(AlarmReceiver.EXTRA_ALARM_ID, -1);
        assert alarmId != -1;

        turnOffAlarmButton = findViewById(R.id.turnOffAlarmButton);
        turnOffAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alarmData.flashlightEnabled())
                    setFlashLight(view.getContext(), false);
                if (alarmData.vibrationEnabled())
                    setVibrator(view.getContext(), false);
                if (alarmData.ringtoneEnabled())
                    setRingtone(view.getContext(), false);
                finish();
            }
        });


        snoozeButton = findViewById(R.id.snoozeButton);
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alarmData.flashlightEnabled())
                    setFlashLight(view.getContext(), false);
                if (alarmData.vibrationEnabled())
                    setVibrator(view.getContext(), false);
                if (alarmData.ringtoneEnabled())
                    setRingtone(view.getContext(), false);
                AlarmController.EnableSnoozeAlarm(alarmId, view.getContext());
                finish();
            }
        });

        alarmData = new AlarmData();
        alarmData.fromString(prefs.getString("" + alarmId, null));

        if (alarmData.flashlightEnabled())
        {
            boolean flashLightWorks = setFlashLight(this.getApplicationContext(), true);
            if (!flashLightWorks)
                Toast.makeText(this, "Could not start flashlight.", Toast.LENGTH_LONG).show();
        }
        if (alarmData.vibrationEnabled())
            setVibrator(this, true);
        if (alarmData.ringtoneEnabled())
            setRingtone(this, true);

        // ads
        if (prefs.getBoolean("pref_ads_enabled", true))
        {
            List<View> adViews = new ArrayList<>();
            adViews.add(findViewById(R.id.adView0));
            adViews.add(findViewById(R.id.adView1));
            adViews.add(findViewById(R.id.adView2));
            adViews.add(findViewById(R.id.adView3));
            AdController.ShowGoogleAdBanners(this, adViews);
        }
        else
        {
            ((ViewGroup)findViewById(R.id.adContainer)).removeAllViews();
        }
    }
}