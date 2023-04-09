package com.sntcls.alarm;

import static com.sntcls.alarm.AlarmController.AlarmIdFromAlarmIndex;
import static com.sntcls.alarm.AlarmController.DisableAlarm;
import static com.sntcls.alarm.AlarmController.EnableAlarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static MainActivity latestMainActivity;

    SharedPreferences prefs;

    FloatingActionButton addAlarmButton;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AlarmListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latestMainActivity = this;
        if (!Settings.canDrawOverlays(this))
            RequestOverlayPermissions();

        prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AlarmListAdapter(prefs);
        recyclerView.setAdapter(adapter);

        addAlarmButton = findViewById(R.id.addAlarmButton);
    }

    public static int LAUNCH_ADD_ALARM_ACTIVITY = 1;

    public void OnAddAlarmButtonClicked(View view)
    {
        Intent intent = new Intent(view.getContext(), AlarmEditActivity.class);
        startActivityForResult(intent, LAUNCH_ADD_ALARM_ACTIVITY);
    }
    public void OnSettingsButtonClicked(View view)
    {
        Intent intent = new Intent(view.getContext(), SettingsActivity.class);
        startActivity(intent);
    }

    public void OnEditAlarm(View view, int alarmIndex)
    {
        Intent intent = new Intent(view.getContext(), AlarmEditActivity.class);
        intent.putExtra("edit", alarmIndex);
        startActivityForResult(intent, LAUNCH_ADD_ALARM_ACTIVITY);
    }

    public void OnDeleteAlarm(View view, int alarmIndex)
    {
        adapter.notifyItemRemoved(alarmIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_ADD_ALARM_ACTIVITY)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                int insertedItemIndex = data.getIntExtra("insertedIndex", -1);
                if (insertedItemIndex > -1)
                    adapter.notifyItemInserted(insertedItemIndex);
                int editedItemIndex = data.getIntExtra("editedIndex", -1);
                if (editedItemIndex > -1)
                    adapter.notifyItemChanged(editedItemIndex);
            }
        }
    }

    public static void EnableAlarmWithIndex(int alarmIndex, Context context) { EnableAlarm(AlarmIdFromAlarmIndex(alarmIndex, context), latestMainActivity); }
    public static void DisableAlarmWithIndex(int alarmIndex, Context context) { DisableAlarm(AlarmIdFromAlarmIndex(alarmIndex, context), latestMainActivity); }


    private void RequestOverlayPermissions()
    {
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.info_background_permissions_title))
                        .setMessage(getString(R.string.info_background_permissions_body))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, 0);
                            }
                        });
        alertDialogBuilder.show();
    }
}

