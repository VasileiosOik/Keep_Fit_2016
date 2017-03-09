package com.example.bill.keepfit;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TabHost;

public class DateRangePickerFragment extends DialogFragment implements View.OnClickListener {

    private OnDateRangeSelectedListener onDateRangeSelectedListener;

    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    boolean is24HourMode;

    public DateRangePickerFragment() {
        // Required empty public constructor
    }

    public static DateRangePickerFragment newInstance(OnDateRangeSelectedListener callback, boolean is24HourMode) {
        DateRangePickerFragment dateRangePickerFragment = new DateRangePickerFragment();
        dateRangePickerFragment.initialize(callback, is24HourMode);
        return dateRangePickerFragment;
    }

    public void initialize(OnDateRangeSelectedListener callback,
                           boolean is24HourMode) {
        onDateRangeSelectedListener = callback;
        this.is24HourMode = is24HourMode;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.date_range_picker, container, false);
        final boolean b = getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        TabHost tabHost = (TabHost) root.findViewById(R.id.tabHost);
        Button butSetDateRange = (Button) root.findViewById(R.id.but_set_time_range);
        startDatePicker = (DatePicker) root.findViewById(R.id.start_date_picker);
        endDatePicker = (DatePicker) root.findViewById(R.id.end_date_picker);
        butSetDateRange.setOnClickListener(this);
        tabHost.findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec startDatePage = tabHost.newTabSpec("start");
        startDatePage.setContent(R.id.start_date_group);
        startDatePage.setIndicator(getString(R.string.title_tab_start_date));

        TabHost.TabSpec endDatePage = tabHost.newTabSpec("end");
        endDatePage.setContent(R.id.end_date_group);
        endDatePage.setIndicator(getString(R.string.ttile_tab_end_date));

        tabHost.addTab(startDatePage);
        tabHost.addTab(endDatePage);
        return root;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;

        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT);
    }


    @Override
    public void onClick(View v) {
        dismiss();
        onDateRangeSelectedListener.onDateRangeSelected(startDatePicker.getDayOfMonth(), startDatePicker.getMonth() + 1, startDatePicker.getYear(),
                endDatePicker.getDayOfMonth(), endDatePicker.getMonth() + 1, endDatePicker.getYear());
    }

    interface OnDateRangeSelectedListener {
         void onDateRangeSelected(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear);
    }
}