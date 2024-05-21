package com.example.mydiary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

public class SettingsScreen extends Fragment {

    public SettingsScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create view
        View view = inflater.inflate(R.layout.fragment_settings_screen, container, false);

        // Setup fontSizeSelector
        Spinner fontSizeSelector = view.findViewById(R.id.font_size_selector);

        ArrayAdapter<String> fontSizeAdapter;
        fontSizeAdapter = new ArrayAdapter<>(
                view.getContext(),
                R.layout.font_size_selector_child,
                getResources().getStringArray(R.array.font_size_array)
        );

        fontSizeSelector.setAdapter(fontSizeAdapter);

        Activity mainActivity = getActivity();

        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("DiaryAppPreferences", mainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int themeId = sharedPreferences.getInt("ThemeId", R.style.AppThemeMedium);

        if (themeId == R.style.AppThemeLarge) {
            fontSizeSelector.setSelection(2);
        }
        if (themeId == R.style.AppThemeMedium) {
            fontSizeSelector.setSelection(1);
        }
        if (themeId == R.style.AppThemeSmall) {
            fontSizeSelector.setSelection(0);
        }

        int reminderHour = sharedPreferences.getInt("ReminderHour", 22);
        int reminderMinute = sharedPreferences.getInt("ReminderMinute", 0);

        TextView reminderTimeContainer = view.findViewById(R.id.reminder_time_container);
        reminderTimeContainer.setText(String.format(Locale.JAPAN, "%d:%02d", reminderHour, reminderMinute));

        // Get parentFragmentManager
        FragmentManager parentFragmentManager = getParentFragmentManager();

        // returnButton pressed
        ImageButton returnButton = view.findViewById(R.id.return_button);
        returnButton.setOnClickListener( v -> parentFragmentManager.popBackStack());

        // reminderButton pressed
        Button reminderButton = view.findViewById(R.id.reminder_button);
        reminderButton.setOnClickListener( v -> {
            Calendar currentTime = Calendar.getInstance();
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog;
            timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    editor.putInt("ReminderHour", selectedHour);
                    editor.putInt("ReminderMinute", selectedMinute);
                    editor.apply();
                    reminderTimeContainer.setText(String.format(Locale.JAPAN, "%d:%02d", selectedHour, selectedMinute));
                    currentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    currentTime.set(Calendar.MINUTE, selectedMinute);
                    currentTime.set(Calendar.SECOND, 0);
                    cancelNotification(getContext());
                    scheduleNotification(getContext(), currentTime.getTimeInMillis());
                }
            }, hour, minute, true); //Yes 24 hour time
            timePickerDialog.show();
        });

        // fontSizeButton pressed
        Button fontSizeButton = view.findViewById(R.id.font_size_button);
        fontSizeButton.setOnClickListener( v -> {
            int chosenFontSize = 0;

            chosenFontSize = fontSizeSelector.getSelectedItemPosition();

            switch(chosenFontSize) {
                case 0:
                    editor.putInt("ThemeId", R.style.AppThemeSmall);
                    break;
                case 1:
                    editor.putInt("ThemeId", R.style.AppThemeMedium);
                    break;
                case 2:
                    editor.putInt("ThemeId", R.style.AppThemeLarge);
                    break;
            }
            editor.apply();

            mainActivity.finish();
            mainActivity.overridePendingTransition(0, 0);
            startActivity(mainActivity.getIntent());
            mainActivity.overridePendingTransition(0, 0);
        });

        return view;
    }

    public static void scheduleNotification(Context context, long time) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 42, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Schedule notification
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pending);
    }

    public static void cancelNotification(Context context) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 42, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Cancel notification
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pending);
    }
}