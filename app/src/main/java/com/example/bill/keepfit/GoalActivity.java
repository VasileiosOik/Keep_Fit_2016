package com.example.bill.keepfit;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class GoalActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private SQLiteDatabase db;
    private EditText et1,et2;
    private String nameCh;
    private float percentageSteps=0;
    private String date="0";
    private String regex = "[0-9]+";
    private static final String DB_NAME = "Mydb.db";
    private Spinner spinner;
    private static final String[]paths = {"Meters", "Yards", "Kilometres", "Miles", "Steps"};
    private int positionSpinner;
    private String unit;
    private TextView tv;
    private ListAdapter listHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv= (TextView) findViewById(R.id.Number_of_steprs);

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(GoalActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(GoalActivity.this);

        //initialize the edittext objects
        et1 = (EditText) findViewById(R.id.editname);
        et2 = (EditText) findViewById(R.id.editsteps);

        }

    //In this method you handle the button events
    @Override
    public void onClick(View v) {

    }

    //This method will be called when insert button is pressed
    public void insert()
    {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        db = dbOpenHelper.openDataBase();

        String name=et1.getText().toString();
        Integer steps=Integer.parseInt(et2.getText().toString());
        et1.setText("");
        et2.setText("");
        System.out.println("H monada einai: " +unit);
        //insert data into able
        db.execSQL("insert into tbl_WG values('"+name+"','"+steps+"','"+unit+"','"+percentageSteps+"')");
        //display Toast
        Toast.makeText(this, "goal stored successfully!", Toast.LENGTH_LONG).show();
        db.close();

        //to return to previous screen
        finish();
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
            // action with ID save_button was selected
            case R.id.save:
                if(et1.getText().toString().trim().equals("") || et2.getText().toString().trim().equals("")){
                    Toast.makeText(GoalActivity.this, "You haven't specified a goal",
                            Toast.LENGTH_LONG).show();
                }else if(et1.getText().toString().trim().equals("0")){
                    Toast.makeText(GoalActivity.this, "Wrong name",
                            Toast.LENGTH_LONG).show();
                }
                else if((Integer.parseInt(et2.getText().toString().trim()))<=0){
                    Toast.makeText(GoalActivity.this, "Wrong number of steps",
                            Toast.LENGTH_LONG).show();
                }else if ( et1.getText().toString().trim().contains("~") || et1.getText().toString().trim().contains("@")
                        || et1.getText().toString().trim().contains("#") || et1.getText().toString().trim().contains("$")
                        || et1.getText().toString().trim().contains("%") ||  et1.getText().toString().trim().contains("^")
                        || et1.getText().toString().trim().contains("&") ||  et1.getText().toString().trim().contains("*")
                        || et1.getText().toString().trim().contains("(") ||  et1.getText().toString().trim().contains(")")
                        || et1.getText().toString().trim().contains("[") ||  et1.getText().toString().trim().contains("]")
                        || et1.getText().toString().trim().contains("{") ||  et1.getText().toString().trim().contains("}")
                        || et1.getText().toString().trim().contains(";") ||  et1.getText().toString().trim().contains(":")
                        || et1.getText().toString().trim().contains("'") ||  et1.getText().toString().trim().contains("|")
                        || et1.getText().toString().trim().contains(",") ||  et1.getText().toString().trim().contains("<")
                        || et1.getText().toString().trim().contains(">") ||  et1.getText().toString().trim().contains(".")
                        || et1.getText().toString().trim().contains("?") ||  et1.getText().toString().trim().contains("/")
                        || et1.getText().toString().trim().contains("-") ||  et1.getText().toString().trim().contains("_")
                        || et1.getText().toString().trim().contains("+") ||  et1.getText().toString().trim().contains("=")){
                    Toast.makeText(GoalActivity.this, "Name cannot contain special characters",
                            Toast.LENGTH_LONG).show();
                }
                else if (nameChecked() != null && nameChecked().equals(et1.getText().toString().trim())) {
                    Toast.makeText(GoalActivity.this, "Name already exists",
                            Toast.LENGTH_LONG).show();
                }else if(et1.getText().toString().trim().matches(regex)){
                    Toast.makeText(GoalActivity.this, "Name cannot contain only numbers",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    insert();
                }
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }



    public String nameChecked(){
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        db = dbOpenHelper.openDataBase();
        Cursor cursor = db.rawQuery("select name from tbl_WG where name='"+et1.getText().toString().trim()+"'", null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            do {
                nameCh=cursor.getString(cursor.getColumnIndex("name"));
                System.out.println("Name is: " +nameCh);
                if(nameCh.equals(et1.getText().toString())){
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return nameCh;
    }

    @Override
    public void onBackPressed() {
        GoalActivity.this.finish();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                positionSpinner=0;
                unit="Meters";
                tv.setText("Number of Meters");
                tv.invalidate();
                break;
            case 1:
                positionSpinner=1;
                unit="Yards";
                tv.setText("Number of Yards");
                tv.invalidate();
                break;
            case 2:
                positionSpinner=2;
                unit="Kilometres";
                tv.setText("Number of Kilometres");
                tv.invalidate();
                break;
            case 3:
                positionSpinner=3;
                unit="Miles";
                tv.setText("Number of Miles");
                tv.invalidate();
                break;
            case 4:
                positionSpinner=4;
                unit="Steps";
                tv.setText("Number of Steps");
                tv.invalidate();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPause() {
        super.onPause();
        save(positionSpinner);
    }

    @Override
    public void onResume() {
        super.onResume();
        spinner.setSelection(load());
    }

    private void save(final int isSelected) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("spinnerState",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int selectedPosition = isSelected;
        editor.putInt("spinnerSelection", selectedPosition);
        editor.putString("spinnerName", unit);
        editor.commit();
    }

    private int load() {


        SharedPreferences prefs = getSharedPreferences("spinnerState", MODE_PRIVATE);
        int iPos=prefs.getInt("spinnerSelection",0);
        return iPos;
    }

}
