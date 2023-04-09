package com.sntcls.alarm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHolder> {
//    private List<AlarmData> mDataSet;
    private final SharedPreferences sharedPrefs;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View topBorderView;
        private final TextView textView;
        private final ImageView flashlightIcon;
        private final ImageView vibrationIcon;
        private final ImageView ringtoneIcon;
        private final Switch alarmEnabledSwitch;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    MainActivity.latestMainActivity.OnEditAlarm(v, getAdapterPosition());
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    String[] options = {"Edit", "Delete"};
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: // Edit
                                    MainActivity.latestMainActivity.OnEditAlarm(v, getAdapterPosition());
                                    break;
                                case 1: // Delete
                                    SharedPreferences prefs = view.getContext().getSharedPreferences(view.getContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    List<String> alarmList = new ArrayList<String>(Arrays.asList(prefs.getString("alarmList", null).split(",")));
                                    String alarmId = alarmList.get(getAdapterPosition());
                                    if (prefs.getBoolean(alarmId + "e", false))
                                        AlarmController.DisableAlarm(Integer.parseInt(alarmId), v.getContext());
                                    editor.remove(alarmId);
                                    editor.remove(alarmId + "e");
                                    alarmList.remove(getAdapterPosition());
                                    editor.putString("alarmList", String.join(",", alarmList));
                                    editor.apply();
                                    MainActivity.latestMainActivity.OnDeleteAlarm(v, getAdapterPosition());
                                    break;
                            }
                        }
                    });

// create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return false;
                }
            });
            topBorderView = v.findViewById(R.id.topBorder);
            textView = v.findViewById(R.id.textView);
            flashlightIcon = v.findViewById(R.id.flashlightIcon);
            vibrationIcon = v.findViewById(R.id.vibrationIcon);
            ringtoneIcon = v.findViewById(R.id.ringtoneIcon);
            alarmEnabledSwitch = v.findViewById(R.id.alarmEnabledSwitch);
        }

        public View getTopBorderView() { return topBorderView; }
        public TextView getTextView() {
            return textView;
        }
        public ImageView getFlashlightIcon() { return flashlightIcon; }
        public ImageView getVibrationIcon() {
            return vibrationIcon;
        }
        public ImageView getRingtoneIcon() {
            return ringtoneIcon;
        }
        public Switch getAlarmEnabledSwitch() {
            return alarmEnabledSwitch;
        }
    }

    public AlarmListAdapter(SharedPreferences prefs) {
        sharedPrefs = prefs;
    }

    @NonNull
    @Override
    public AlarmListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmListAdapter.ViewHolder holder, int position) {

        int alarmId = Integer.parseInt(sharedPrefs.getString("alarmList", "").split(",")[position]);

        AlarmData alarmData = new AlarmData();
        alarmData.fromString(sharedPrefs.getString("" + alarmId, null));
        holder.getTextView().setText(
                (alarmData.hour < 10 ? "0" : "") + alarmData.hour + ":" +
                (alarmData.minute < 10 ? "0" : "") + alarmData.minute);
        holder.getTopBorderView().setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);

        holder.getAlarmEnabledSwitch().setChecked(sharedPrefs.getBoolean("" + alarmId + "e", false));

        if (alarmData.flashlightEnabled())
            holder.getFlashlightIcon().setColorFilter(Color.parseColor("#DDDDDD"));
        else
            holder.getFlashlightIcon().setColorFilter(Color.parseColor("#444444"));
        if (alarmData.vibrationEnabled())
            holder.getVibrationIcon().setColorFilter(Color.parseColor("#DDDDDD"));
        else
            holder.getVibrationIcon().setColorFilter(Color.parseColor("#444444"));
        if (alarmData.ringtoneEnabled())
            holder.getRingtoneIcon().setColorFilter(Color.parseColor("#DDDDDD"));
        else
            holder.getRingtoneIcon().setColorFilter(Color.parseColor("#444444"));

        holder.getAlarmEnabledSwitch().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((Switch)view).isChecked())
                    MainActivity.EnableAlarmWithIndex(holder.getAdapterPosition(), view.getContext());
                else
                    MainActivity.DisableAlarmWithIndex(holder.getAdapterPosition(), view.getContext());

                SharedPreferences prefs = view.getContext().getSharedPreferences(view.getContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("" + AlarmController.AlarmIdFromAlarmIndex(holder.getAdapterPosition(), view.getContext()) + "e", ((Switch)view).isChecked());
                editor.apply();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        String alarmListString = sharedPrefs.getString("alarmList", "");
        if (alarmListString.length() == 0)
            return 0 ;
        String[] alarmListStrings = alarmListString.split(",");
        return alarmListStrings.length;
    }
}
