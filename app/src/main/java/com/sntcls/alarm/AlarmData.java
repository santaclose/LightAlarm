package com.sntcls.alarm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmData
{
    public int hour;
    public int minute;
    public int daysOfWeek;
    public int components;

    public String toString()
    {
        String result = "";
        result += hour;
        result += ',';
        result += minute;
        result += ',';
        result += daysOfWeek;
        result += ',';
        result += components;
        return result;
    }
    public void fromString(String stringValue)
    {
        String[] parts = stringValue.split(",");
        assert parts.length == 4;
        this.hour = Integer.valueOf(parts[0]);
        this.minute = Integer.valueOf(parts[1]);
        this.daysOfWeek = Integer.valueOf(parts[2]);
        this.components = Integer.valueOf(parts[3]);
    }
    public boolean flashlightEnabled() { return (components & (1 << 2)) != 0; }
    public boolean vibrationEnabled() { return (components & (1 << 1)) != 0; }
    public boolean ringtoneEnabled() { return (components & (1 << 0)) != 0; }

    public boolean sunEnabled() { return (daysOfWeek & (1 << 6)) != 0; }
    public boolean monEnabled() { return (daysOfWeek & (1 << 5)) != 0; }
    public boolean tueEnabled() { return (daysOfWeek & (1 << 4)) != 0; }
    public boolean wedEnabled() { return (daysOfWeek & (1 << 3)) != 0; }
    public boolean thuEnabled() { return (daysOfWeek & (1 << 2)) != 0; }
    public boolean friEnabled() { return (daysOfWeek & (1 << 1)) != 0; }
    public boolean satEnabled() { return (daysOfWeek & (1 << 0)) != 0; }

    public PendingIntent generateIntent(Context context, int alarmId)
    {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId);
        int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        pendingIntentFlags |= android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0;
        return PendingIntent.getBroadcast(context, alarmId, intent, pendingIntentFlags);
    }

    public long getNextTime()
    {
        Calendar now = Calendar.getInstance();
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, this.hour);
        next.set(Calendar.MINUTE, this.minute);
        next.set(Calendar.SECOND, 0);

        while (now.after(next))
            next.add(Calendar.DATE, 1);

        int nextDay = next.get(Calendar.DAY_OF_WEEK) - 1; // index on 0-6, rather than the 1-7 returned by Calendar

        for (int i = 0; i < 7 && (daysOfWeek & (1 << (6 - nextDay))) == 0; i++) {
            nextDay++;
            nextDay %= 7;
        }

        next.set(Calendar.DAY_OF_WEEK, nextDay + 1); // + 1 = back to 1-7 range

        while (now.after(next))
            next.add(Calendar.DATE, 7);

        return next.getTimeInMillis();
    }

    public long getTime()
    {
        long time;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, this.hour);
        calendar.set(Calendar.MINUTE, this.minute);
        if (sunEnabled()) calendar.add(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        if (monEnabled()) calendar.add(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if (tueEnabled()) calendar.add(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        if (wedEnabled()) calendar.add(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        if (thuEnabled()) calendar.add(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        if (friEnabled()) calendar.add(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        if (satEnabled()) calendar.add(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
        if (System.currentTimeMillis() > time)
        {
            if (calendar.AM_PM == 0)
                time = time + (1000 * 60 * 60 * 12);
            else
                time = time + (1000 * 60 * 60 * 24);
        }
        return time;
    }
}
