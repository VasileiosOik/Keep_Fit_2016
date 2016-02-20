package com.example.bill.keepfit;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class TestModeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private EditText et;
    private Integer activeTestMode=0;
    private CheckBox myCheckBox;
    private TextView tv;
    private  boolean myBoolean = false;
    private  String currentDate;
    private SQLiteDatabase database;
    private static final String TABLE_NAME = "history_tbl_WG";
    private static final String DB_NAME = "MyHistorydb.db";
    private String previousDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et = (EditText) findViewById(R.id.date);
        tv = (TextView) findViewById(R.id.toolbar_title);


        myCheckBox= (CheckBox) findViewById(R.id.checkBox);
        myCheckBox.setOnCheckedChangeListener(this);



}
    // / Listen Check box status change event and perform action accordingly
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == myCheckBox) {
            if (isChecked) {
                tv.setVisibility(View.VISIBLE);
                et.setVisibility(View.VISIBLE);
                myBoolean=true;
            } else {
                tv.setVisibility(View.GONE);
                myBoolean=false;
                previousDate=et.getText().toString().trim();
                et.setText("");
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
              //  System.out.println(et.getText().toString());
                if(et.getText().toString().trim().equals("")){
                    if( myCheckBox.isChecked()==true && et.getText().toString().trim().equals("")){
                        Toast.makeText(TestModeActivity.this, "Enter a proper Date", Toast.LENGTH_LONG).show();
                    }else{
                        storeTheDate();
                        finish();
                    }
                }else{
                    storeTheDate();
                    finish();
                }

                break;
            case android.R.id.home:
                if(myBoolean==true && et.getText().toString().trim().equals(""))
                {
                    onBackPressed();
                }else if(myBoolean==true && !et.getText().toString().trim().equals("")){
                    onBackPressed();
                }else if(myBoolean==false && previousDate!=null ){
                    Toast.makeText(TestModeActivity.this, "Press save to exit", Toast.LENGTH_LONG).show();
                }else{
                    onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void storeTheDate() {

        if(et.getText().toString().trim().equals("")){
            //dont store something
            currentDate="";
            activeTestMode=0;
        }else{
            currentDate=et.getText().toString().trim();
            System.out.println(currentDate);

            String data[] =currentDate.split("-");
            String year=data[data.length-1];
            String month=data[data.length-2];
            String day=data[data.length-3];

            if(myCheckBox.isChecked()==true){
                activeTestMode=1;

                System.out.println("Einai 1 ");
                Toast.makeText(TestModeActivity.this, "Test mode is active", Toast.LENGTH_LONG).show();
            }else{
                activeTestMode=0;

                System.out.println("Einai 0 ");
                Toast.makeText(TestModeActivity.this, "Test mode is inactive", Toast.LENGTH_LONG).show();
            }




        }
        SharedPreferences.Editor editor = getSharedPreferences("textModeSetting", MODE_PRIVATE).edit();
        editor.putString("date", currentDate);
        editor.putInt("testM", activeTestMode);
        editor.putBoolean("returnDate", myBoolean);
        editor.commit();

        if(activeTestMode==0){
            System.out.println("Diegrapse ton goal apo to history");
            destroyTestModeGoals();
        }
        //yes or no???????
        previousDate=null;
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
        editor.commit();
    }

    private boolean load() {


        SharedPreferences prefs = getSharedPreferences("textModeSetting", MODE_PRIVATE);
        if((prefs.getBoolean("returnDate", false))==true){
            et.setText(prefs.getString("date", null));
        }else{
            et.setText("");
        }


        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("check", false);
    }

    public void destroyTestModeGoals(){

        //open the database
        ExternalDbOpenHelper dbOpenHelper1 = new ExternalDbOpenHelper(this, "MyHelpdb.db");
        database = dbOpenHelper1.openDataBase();

        //delete the rows from history table
        database.execSQL("delete from time_tbl_WG where date='"+previousDate+"'");
        database.close();
        System.out.println("paei to prwto");

        //make 0 all the rows from the first table
        clearDataFromFirstTable();

        //open the database
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        //delete the rows from history table
        database.execSQL("delete from "+TABLE_NAME+" where date='"+previousDate+"'");
        database.close();
        System.out.println("paei to trito");

    }

    private void clearDataFromFirstTable() {
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, "Mydb.db");
        database = dbOpenHelper.openDataBase();

        SharedPreferences sharedPreferences = getSharedPreferences("status", MODE_PRIVATE);
        String goal_to_remember=sharedPreferences.getString("MyGoal1", "0");
       // System.out.println(goal_to_remember);
        if(goal_to_remember!=null && goal_to_remember!="0"){
            String data[] = goal_to_remember.split(" ");
            String nameOfTestGoal = data[data.length - 4];

            database.execSQL("UPDATE tbl_WG SET percentage='"+0+"' WHERE name='"+nameOfTestGoal+"'");
            database.close();

        }

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("MyData", String.valueOf(0));//percentage
        prefsEditor.putFloat("MyData2", 0f);//current steps
        prefsEditor.putString("MyData3", String.valueOf(0));//name of the current goal
        prefsEditor.commit();

    }

}
