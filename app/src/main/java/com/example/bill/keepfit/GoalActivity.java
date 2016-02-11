package com.example.bill.keepfit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class GoalActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private SQLiteDatabase db;
    private EditText et1,et2;
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;
    private String nameCh;
    private float percentageSteps=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize a tool bar
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);


        //initialize the edittext objects
        et1 = (EditText) findViewById(R.id.editname);
        et2 = (EditText) findViewById(R.id.editsteps);
        //create database if not already exist

        db = openOrCreateDatabase("Mydb.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.setVersion(1);
        db.setLocale(Locale.getDefault());


        //create new table if not already exist 1st way
        final String CREATE_TABLE_WalkingGoals =
                "CREATE TABLE IF NOT EXISTS tbl_WG ("
                        + "name VARCHAR PRIMARY KEY ,"
                        + "steps INTEGER, "
                        + "percentage FLOAT);";
        db.execSQL(CREATE_TABLE_WalkingGoals);
        System.out.println("Table has created successfully!");


        }

    //In this method you handle the button events
    @Override
    public void onClick(View v) {

    }

    //This method will be called when insert button is pressed
    public void insert()
    {
        String name=et1.getText().toString();
        Integer steps=Integer.parseInt(et2.getText().toString());
        et1.setText("");
        et2.setText("");
        //insert data into able
        db.execSQL("insert into tbl_WG values('"+name+"','"+steps+"','"+percentageSteps+"')");
        //display Toast
        Toast.makeText(this, "goal stored successfully!", Toast.LENGTH_LONG).show();
        //to return to previous screen
        finish();
    }
    /*
    //This method will be called when display button is pressed
    public void display(View v)
    {
        //use cursor to keep all data
        //cursor can keep data of any data type
        Cursor c=db.rawQuery("select * from tbl_WG", null);
        tv.setText("");
        //move cursor to first position
        c.moveToFirst();
        //fetch all data one by one
        do
        {
            //we can use c.getString(0) here
            //or we can get data using column index
            String name=c.getString(c.getColumnIndex("name"));
            Integer steps=Integer.parseInt(c.getString(1));
            //display on text view
            tv.append("Name: "+name+"\n" +"Steps: "+steps+"\n");
            //put them in a list
          //  ArrayList<String> goalList = new ArrayList<String>();
          //  goalList.add("Name: "+name+" and Steps: "+steps);
          //  System.out.println(goalList);
         //   listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, goalList);
            //move next position until end of the data
        }while(c.moveToNext());


    }
    */
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
                }

                else if (nameChecked() != null && nameChecked().equals(et1.getText().toString().trim())) {

                //    nameChecked().equals(et1.getText().toString().trim())
                    Toast.makeText(GoalActivity.this, "Name already exists",
                            Toast.LENGTH_LONG).show();
                }else if(et1.getText().toString().trim().matches(".*\\d.*")){
                    Toast.makeText(GoalActivity.this, "Name contains numbers",
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

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                else if (nameChecked().equals(et1.getText().toString().trim())) {
                    Toast.makeText(GoalActivity.this, "Name already exists",
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
                break;
        }

        return true;
    }
*/


    public String nameChecked(){
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

        return nameCh;
    }

    @Override
    public void onBackPressed() {
        GoalActivity.this.finish();
    }


}
