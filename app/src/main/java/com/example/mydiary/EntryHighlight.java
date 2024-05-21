package com.example.mydiary;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class EntryHighlight extends Fragment {

    public EntryHighlight() {
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
        View view = inflater.inflate(R.layout.fragment_entry_highlight, container, false);

        TextView yearText = view.findViewById(R.id.year_text);
        TextView monthText = view.findViewById(R.id.month_text);
        TextView dayText = view.findViewById(R.id.day_text);
        TextView contentText = view.findViewById(R.id.entry_content_text);

        Cursor cursor = MainActivity.databaseManager.fetch(0,0,0);
        if (cursor != null && cursor.getCount() != 0) {
            Random rand = new Random();
            int rowNum = cursor.getCount();
            int randomEntryPosition = rand.nextInt(rowNum);

            cursor.moveToPosition(randomEntryPosition);

            int year = cursor.getInt(1);
            int month = cursor.getInt(2);
            int day = cursor.getInt(3);
            String entryContent = cursor.getString(4);


            yearText.setText(String.valueOf(year));
            monthText.setText(String.format(Locale.JAPAN, "%2d", month + 1));
            dayText.setText(String.format(Locale.JAPAN, "%2d", day));
            contentText.setText(entryContent);

            view.setOnClickListener(v -> {
                view.setOnClickListener(null);

                FragmentManager mainViewFragmentManager = getParentFragment().getParentFragmentManager();
                FragmentTransaction transaction = mainViewFragmentManager.beginTransaction();

                // Pass current year and current month
                Bundle bundle = new Bundle();
                bundle.putInt("Year", year);
                bundle.putInt("Month", month);
                bundle.putInt("Day", day);

                // Create new CalendarContainer and add Bundle
                EntryScreen entryScreen = new EntryScreen();
                entryScreen.setArguments(bundle);

                // Setting transaction
                transaction.setReorderingAllowed(true);
                transaction.addToBackStack(null);
                // Set animation and show EntryScreen
                transaction.setCustomAnimations(
                        R.anim.slide_from_right, //enter
                        R.anim.slide_to_left, //exit
                        R.anim.slide_from_left, //popEnter
                        R.anim.slide_to_right //popExit
                );
                transaction.replace(
                        R.id.main_fragment_view,
                        entryScreen
                );
                transaction.commit();
            });
        } else {
            FragmentManager parentFragmentManager = getParentFragmentManager();
            FragmentTransaction transaction = parentFragmentManager.beginTransaction();

            transaction.remove(this);
            transaction.commit();
        }

        return view;
    }
}