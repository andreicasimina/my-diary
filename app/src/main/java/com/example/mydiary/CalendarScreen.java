package com.example.mydiary;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.Calendar;

public class CalendarScreen extends Fragment {

    private int shownYear, shownMonth;

    public CalendarScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            shownYear = bundle.getInt("Year");
            shownMonth = bundle.getInt("Month");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_screen, container, false);

        // Get parentFragmentManager
        FragmentManager parentFragmentManager = getParentFragmentManager();

        // returnButton pressed
        ImageButton returnButton = view.findViewById(R.id.return_button);
        returnButton.setOnClickListener( v -> parentFragmentManager.popBackStack());

        // Get childFragmentManager
        FragmentManager childFragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = childFragmentManager.beginTransaction();

        // Pass current year and current month
        Bundle bundle = new Bundle();
        bundle.putInt("Year", MainActivity.currentDate.get(Calendar.YEAR));
        bundle.putInt("Month", MainActivity.currentDate.get(Calendar.MONTH));

        // Create new CalendarContainer and add Bundle
        CalendarContainer calendarContainer = new CalendarContainer();
        calendarContainer.setArguments(bundle);

        // Create calendarContainer
        transaction.setReorderingAllowed(true);
        transaction.add(R.id.calendar_container, calendarContainer);
        transaction.commit();

        return view;
    }
}