package com.example.mydiary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.util.Objects;

public class DaysContainer extends Fragment {

    private int year, month;
    private int[] daysWithContent;

    LinearLayout verticalLayoutContainer;

    public DaysContainer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if ( bundle != null ) {
            year = bundle.getInt("Year");
            month = bundle.getInt("Month");
            daysWithContent = bundle.getIntArray("daysWithContent");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_days_container, container, false);

        // Get first weekday and last day of month
        int firstWeekday = getFirstWeekdayOfMonth();
        int lastDay = getLastDayOfMonth();

        // Get vertical layout container
        verticalLayoutContainer = view.findViewById(R.id.days_container_vertical_layout_container);

        // Base layout parameters
        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        layoutParams.weight = 1;

        LinearLayout horizontalLayout = new LinearLayout(view.getContext());
        horizontalLayout.setLayoutParams(layoutParams);

        // Get fontSize of Theme
        Activity mainActivity = getActivity();
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("DiaryAppPreferences", mainActivity.MODE_PRIVATE);
        // Get themId
        int themeId = sharedPreferences.getInt("ThemeId", R.style.AppThemeMedium);
        TypedArray typedArray1 = mainActivity.getTheme().obtainStyledAttributes(
                themeId,
                new int[] { R.attr.textSizeSmall }
        );
        // Get textSizeSmallId
        int textSizeSmallId = typedArray1.getResourceId(0, 0);
        // Get color hex code (eg, #fff)
        TypedArray typedArray2 = mainActivity.obtainStyledAttributes(
                textSizeSmallId,
                new int[]{android.R.attr.textSize}
        );
        // Get smallTextSize as SP
        int smallTextSize = typedArray2.getDimensionPixelSize(0, 90);
        // Don't forget to recycle
        typedArray1.recycle();
        typedArray2.recycle();

        // daySquare layout parameters
        LinearLayout.LayoutParams daySquareLayoutParams = new LinearLayout
                .LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        daySquareLayoutParams.weight = 1;
        int daySquareMargin = (int) (getResources().getDimension(R.dimen.day_square_margin));
        daySquareLayoutParams.setMargins(daySquareMargin, daySquareMargin, daySquareMargin, daySquareMargin);

        int totalDaySquares = lastDay + firstWeekday + (7 - ((lastDay + firstWeekday) % 7));
        for(int i=1; i<=totalDaySquares; i++) {

            // Make a daySquare
            TextView daySquare = new TextView(view.getContext());
            int daySquarePadding = (int) (getResources().getDimension(R.dimen.day_square_padding));
            daySquare.setLayoutParams(daySquareLayoutParams);

            daySquare.setPadding(daySquarePadding, daySquarePadding, daySquarePadding, daySquarePadding);
            daySquare.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    smallTextSize
            );
            daySquare.setGravity(Gravity.CENTER);

            // Set text only when it is a real day
            if ((i > firstWeekday-1) && (i < lastDay + firstWeekday)) {
                int day = i - (firstWeekday - 1);
                daySquare.setText(String.valueOf(day));

                if (daysWithContent.length != 0) {
                    for (int j : daysWithContent) {
                        if (day == j) {
                            daySquare.setBackground(getResources().getDrawable(R.drawable.has_entry_background));
                            daySquare.setTextColor(getResources().getColor(R.color.white));
                            daySquare.setTypeface(null, Typeface.BOLD);
                            break;
                        }
                    }
                }

                // Go to entryScreen when daySquare pressed
                daySquare.setOnClickListener( v -> {
                    Fragment calendarScreenFragment = requireParentFragment().getParentFragment();
                    FragmentManager mainFragmentManager = Objects.requireNonNull(calendarScreenFragment).getParentFragmentManager();
                    FragmentTransaction transaction = mainFragmentManager.beginTransaction();

                    // Pass current year and current month
                    Bundle bundle = new Bundle();
                    bundle.putInt("Year", year);
                    bundle.putInt("Month", month);
                    bundle.putInt("Day", day);

                    // Create new CalendarContainer and add Bundle
                    EntryScreen entryScreen = new EntryScreen();
                    entryScreen.setArguments(bundle);

                    // Set animation and show EntryScreen
                    transaction.setReorderingAllowed(true);
                    transaction.setCustomAnimations(
                            R.anim.slide_from_right, //enter
                            R.anim.slide_to_left, //exit
                            R.anim.slide_from_left, //popEnter
                            R.anim.slide_to_right //popExit
                    );
                    transaction.hide(calendarScreenFragment);
                    transaction.add(
                            R.id.main_fragment_view,
                            entryScreen
                    );
                    transaction.addToBackStack(null);
                    transaction.commit();
                });

            } else {
                daySquare.setText(" ");
            }

            horizontalLayout.addView(daySquare);

            // Go to next line
            if(i%7==0) {
                verticalLayoutContainer.addView(horizontalLayout);
                horizontalLayout = new LinearLayout(view.getContext());
                horizontalLayout.setLayoutParams(layoutParams);
            }
        }

        // Inflate the layout for this fragment
        return view;
    }

    private int getFirstWeekdayOfMonth () {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, 1);

        return date.get(Calendar.DAY_OF_WEEK);
    }

    private int getLastDayOfMonth () {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.DAY_OF_MONTH, -1);

        return date.get(Calendar.DAY_OF_MONTH);
    }

    private int dpToPixels (int sizeInDp) {
        return (int) (sizeInDp * getResources().getDisplayMetrics().density);
    }

    private int spToPixels (int sizeInSp) {
        return (int) (sizeInSp * getResources().getDisplayMetrics().scaledDensity);
    }
}