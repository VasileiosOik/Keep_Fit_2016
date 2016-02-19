package com.example.bill.keepfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    private float prefNameSteps;
    private String goalName;
    private String curDateHistory;
    private String dateTime;
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
        prefNameSteps = myPrefs.getFloat("MyData2", 0);//current steps
        goalName=myPrefs.getString("MyData3", "0");//name of the current goal


        //here we initialize the listview to the list in the xml file
        mainListView = (ListView) findViewById( R.id.goal_list );

        //get the present date
        curDateHistory = getCurrentDate();

        print();

        //insert the percentage
     //   saveDatabase(prefName);
        if(checkIfTableIsEmpty()==true){
            storeActiveGoal();
        }


        //here the list of goals is appeared in the main screen
        display1(v1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clear, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //no inspection Simplifiable If Statement
        if (id == R.id.action_clear) {
            ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
            database = dbOpenHelper.openDataBase();
            //clear the history
            database.execSQL("delete from "+ TABLE_NAME);
            database.close();
            //reload the activity instantly
            Intent intent = getIntent();
            //no animation
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
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
/*
private void saveDatabase(String portion) {
    float percentage;
    //The database is open!
    ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
    database = dbOpenHelper.openDataBase();


    if (portion != null && !portion.isEmpty()) {
        // doSomething

        //we want here to update the value with a new one given by the user
     //   try {
            percentage=(Float.parseFloat(prefName)*100);

      //  }
      //  catch(NumberFormatException ex) {
      //      percentage = 0; // default ??

      //  }
    }else{
        percentage = 0;
    }


    System.out.println("The percentage is: " + percentage);
    //  int percentage=(Integer.parseInt(prefName));
    database.execSQL("UPDATE history_tbl_WG SET percentage='"+  (percentage+0.5) +"' WHERE name='"+goalName+"'");
    System.out.println("Name of the goal from function: " + goalName);
}
*/
    public void display1(View v){
        SharedPreferences sharedPreferences = getSharedPreferences("status", MODE_PRIVATE);
        String goal_to_remember=sharedPreferences.getString("MyGoal1", "0");
      //  System.out.println(goal_to_remember);
        if(goal_to_remember.equals("0")){
            //dont show anything
        }else {
            String data[] = goal_to_remember.split(" ");
            //store the int value that we want to edit
            //  helpInt=Integer.parseInt(data[data.length-1].replace("]",""));//the total steps of one specific goal
            //store the name of the goal that was chosen
            String nameOfCurrentGoal = data[data.length - 4];
            System.out.println(nameOfCurrentGoal);

            //The database is open!
            ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
            database = dbOpenHelper.openDataBase();
            //here i retrieve the data i want
            goalList = new ArrayList<String>();



          //  Cursor cursor = database.rawQuery("select * from history_tbl_WG where name='" + nameOfCurrentGoal + "'", null);
            Cursor cursor = database.rawQuery("select * from history_tbl_WG where active='" + 1 + "'", null);
            cursor.moveToFirst();
         //   System.out.println("edw");
            if (!cursor.isAfterLast()) {
           //     System.out.println("edw1");
                do {
                    System.out.println("Retrieve data now and checking the date...");
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    Integer steps = cursor.getInt(cursor.getColumnIndex("allsteps"));
                    Integer stepsDid = cursor.getInt(cursor.getColumnIndex("didsteps"));
                    Float percentage = cursor.getFloat(cursor.getColumnIndex("percentage"));
                   // System.out.println("Percentage is: "+percentage);
                    dateTime = cursor.getString(cursor.getColumnIndex("date"));
                   // System.out.println("Day created was: "+dateTime);
                    if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory)<0)) {
                        System.out.println("difference in days in if: " + differenceInDays());
                        goalList.add("Name: " + name + " || Steps: " + steps + " || Percentage: " + (int) ((percentage*100)+0.5) + "%" + " || Steps Walked: " + stepsDid);

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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String DateToStr = format.format(curDate);
     //   System.out.println(DateToStr);
        return DateToStr;
    }

    public long differenceInDays(){

        long days=0;
        //the format of the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Date oldDate = dateFormat.parse(dateTime);
         //   System.out.println(oldDate);
          //  System.out.println("Now date: " +oldDate);

            Date currentDate =  dateFormat.parse(curDateHistory);
         //   System.out.println("Now date: " +currentDate);

          //  long diff = currentDate.getTime() - oldDate.getTime();
        //    long seconds = diff / 1000;
        //    long minutes = seconds / 60;
        //    long hours = minutes / 60;
        //    days = hours / 24;

            if(oldDate.compareTo(currentDate)==0){
                days=0;

            }else{
                days=1;
            }
            /*
            if (oldDate.before(currentDate)) {

                Log.e("oldDate", "is previous date");
                Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes + " hours: " + hours + " days: " + days);
              //  System.out.println("The days passed are: " +days);

            }
            */

        } catch (ParseException e) {

            e.printStackTrace();
        }

        return days;
    }

    public void storeActiveGoal(){
        String name="";
        Integer steps=0;
        Integer stepsDid=0;
        Float percentage=0f;
        String dateSearch="";
        Integer activeNumber=0;
        SQLiteDatabase databasehelp;
        //here i store the active goal that i want to transfer
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, "MyHelpdb.db");
        database = dbOpenHelper.openDataBase();

        Cursor cursor = database.rawQuery("select * from time_tbl_WG where active='"+1+"'", null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {

            do {
                System.out.println("Collect the data from the active goal");
                name = cursor.getString(cursor.getColumnIndex("name"));
                steps = cursor.getInt(cursor.getColumnIndex("allsteps"));
                stepsDid = cursor.getInt(cursor.getColumnIndex("didsteps"));
                percentage = cursor.getFloat(cursor.getColumnIndex("percentage"));
                dateSearch = cursor.getString(cursor.getColumnIndex("date"));
                activeNumber=cursor.getInt(cursor.getColumnIndex("active"));


            } while (cursor.moveToNext());
        }
        cursor.close();


        //open the database
        ExternalDbOpenHelper dbOpenHelper1 = new ExternalDbOpenHelper(this, DB_NAME);
        databasehelp = dbOpenHelper1.openDataBase();


        if((dateSearch.compareTo(curDateHistory)<0) && dateSearch!=null && dateSearch!="") {
            System.out.println("palia: " +dateSearch);
            System.out.println("Nea: " +curDateHistory);
            databasehelp.execSQL("insert into history_tbl_WG values('" + name + "','" + steps + "','" + stepsDid + "','" + percentage  + "','" + activeNumber + "','" + dateSearch + "')");
            System.out.println("insert the data to history");
        }else{
            System.out.println("Date has not changed! Nothing is inserted!");
        }


    }

    public boolean checkIfTableIsEmpty() {
        boolean flag=false;
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, "MyHistorydb.db");
        database = dbOpenHelper.openDataBase();

        String count = "SELECT count(*) FROM history_tbl_WG";
        Cursor mcursor = database.rawQuery(count, null);
        mcursor.getCount();

        if(mcursor != null && mcursor.getCount()>0){
            mcursor.moveToFirst();
            flag=true;
        }
        else {
            flag=false;
        }

        return flag;
    }

    public void print() {
        String name = "";
        Integer steps = 0;
        Integer stepsDid = 0;
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
                stepsDid = cursor.getInt(cursor.getColumnIndex("didsteps"));
                percentage = cursor.getFloat(cursor.getColumnIndex("percentage"));
                dateSearch = cursor.getString(cursor.getColumnIndex("date"));
                activeNumber = cursor.getInt(cursor.getColumnIndex("active"));


            } while (cursor.moveToNext());
        }
        cursor.close();

    }
    }
