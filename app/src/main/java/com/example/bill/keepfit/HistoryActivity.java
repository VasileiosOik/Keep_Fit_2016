package com.example.bill.keepfit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    private static final String TABLE_NAME = "history_tbl_WG";
    private static final String DB_NAME = "MyHistorydb.db";
    private ArrayAdapter<String> listAdapter ;
    private ListView mainListView;
    private ArrayList<String> goalList;
    private View v1;
    private String prefName;
    private Double prefNameSteps;
    private String goalName;
    private String curDateHistory;
    private String dateTime;
    private String dateTM;
    private Integer numberTM;
    private int exists=0;
    private static DecimalFormat df2 = new DecimalFormat(".##");
    private MenuItem mSpinnerItem1 = null;
    private ArrayAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the content of the history screen
        setContentView(R.layout.activity_history);

        //make visible the back button in the action bar <-
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        //return the data from the pedometer activity (last goal is active there)
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        prefName = myPrefs.getString("MyData", "0");//percentage
        prefNameSteps = Double.valueOf(myPrefs.getString("MyData2", String.valueOf(0.0)));//current steps
        goalName=myPrefs.getString("MyData3", "0");//name of the current goal

        //hereeeeeeeeeeeeeeeeeeeeeeeeee i open the shared preferences of the test mode
        SharedPreferences testModePreferences = this.getSharedPreferences("textModeSetting", MODE_PRIVATE);
        dateTM=testModePreferences.getString("date", null);
        numberTM=testModePreferences.getInt("testM",0);

        //here we initialize the listview to the list in the xml file
        mainListView = (ListView) findViewById( R.id.goal_list );


        //get the present date
        if(numberTM==0){
            curDateHistory=getCurrentDate();
        }else{
            curDateHistory=dateTM;
        }

        //see what history table has inside
        print();


        //here the list of goals is appeared in the main screen
        display1(v1);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clear, menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history_menu, menu);
        mSpinnerItem1 = menu.findItem( R.id.spinner1);
        //spinner with choices
        View view1 = mSpinnerItem1.getActionView();
        if (view1 instanceof Spinner)
        {
            Spinner spinner = (Spinner) view1;
            spinnerAdapter=ArrayAdapter.createFromResource(this, R.array.spinner_data, android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });

        }

        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_clear) {

            if(checkIfTableIsEmpty()==true) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }else{
                Toast.makeText(HistoryActivity.this, "There is nothing to clear", Toast.LENGTH_LONG).show();
            }
            return true;
        }else if(id==android.R.id.home){
            onBackPressed();
        }





        return super.onOptionsItemSelected(item);
    }


    // Physical back button click handler
    @Override
    public void onBackPressed() {
        super.finish();

    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());

    }

    public void display1(View v){
        SharedPreferences sharedPreferences = getSharedPreferences("status", MODE_PRIVATE);
        String goal_to_remember=sharedPreferences.getString("MyGoal1", "0");
        if(goal_to_remember.equals("0")){
            //dont show anything
        }else {
            String data[] = goal_to_remember.split(" ");
            //store the name of the goal that was chosen
            String nameOfCurrentGoal = data[data.length - 4];
            System.out.println(nameOfCurrentGoal);

            //The database is open!
            ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
            database = dbOpenHelper.openDataBase();
            //here i retrieve the data i want
            goalList = new ArrayList<String>();

            Cursor cursor = database.rawQuery("select * from history_tbl_WG where active='" + 1 + "'", null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    System.out.println("Retrieve data now and checking the date...");
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    Integer steps = cursor.getInt(cursor.getColumnIndex("allsteps"));
                    Double stepsDid = cursor.getDouble(cursor.getColumnIndex("didsteps"));
                    String unit=cursor.getString(cursor.getColumnIndex("unit"));
                    Float percentage = cursor.getFloat(cursor.getColumnIndex("percentage"));
                    Integer modeTest=cursor.getInt(cursor.getColumnIndex("testMode"));
                    dateTime = cursor.getString(cursor.getColumnIndex("date"));
                    //hereeeeeeee test mode again
                    if(numberTM==1  && modeTest==2){
                        System.out.println("here in test mode");
                        if(unitReturn().equals("Steps")) {
                            goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + new Double(stepsDid).longValue());
                        }else{
                            goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                        }
                    }

                        if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory)<0) && numberTM==0) {
                            System.out.println("difference in days in if: " + differenceInDays());
                            if(unitReturn().equals("Steps")){
                                goalList.add(dateTime+ "\n" +"Name: " + name + " || "+unitReturn()+": " + steps + " || Percentage: " + (int) ((percentage*100)+0.5) + "%"  +" ||" +"\n" +unitReturn()+" Walked: " + new Double(stepsDid).longValue());

                            }else{
                                goalList.add(dateTime+ "\n" +"Name: " + name + " || "+unitReturn()+": " + steps + " || Percentage: " + (int) ((percentage*100)+0.5) + "%"  +" ||" +"\n" +unitReturn()+" Walked: " + df2.format(stepsDid));
                            }

                        } else {
                            //nothing
                            System.out.println("difference in days on else: " + differenceInDays());
                        }




                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();


        ListAdapter adapter = new ListAdapter(this, goalList);
        mainListView.setAdapter(adapter);
        }


    }

    public String getCurrentDate(){
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String DateToStr = format.format(curDate);
        return DateToStr;
    }

    public long differenceInDays(){

        long days=0;
        //the format of the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {

            Date oldDate = dateFormat.parse(dateTime);


            Date currentDate =  dateFormat.parse(curDateHistory);


            if(oldDate.compareTo(currentDate)==0){
                days=0;

            }else{
                days=1;
            }

        } catch (ParseException e) {

            e.printStackTrace();
        }

        return days;
    }


    public boolean checkIfTableIsEmpty() {
        boolean flag=false;
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, "MyHistorydb.db");
        database = dbOpenHelper.openDataBase();

        String count = "SELECT COUNT(*) FROM history_tbl_WG";

        Cursor mcursor = database.rawQuery(count, null);
        mcursor.getCount();


        if(mcursor != null){
            mcursor.moveToFirst();

            String countName=mcursor.getString(0);
            if(!countName.equals("0")){
                flag=true;
            } else {
                flag=false;
            }

        } else {
            flag=false;
        }

        System.out.println(flag);
        mcursor.close();
        database.close();
        return flag;
    }

    public void print() {
        String name = "";
        Integer steps = 0;
        Double stepsDid=0.0;
        String unit="";
        Float percentage = 0f;
        String dateSearch = "";
        Integer activeNumber = 0;
        SQLiteDatabase databasehelp;
        //here i store the active goal that i want to transfer
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, "MyHistorydb.db");
        database = dbOpenHelper.openDataBase();

        Cursor cursor = database.rawQuery("select * from history_tbl_WG where active='" + 1 + "'", null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            System.out.println("edw1111111");
            do {
                System.out.println("Collect the data from the active goal");
                name = cursor.getString(cursor.getColumnIndex("name"));
                steps = cursor.getInt(cursor.getColumnIndex("allsteps"));
                stepsDid = cursor.getDouble(cursor.getColumnIndex("didsteps"));
                unit=cursor.getString(cursor.getColumnIndex("unit"));
                percentage = cursor.getFloat(cursor.getColumnIndex("percentage"));
                dateSearch = cursor.getString(cursor.getColumnIndex("date"));
                activeNumber = cursor.getInt(cursor.getColumnIndex("active"));
                System.out.println(name +" " +steps +" " +stepsDid +" "+unit+" " +percentage + " " +dateSearch +" " +activeNumber);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
    }



    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete Action")
                .setMessage("Do you want to delete History?")


                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //my deleting code
                        deleteHistory();
                        dialog.dismiss();
                    }

                })



                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    private void deleteHistory() {
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        //clear the history
        if(numberTM==0) {
            database.delete(TABLE_NAME, null, null);
            database.close();
        }else{
            deleteTestModeGoals();
        }

        Intent intent = getIntent();

        //no animation
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    public void deleteTestModeGoals(){
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        database.delete(TABLE_NAME, "testMode" + "='" + 2 + "'", null);

        database.close();

    }

    public String unitReturn() {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, "MyHistorydb.db");
        database = dbOpenHelper.openDataBase();

        //put cursor
        Cursor cursor = database.rawQuery("select unit from history_tbl_WG where name='" +goalName+ "'", null);
        cursor.moveToFirst();
        String unit = null;
        if (!cursor.isAfterLast()) {
            do {

                unit = cursor.getString(cursor.getColumnIndex("unit"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return unit;
    }



    }
