package com.sntcls.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String alarmListStringTemp = prefs.getString("alarmList", "");
        if (alarmListStringTemp.length() == 0)
            return;
        String[] alarmListTemp = alarmListStringTemp.split(",");
        for (String alarmIdString : alarmListTemp)
        {
            int alarmId = Integer.parseInt(alarmIdString);
            if (!prefs.getBoolean("" + alarmId + "e", false))
                continue;
            AlarmController.EnableAlarm(alarmId, context);
        }
    }
}
