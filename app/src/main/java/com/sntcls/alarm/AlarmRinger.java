package com.sntcls.alarm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null)
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (ringtone == null)
         ringtone = RingtoneManager.getRingtone(context, alarmUri);
        if (on)
            ringtone.play();
        else
            ringtone.stop();
    }

    Button turnOffAlarmButton;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringer);

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
            }
        });

        int alarmId = getIntent().getIntExtra(AlarmReceiver.EXTRA_ALARM_ID, -1);
        assert alarmId != -1;

        SharedPreferences prefs = this.getSharedPreferences(this.getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
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
    }
}