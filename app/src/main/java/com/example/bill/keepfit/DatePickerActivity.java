package com.example.bill.keepfit;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class DatePickerActivity extends AppCompatActivity implements DateRangePickerFragment.OnDateRangeSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        //make visible the back button in the action bar <-
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        prefs = getSharedPreferences("nbRepet", Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putInt("nbRepet", 1);
        editor.apply();

        DateRangePickerFragment dateRangePickerFragment;
        dateRangePickerFragment = DateRangePickerFragment.newInstance(DatePickerActivity.this, false);
        dateRangePickerFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateRangeSelected(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear) {
        Log.d("range : ", "from: " + startDay + "-" + startMonth + "-" + startYear + " to : " + endDay + "-" + endMonth + "-" + endYear);
        String st1 = startDay + "/" + startMonth + "/" + startYear;
        String st2 = endDay + "/" + endMonth + "/" + endYear;

        SharedPreferences datePickerPref = this.getSharedPreferences("myDatePicker", MODE_PRIVATE);
        SharedPreferences.Editor prefsDateEditor = datePickerPref.edit();
        prefsDateEditor.putString("date1", st1);
        prefsDateEditor.putString("date2", st2);
        prefsDateEditor.apply();
        this.finish();
    }

    @Override
    public void onBackPressed() {
        DatePickerActivity.this.finish();
    }
}
