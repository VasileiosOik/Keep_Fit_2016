package com.example.bill.keepfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
//importnant Library
import android.view.ActionMode;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChooseActivity extends AppCompatActivity {
    private ListView mainListView;
    private ArrayList<String> goalList;
    private SQLiteDatabase database;
    private static final String DB_NAME = "Mydb.db";
    private View v1;
    public boolean[] status;
    private Boolean[] checkedStatus;


    public ChooseActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        //for the back button in the menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize the list view
        mainListView = (ListView) findViewById( R.id.goal_list1 );
        mainListView.setChoiceMode(mainListView.CHOICE_MODE_SINGLE);

        //display the options
        display(v1);

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
                int count=0;
                int onlyOne=0;
                //check how many true values the table has
                for (boolean value : checkedStatus) {
                    if (value){
                        onlyOne++;
                    }

                }
                //start the new activity only when only one switch is on
                for (int i = 0; i < checkedStatus.length; i++){
                    if(checkedStatus[i]==true && onlyOne==1) {
                        SharedPreferences sharedPreferences = getSharedPreferences("status", MODE_PRIVATE);
                        String goal_to_remember=sharedPreferences.getString("MyGoal1", "0");
                        Intent b = new Intent(ChooseActivity.this, PedometerActivity.class);
                        b.putExtra("string", goal_to_remember);
                        startActivity(b);
                        ChooseActivity.this.finish();
                    }else {
                        count++;
                    }
                }

                if(onlyOne>1) {
                    Toast.makeText(ChooseActivity.this, "Multiple goals selected", Toast.LENGTH_LONG).show();
                }

                if(count==checkedStatus.length && onlyOne==0){
                    Toast.makeText(ChooseActivity.this, "No goal selected", Toast.LENGTH_LONG).show();
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


    public void display(View v) {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        //initialize
        goalList = new ArrayList<String>();
        //put cursor
        Cursor cursor = database.rawQuery("select * from tbl_WG", null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            do {
                String name=cursor.getString(cursor.getColumnIndex("name"));
                String unit=cursor.getString(cursor.getColumnIndex("unit"));
                Integer steps=Integer.parseInt(cursor.getString(1));

                goalList.add("Name: "+name+" || " +unit+": "+steps);


            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        //size of the status list;
        status= new boolean[goalList.size()];

        //check state
        SharedPreferences sharedPreferences = getSharedPreferences("status", MODE_PRIVATE);
        checkedStatus = new Boolean[goalList.size()];
        for ( int index = 0; index < checkedStatus.length; index++)
            checkedStatus[index] = sharedPreferences.getBoolean(Integer.toString(index), false);

        //create an ArrayAdaptar from the String Array
        ListAdapter2 adapter = new ListAdapter2(this, goalList, checkedStatus);
        // Assign adapter to ListView
        mainListView.setAdapter(adapter);

    }

    // Physical back button click handler
    @Override
    public void onBackPressed() {
        ChooseActivity.this.finish();

    }

}
