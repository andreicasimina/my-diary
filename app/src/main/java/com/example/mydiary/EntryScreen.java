package com.example.mydiary;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.Calendar;

public class EntryScreen extends Fragment {

    int shownYear, shownMonth, shownDay;
    Spinner yearSelector, monthSelector, daySelector;
    EditText entryContentInput;
    String entryContent;
    boolean isNew = true;
    View view;

    public EntryScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            shownYear = bundle.getInt("Year");
            shownMonth = bundle.getInt("Month");
            shownDay = bundle.getInt("Day");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_entry_screen, container, false);

        // returnButton pressed
        ImageButton returnButton = view.findViewById(R.id.return_button);
        returnButton.setOnClickListener( v -> {
            // Hide keyboard
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // Get parentFragmentManager
            FragmentManager parentFragmentManager = getParentFragmentManager();

            // popBackStack
            parentFragmentManager.popBackStack();
        });

        // Get selectors
        yearSelector = view.findViewById(R.id.year_selector);
        monthSelector = view.findViewById(R.id.month_selector);
        daySelector = view.findViewById(R.id.day_selector);

        // Set ArrayAdapters to selectors
        ArrayAdapter<Integer> yearsAdapter = new ArrayAdapter<>(view.getContext(), R.layout.selector_child, MainActivity.yearsArray);
        ArrayAdapter<Integer> monthsAdapter = new ArrayAdapter<>(view.getContext(), R.layout.selector_child, MainActivity.monthsArray);
        ArrayAdapter<Integer> daysAdapter = new ArrayAdapter<>(view.getContext(), R.layout.selector_child, MainActivity.daysArray);

        yearSelector.setAdapter(yearsAdapter);
        monthSelector.setAdapter(monthsAdapter);
        daySelector.setAdapter(daysAdapter);

        // Set first selection
        yearSelector.setSelection(yearsAdapter.getPosition(shownYear));
        monthSelector.setSelection(monthsAdapter.getPosition(shownMonth + 1));
        daySelector.setSelection(daysAdapter.getPosition(shownDay));

        // Get entryContentInput
        entryContentInput = view.findViewById(R.id.entry_content_input);

        // Set text of entryContentInput
        entryContentInput.setText(entryContent);

        // When yearSelector item selected
        yearSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                shownYear = Integer.parseInt(parent.getItemAtPosition(position).toString());
                showEntryContent();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Required onNothingSelected
            }
        });

        // When monthSelector item selected
        monthSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                shownMonth = position;
                showEntryContent();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Required onNothingSelected
            }
        });

        // When daySelector item selected
        daySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                shownDay = Integer.parseInt(parent.getItemAtPosition(position).toString());
                showEntryContent();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Required onNothingSelected
            }
        });

        showEntryContent();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        entryContent = entryContentInput.getText().toString();

        if (entryContent.trim().isEmpty() && isNew) { return; }

        if (entryContent.trim().isEmpty() && !isNew) {
            MainActivity.databaseManager.delete(shownYear, shownMonth, shownDay);
            return;
        }

        if (isNew) {
            MainActivity.databaseManager.insert(shownYear, shownMonth, shownDay, entryContent);
        } else {
            MainActivity.databaseManager.update(shownYear, shownMonth, shownDay, entryContent);
        }
    }

    public void showEntryContent() {
        isNew = true;
        entryContentInput.setText("");

        Cursor cursor = MainActivity.databaseManager.fetch(shownYear, shownMonth, shownDay);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                entryContent = cursor.getString(4);
                isNew = false;
                entryContentInput.setText(entryContent);
            }
        }
    }
}