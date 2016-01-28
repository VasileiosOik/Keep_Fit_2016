package com.example.bill.keepfit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

/*
        //create new table if not already exist 1st way
        final String CREATE_TABLE_WalkingGoals =
                "CREATE TABLE IF NOT EXISTS tbl_WG ("
                        + "name VARCHAR PRIMARY KEY ,"
                        + "steps INTEGER);";
        db.execSQL(CREATE_TABLE_WalkingGoals);
*/
        //delete the table
       // db.execSQL("DROP TABLE IF EXISTS tbl_WG");

        //create the table 2nd way
        db.execSQL("create table if not exists tbl_WG(name varchar primary key, steps int)");
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
        db.execSQL("insert into tbl_WG values('"+name+"','"+steps+"')");
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
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }
    */
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
                insert();
                //Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                //.show();
                break;
            default:
                break;
        }

        return true;
    }
}
