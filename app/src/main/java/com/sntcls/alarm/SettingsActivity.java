package com.sntcls.alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    public void OnBackArrowClicked(View view)
    {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        // ads
        SharedPreferences prefs = this.getSharedPreferences(this.getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (prefs.getBoolean("pref_ads_enabled", true))
        {
            List<View> adViews = new ArrayList<>();
            adViews.add(findViewById(R.id.adView0));
            AdController.ShowGoogleAdBanners(this, adViews);
        }
        else
        {
            ((ViewGroup)findViewById(R.id.adContainer)).removeAllViews();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        boolean canUseAlarmType;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

            final ListPreference listPreference = findPreference("ringtone_list_preference");
            final SwitchPreferenceCompat enableAlarmOnSavePreference = findPreference("enable_alarm_on_save_preference");
            final SeekBarPreference snoozeTimePreference = findPreference("snooze_time_preference");
            final SwitchPreferenceCompat adsEnabledPreference = findPreference("ads_switch_preference");

            RingtoneManager manager = new RingtoneManager(getContext());
            canUseAlarmType = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) != null;
            manager.setType(canUseAlarmType ? RingtoneManager.TYPE_ALARM : RingtoneManager.TYPE_NOTIFICATION);
            Cursor cursor = manager.getCursor();
            ArrayList<String> ringtoneList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                ringtoneList.add(title);
            }

            String[] ringtoneListArray = new String[ringtoneList.size()];
            ringtoneListArray = ringtoneList.toArray(ringtoneListArray);
            CharSequence[] entries = ringtoneListArray;
            CharSequence[] entryValues = ringtoneListArray;
            listPreference.setEntries(entries);
            listPreference.setEntryValues(entryValues);

            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ListPreference newPreference = (ListPreference) preference;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("prefs_ringtone_can_use_alarm_type", canUseAlarmType);
                    int newIndex = newPreference.findIndexOfValue((String) newValue);
                    editor.putInt("prefs_ringtone_index", newIndex);
                    editor.apply();
                    return true;
                }
            });

            enableAlarmOnSavePreference.setChecked(prefs.getBoolean("pref_enable_alarm_on_save", true));
            enableAlarmOnSavePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("pref_enable_alarm_on_save", (Boolean)newValue);
                    editor.apply();
                    return true;
                }
            });

            snoozeTimePreference.setMax(120);
            snoozeTimePreference.setMin(1);
            snoozeTimePreference.setValue(prefs.getInt("pref_snooze_time", 5));
            snoozeTimePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("pref_snooze_time", (Integer)newValue);
                    editor.apply();
                    return true;
                }
            });

            adsEnabledPreference.setChecked(prefs.getBoolean("pref_ads_enabled", true));
            adsEnabledPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("pref_ads_enabled", (Boolean)newValue);
                    editor.apply();
                    return true;
                }
            });
        }
    }
}