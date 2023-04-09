package com.sntcls.alarm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class AlarmReceiver extends BroadcastReceiver
{
    public static final String EXTRA_ALARM_ID = "com.sntcls.EXTRA_ALARM_ID";

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(Context context, Intent intent)
    {
        WakeLocker.acquire(context);

        int alarmId = intent.getIntExtra(EXTRA_ALARM_ID, -1);
        assert alarmId != -1;
        AlarmController.EnableAlarm(alarmId, context.getApplicationContext()); // make it repeat

        Intent ringer = new Intent(context, AlarmRinger.class);
        ringer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ringer.putExtra(EXTRA_ALARM_ID, alarmId);
        context.startActivity(ringer);

        WakeLocker.release();
    }
}