package com.sntcls.alarm;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;

public class AlarmController
{
    public static int AlarmIdFromAlarmIndex(int alarmIndex, Context context)
    {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String alarmListStringTemp = prefs.getString("alarmList", "");
        String[] alarmListTemp = alarmListStringTemp.split(",");
        assert alarmListStringTemp.length() > 0;
        return Integer.parseInt(alarmListTemp[alarmIndex]);
    }

    public static void EnableAlarm(int alarmId, Context context)
    {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        AlarmData alarmData = new AlarmData();
        alarmData.fromString(prefs.getString("" + alarmId, null));

        PendingIntent alarmIntent = alarmData.generateIntent(context, alarmId);
        AlarmManager.AlarmClockInfo aci = new AlarmManager.AlarmClockInfo(alarmData.getNextTime(), alarmIntent);
        alarmManager.setAlarmClock(aci, alarmIntent);
    }

    public static void DisableAlarm(int alarmId, Context context)
    {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        AlarmData alarmData = new AlarmData();
        alarmData.fromString(prefs.getString("" + alarmId, null));

        alarmManager.cancel(alarmData.generateIntent(context, alarmId));
    }

    public static void OnAlarmChanged(int alarmId, Context context)
    {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (prefs.getBoolean("" + alarmId + "e", false))
        {
            DisableAlarm(alarmId, context);
            EnableAlarm(alarmId, context);
        }
    }

    public static void EnableSnoozeAlarm(int alarmId, Context context)
    {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        AlarmData alarmData = new AlarmData();
        alarmData.fromString(prefs.getString("" + alarmId, null));

        long targetTime = System.currentTimeMillis();
        targetTime += ((long)prefs.getInt("pref_snooze_time", 5)) * 60 * 1000; // minutes to milliseconds
        PendingIntent alarmIntent = alarmData.generateIntent(context, alarmId);
        AlarmManager.AlarmClockInfo aci = new AlarmManager.AlarmClockInfo(targetTime, alarmIntent);
        alarmManager.setAlarmClock(aci, alarmIntent);
    }
}
