package com.example.mydiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static Calendar currentDate;
    static Integer[] yearsArray = new Integer[1000];
    static Integer[] monthsArray = new Integer[12];
    static Integer[] daysArray = new Integer[31];
    static DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("DiaryAppPreferences", MODE_PRIVATE);
        int themeId = sharedPreferences.getInt("ThemeId", R.style.AppThemeSmall);
        setTheme(themeId);
        setContentView(R.layout.activity_main);

        // Get currentDate
        currentDate = Calendar.getInstance();

        // Generate years from 2000 to 2999
        for (int i=0; i<1000; i++) {
            yearsArray[i] = i + 2000;
        }

        // Generate months from 0 to 11
        for (int i=0; i<12; i++) {
            monthsArray[i] = i + 1;
        }

        // Generate days from 1 to 31
        for (int i=0; i<31; i++) {
            daysArray[i] = i + 1;
        }

        // Get databaseManager
        databaseManager = new DatabaseManager(this);
        try {
            databaseManager.open();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Get fragmentManager and transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Create new calendar container and show it
        transaction.add(R.id.main_fragment_view, HomeScreen.class, null);
        transaction.setReorderingAllowed(true);
        transaction.commit();
    }
}