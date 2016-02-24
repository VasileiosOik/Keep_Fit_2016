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
    private String date="0";
    private String regex = "[0-9]+";
    private static final String DB_NAME = "Mydb.db";

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


        /*
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

*/
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
        //insert data into able
        db.execSQL("insert into tbl_WG values('"+name+"','"+steps+"','"+percentageSteps+"')");
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
                }else if(et2.getText().toString().trim().contains(".") || et2.getText().toString().trim().contains("-") || et2.getText().toString().trim().contains(",")){
                    Toast.makeText(GoalActivity.this, "Steps must contain only numbers",
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
                }else if(et1.getText().toString().trim().matches(regex)){//(et1.getText().toString().trim().matches(".*\\d.*")){
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


}
