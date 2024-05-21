package com.example.mydiary;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

public class CalendarContainer extends Fragment {


    private int shownYear, shownMonth;

    private ArrayAdapter<Integer> yearsAdapter, monthsAdapter;
    private Spinner yearSelector, monthSelector;

    public CalendarContainer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if ( bundle != null ) {
            shownYear = bundle.getInt("Year");
            shownMonth = bundle.getInt("Month");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar_container, container, false);

        yearSelector = view.findViewById(R.id.calendar_year_selector);
        monthSelector = view.findViewById(R.id.calendar_month_selector);

        yearsAdapter = new ArrayAdapter<>(view.getContext(), R.layout.selector_child, MainActivity.yearsArray);
        monthsAdapter = new ArrayAdapter<>(view.getContext(), R.layout.selector_child, MainActivity.monthsArray);

        ImageButton leftButton, rightButton;

        // Set button listeners
        leftButton = view.findViewById(R.id.calendar_left_button);
        leftButton.setOnClickListener( v -> {
            shownMonth--;
            if (shownMonth < 0) {
                shownMonth = 11;
                shownYear--;
            }
            updateDisplay("Left");
        });

        rightButton = view.findViewById(R.id.calendar_right_button);
        rightButton.setOnClickListener( v -> {
            shownMonth++;
            if (shownMonth > 11) {
                shownMonth = 0;
                shownYear++;
            }
            updateDisplay("Right");
        });

        // When yearSelector item selected
        yearSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                int newYear = Integer.parseInt(parent.getItemAtPosition(position).toString());
                if(shownYear < newYear) {
                    shownYear = newYear;
                    updateDisplay("Right");
                } else if (shownYear > newYear) {
                    shownYear = newYear;
                    updateDisplay("Left");
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Required onNothingSelected
            }
        });

        // When monthSelector item selected
        monthSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                int newMonth = position;
                if(shownMonth < newMonth) {
                    shownMonth = newMonth;
                    updateDisplay("Right");
                } else if (shownMonth > newMonth) {
                    shownMonth = newMonth;
                    updateDisplay("Left");
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Required onNothingSelected
            }
        });

        // Load initial days container
        updateDisplay("None");

        // Inflate the layout for this fragment
        return view;
    }

    public void updateDisplay (String toWhere) {
        // Updating Title
        yearSelector.setAdapter(yearsAdapter);
        monthSelector.setAdapter(monthsAdapter);

        yearSelector.setSelection(yearsAdapter.getPosition(shownYear));
        monthSelector.setSelection(monthsAdapter.getPosition(shownMonth + 1));

        // Updating Days Container

        // Get fragment manager
        FragmentManager childFragmentManager = getChildFragmentManager();
        FragmentTransaction childTransaction = childFragmentManager.beginTransaction();

        // Get days with content
        Cursor cursor = MainActivity.databaseManager.fetch(shownYear, shownMonth, 0);
        int[] daysWithContent = new int[31];
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int i = 0;
                do {
                    int day = cursor.getInt(3);
                    daysWithContent[i] = day;
                    i++;
                } while (cursor.moveToNext());
            }
        }

        Bundle bundle = new Bundle();
        bundle.putInt("Year", shownYear);
        bundle.putInt("Month", shownMonth);
        bundle.putIntArray("daysWithContent", daysWithContent);

        DaysContainer daysContainer = new DaysContainer();
        daysContainer.setArguments(bundle);

        //Animation
        childTransaction.setReorderingAllowed(true);
        if(Objects.equals(toWhere, "Right")) {
            childTransaction.setCustomAnimations(
                    R.anim.slide_from_right, //enter
                    R.anim.slide_to_left //exit
            );
        } else if (Objects.equals(toWhere, "Left")) {
            childTransaction.setCustomAnimations(
                    R.anim.slide_from_left, //enter
                    R.anim.slide_to_right //exit
            );
        }

        childTransaction.replace(R.id.days_container_view, daysContainer);
        childTransaction.addToBackStack(null);
        childTransaction.commit();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i("DATA_TAG", "onHiddenChanged");
        updateDisplay("None");
    }
}