package com.example.mydiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;

public class HomeScreen extends Fragment {

    public HomeScreen() {
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
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        // Get buttons
        ImageButton settingsButton = view.findViewById(R.id.settings_button);
        ImageButton calendarButton = view.findViewById(R.id.calendar_button);
        ImageButton newButton = view.findViewById(R.id.new_button);

        // Get parentFragmentManager and transaction
        FragmentManager parentFragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = parentFragmentManager.beginTransaction();

        // Setting transaction
        transaction.setReorderingAllowed(true);
        transaction.addToBackStack(null);

        // settingsButton clicked
        settingsButton.setOnClickListener( v -> {
            settingsButton.setOnClickListener(null);

            transaction.setCustomAnimations(
                    R.anim.slide_from_left, //enter
                    R.anim.slide_to_right, //exit
                    R.anim.slide_from_right, //popEnter
                    R.anim.slide_to_left //popExit
            );
            transaction.replace(
                    R.id.main_fragment_view,
                    new SettingsScreen(),
                    null
            );
            transaction.commit();
        });

        // calendarButton clicked
        calendarButton.setOnClickListener( v -> {
            calendarButton.setOnClickListener(null);

            // Pass current year and current month
            Bundle bundle = new Bundle();
            bundle.putInt("Year", MainActivity.currentDate.get(Calendar.YEAR));
            bundle.putInt("Month", MainActivity.currentDate.get(Calendar.MONTH));

            // Create new CalendarContainer and add Bundle
            CalendarScreen calendarScreen = new CalendarScreen();
            calendarScreen.setArguments(bundle);

            // Set animation and show CalendarScreen
            transaction.setCustomAnimations(
                    R.anim.slide_from_down, //enter
                    R.anim.slide_to_up, //exit
                    R.anim.slide_from_up, //popEnter
                    R.anim.slide_to_down //popExit
            );
            transaction.replace(
                    R.id.main_fragment_view,
                    calendarScreen
            );
            transaction.commit();
        });

        // newButton clicked
        newButton.setOnClickListener( v -> {
            newButton.setOnClickListener(null);

            // Pass current year and current month
            Bundle bundle = new Bundle();
            bundle.putInt("Year", MainActivity.currentDate.get(Calendar.YEAR));
            bundle.putInt("Month", MainActivity.currentDate.get(Calendar.MONTH));
            bundle.putInt("Day", MainActivity.currentDate.get(Calendar.DAY_OF_MONTH));

            // Create new CalendarContainer and add Bundle
            EntryScreen entryScreen = new EntryScreen();
            entryScreen.setArguments(bundle);

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

        // See Entry View
        loadHighlightEntry();

        return view;
    }

    private void loadHighlightEntry () {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.see_entry_container, EntryHighlight.class, null);
        transaction.setReorderingAllowed(true);
        transaction.commit();
    }
}