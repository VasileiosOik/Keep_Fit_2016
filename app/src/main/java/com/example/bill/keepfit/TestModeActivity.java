package com.example.bill.keepfit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class TestModeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private EditText et;
    private CheckBox myCheckBox;
    private TextView tv;
    private boolean myBoolean = false;
    private SQLiteDatabase database;
    private static final String TABLE_NAME = "history_tbl_WG";
    private static final String DB_NAME = "MyHistorydb.db";
    private String previousDate;
    int year_x, month_x, day_x;
    final static int Dialog_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mode);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et = (EditText) findViewById(R.id.date);
        //no keyboard pop up
        et.setInputType(InputType.TYPE_NULL);
        tv = (TextView) findViewById(R.id.toolbar_title);


        myCheckBox = (CheckBox) findViewById(R.id.checkBox);
        myCheckBox.setOnCheckedChangeListener(this);

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);


    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == Dialog_ID)
            return new DatePickerDialog(TestModeActivity.this, dpicker, year_x, month_x, day_x);
        return null;

    }

    private DatePickerDialog.OnDateSetListener dpicker
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            updateDate();
        }

    };

    private void updateDate() {

        et.setText(day_x + "/" + month_x + "/" + year_x);

    }

    // / Listen Check box status change event and perform action accordingly
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == myCheckBox) {
            if (isChecked) {
                tv.setVisibility(View.VISIBLE);
                et.setVisibility(View.VISIBLE);
                et.setFocusable(true);
                et.setFocusableInTouchMode(true);
                et.requestFocus();
                myBoolean = true;
                //here i press the edittext and the date picker option pops up
                et.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showDialog(Dialog_ID);
                    }

                });

            } else {
                previousDate = et.getText().toString().trim();
                myBoolean = false;
                tv.setVisibility(View.GONE);
                et.setVisibility(View.GONE);

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.save:
                System.out.println(previousDate);
                System.out.println(myBoolean);
                System.out.println(et.getText().toString().trim());
                if (et.getText().toString().trim().equals("")) {
                    if (myCheckBox.isChecked() && et.getText().toString().trim().equals("")) {
                        Toast.makeText(TestModeActivity.this, "Enter a Date to continue", Toast.LENGTH_LONG).show();
                    } else {
                        storeTheDate();
                        finish();
                    }
                } else if (!myBoolean && !previousDate.equals("") && checkIfTableIsEmpty()) {
                    System.out.println("bika sto alert");
                    openAlert();
                } else if (!myBoolean && !previousDate.equals("") && checkIfTableIsEmpty()) {
                    System.out.println("DEN UPARXEI TPT");
                    et.setText("");
                    storeTheDate();
                    finish();
                } else {
                    System.out.println("Apothikeuw");
                    storeTheDate();
                    finish();
                }
                break;
            case android.R.id.home:
                if (myBoolean && et.getText().toString().trim().equals("")) {
                    myCheckBox.setChecked(false);
                    onBackPressed();
                } else if (myBoolean && !et.getText().toString().trim().equals("") && checkIfTableIsEmpty()) {
                    myCheckBox.setChecked(true);
                    onBackPressed();
                } else if (myBoolean && !et.getText().toString().trim().equals("") && !checkIfTableIsEmpty()) {
                    myCheckBox.setChecked(false);
                    onBackPressed();
                } else if (!myBoolean && previousDate != null && !previousDate.equals("")) {
                    Toast.makeText(TestModeActivity.this, "Press save to exit", Toast.LENGTH_LONG).show();
                } else {
                    onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void storeTheDate() {

        String currentDate;
        Integer activeTestMode;
        if (et.getText().toString().trim().equals("")) {
            //dont store something
            currentDate = "";
            activeTestMode = 0;
            if (!myCheckBox.isChecked()) {
                System.out.println("Einai 0 ");
                Toast.makeText(TestModeActivity.this, "Test mode is inactive", Toast.LENGTH_LONG).show();
                activeTestMode = 0;
            }

        } else {
            currentDate = et.getText().toString().trim();
            System.out.println(currentDate);


            if (myCheckBox.isChecked()) {
                activeTestMode = 1;
                System.out.println("Einai 1 ");
                Toast.makeText(TestModeActivity.this, "Test mode is active", Toast.LENGTH_LONG).show();
            } else {
                activeTestMode = 0;
                System.out.println("Einai 0 ");
            }

        }

        System.out.println("Einai to test mode: " + activeTestMode);
        SharedPreferences.Editor editor = getSharedPreferences("textModeSetting", MODE_PRIVATE).edit();
        editor.putString("date", currentDate);
        editor.putInt("testM", activeTestMode);
        editor.putBoolean("returnDate", myBoolean);
        editor.apply();

        if (activeTestMode == 0) {
            System.out.println("Diegrapse ton goal apo to history");
            destroyTestModeGoals();
            et.setText("");
        } else {
            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            prefsEditor.putString("MyData", String.valueOf(0));//percentage
            prefsEditor.putString("MyData2", String.valueOf(0));
            // prefsEditor.putFloat("MyData2", 0f);//current steps
            prefsEditor.putString("MyData3", String.valueOf(0));//name of the current goal
            prefsEditor.putInt("MyData4", 0);
            prefsEditor.apply();
        }
        //yes or no???????
        previousDate = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        save(myCheckBox.isChecked());
    }

    @Override
    public void onResume() {
        super.onResume();
        myCheckBox.setChecked(load());
    }

    private void save(final boolean isChecked) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("check", isChecked);
        editor.apply();
    }

    private boolean load() {


        SharedPreferences prefs = getSharedPreferences("textModeSetting", MODE_PRIVATE);
        if ((prefs.getBoolean("returnDate", false))) {
            et.setText(prefs.getString("date", null));
        } else {
            et.setText("");
        }


        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("check", false);
    }

    public void destroyTestModeGoals() {

        //open the database
        ExternalDbOpenHelper dbOpenHelper1 = new ExternalDbOpenHelper(this, "MyHelpdb.db");
        database = dbOpenHelper1.openDataBase();

        //delete the rows from history table
        database.execSQL("delete from time_tbl_WG where date='" + previousDate + "'");
        database.close();
        System.out.println("paei to prwto");

        //make 0 all the rows from the first table
        clearDataFromFirstTable();

        //open the database
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        //delete the rows from history table
        database.execSQL("delete from " + TABLE_NAME + " where testMode='" + 2 + "'");
        database.close();
        System.out.println("paei to trito");

    }

    private void clearDataFromFirstTable() {
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, "Mydb.db");
        database = dbOpenHelper.openDataBase();

        SharedPreferences sharedPreferences = getSharedPreferences("status", MODE_PRIVATE);
        String goal_to_remember = sharedPreferences.getString("MyGoal1", "0");
        System.out.println("Paei to deutero");
        if (!goal_to_remember.equals("0")) {
            String data[] = goal_to_remember.split(" ");
            String nameOfTestGoal = data[data.length - 4];

            database.execSQL("UPDATE tbl_WG SET percentage='" + 0 + "' WHERE name='" + nameOfTestGoal + "'");
            database.close();

        }

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("MyData", String.valueOf(0));//percentage
        prefsEditor.putString("MyData2", String.valueOf(0));
        //  prefsEditor.putFloat("MyData2", 0f);//current steps
        prefsEditor.putString("MyData3", String.valueOf(0));//name of the current goal
        prefsEditor.putInt("MyData4", 0);// 0
        prefsEditor.apply();

    }

    private void openAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TestModeActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm of Erasing Test Mode data");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to exit?");


        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Write your code here to invoke YES event
                storeTheDate();
                finish();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    public boolean checkIfTableIsEmpty() {
        boolean flag;
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, "MyHistorydb.db");
        database = dbOpenHelper.openDataBase();

        String count = "SELECT COUNT(*) FROM history_tbl_WG WHERE testMode='" + 2 + "'";

        Cursor mcursor = database.rawQuery(count, null);


        if (mcursor != null) {
            mcursor.moveToFirst();

            String countName = mcursor.getString(0);
            flag = !countName.equals("0");

        } else {
            flag = false;
        }

        System.out.println(flag);
        if (mcursor != null) {
            mcursor.close();
        }
        database.close();
        return flag;
    }


}
