package com.example.bill.keepfit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
    private String dateTM;
    private Integer numberTM;
    private int exists=0;
    private Boolean cClear=false;
    private String  clearDate="";
    private Boolean clearMode=false;
    private boolean keepValue=false;
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

        //hereeeeeeeeeeeeeeeeeeeeeeeeee i open the shared preferences of the test mode
        SharedPreferences testModePreferences = this.getSharedPreferences("textModeSetting", MODE_PRIVATE);
        dateTM=testModePreferences.getString("date", null);
        numberTM=testModePreferences.getInt("testM",0);

        //hereeeeeeeeeeeeeeeeeeeeeeeeee i open the shared preferences of the clear
      //  SharedPreferences clearPreferences = this.getSharedPreferences("clearSetting", MODE_PRIVATE);
       // clearMode=clearPreferences.getBoolean("returnClear", false);
     //   clearDate = clearPreferences.getString("returnClearDate", "nothing");


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

              //  if(numberTM==0) {
                    //this enables the clear history to work properly
                //    if (checkIfTableIsEmpty() == true && !clearDate.equals(curDateHistory)) {
                       // storeActiveGoal();
                 //   }

               // }else{
               //     if (checkIfTableIsEmpty() == true) {
                 //       storeActiveGoal();
                 //   }
              //  }


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
                    Integer modeTest=cursor.getInt(cursor.getColumnIndex("testMode"));
                   // System.out.println("Percentage is: "+percentage);
                    dateTime = cursor.getString(cursor.getColumnIndex("date"));
                   // System.out.println("Day created was: "+dateTime);
                    //hereeeeeeee test mode again
                    if(numberTM==1  && modeTest==2){ //&& dateTime.equals(dateTM)){
                        System.out.println("here in test mode");
                        goalList.add(dateTime+ "\n" +"Name: " + name + " || Steps: " + steps + " || Percentage: " + (int) ((percentage*100)+0.5) + "%"  +" ||" +"\n" +"Steps Walked: " + stepsDid);

                    }

                        if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory)<0) && numberTM==0) {
                            System.out.println("difference in days in if: " + differenceInDays());
                            goalList.add(dateTime+ " " +"Name: " + name + " || Steps: " + steps + " || Percentage: " + (int) ((percentage*100)+0.5) + "%" + " || Steps Walked: " + stepsDid);

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
     //   System.out.println(DateToStr);
        return DateToStr;
    }

    public long differenceInDays(){

        long days=0;
        //the format of the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
        SQLiteDatabase databasehelp = null;
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

                if(name.equals(checkIsTheSame()) && dateSearch.equals(checkIsTheDate())){
                    exists=1;
                }else{
                    exists=0;
                }
                System.out.println("To exists einai: " +exists);
            //edw htan prin
            } while (cursor.moveToNext());
        }
        cursor.close();

        //open the database
        ExternalDbOpenHelper dbOpenHelper1 = new ExternalDbOpenHelper(this, DB_NAME);
        databasehelp = dbOpenHelper1.openDataBase();
        //here test mode again
        System.out.println("einai to test mode: " +numberTM);
        System.out.println(dateSearch);
        System.out.println(dateTM);
        if(numberTM==1 && dateSearch.equals(dateTM)){
            System.out.println(name);
            if(name.equals(checkIsTheSame()) && exists!=0){
                System.out.println("Exists in the history table");
                databasehelp.execSQL("UPDATE history_tbl_WG SET allsteps='"+steps+"' WHERE name='"+name+"'");
                databasehelp.execSQL("UPDATE history_tbl_WG SET didsteps='"+stepsDid+"' WHERE name='"+name+"'");
                databasehelp.execSQL("UPDATE history_tbl_WG SET percentage='"+percentage+"' WHERE name='"+name+"'");
            }else{
                System.out.println("insert apo to test mode mias kai den exist");
                databasehelp.execSQL("insert into history_tbl_WG values('" + name + "','" + steps + "','" + stepsDid + "','" + percentage  + "','" + activeNumber + "','" + dateTM + "')");
            }

        }


            if(numberTM==0 && exists==0) {
                if ((dateSearch.compareTo(curDateHistory) < 0) && dateSearch != null && dateSearch != "") {
                    System.out.println("palia: " + dateSearch);
                    System.out.println("Nea: " + curDateHistory);
                    databasehelp.execSQL("insert into history_tbl_WG values('" + name + "','" + steps + "','" + stepsDid + "','" + percentage + "','" + activeNumber + "','" + dateSearch + "')");
                    System.out.println("insert the data to history");
                } else {
                    System.out.println("Date has not changed! Nothing is inserted!");
                }

            }


        //close the databases
        database.close();
        databasehelp.close();
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
                System.out.println(name +" " +steps +" " +stepsDid +" " +percentage + " " +dateSearch +" " +activeNumber);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
    }

    public String checkIsTheSame() {
        String name = null;
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        Cursor cursor = database.rawQuery("select name from history_tbl_WG where active='" + 1 + "'", null);
        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            // if(cursor.moveToFirst()){
            do {
                // System.out.println("Retrieve data now");
                name = cursor.getString(cursor.getColumnIndex("name"));
                //Integer steps = cursor.getInt(cursor.getColumnIndex("allsteps"));
                //Integer stepsDid = cursor.getInt(cursor.getColumnIndex("didsteps"));
                //Integer percentage = cursor.getInt(cursor.getColumnIndex("percentage"));
                //dateTime = cursor.getString(cursor.getColumnIndex("date"));


            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        System.out.println("checkIsTheSame: " +name);
        return name;
    }

    public String checkIsTheDate() {
        String date = null;
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        Cursor cursor = database.rawQuery("select date from history_tbl_WG where active='" + 1 + "'", null);
        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            // if(cursor.moveToFirst()){
            do {
                // System.out.println("Retrieve data now");
                date = cursor.getString(cursor.getColumnIndex("date"));
                //Integer steps = cursor.getInt(cursor.getColumnIndex("allsteps"));
                //Integer stepsDid = cursor.getInt(cursor.getColumnIndex("didsteps"));
                //Integer percentage = cursor.getInt(cursor.getColumnIndex("percentage"));
                //dateTime = cursor.getString(cursor.getColumnIndex("date"));


            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        System.out.println("checkIsTheSame: " +date);
        return date;
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
        // database.execSQL("delete from "+ TABLE_NAME);
        if(numberTM==0) {
            database.delete(TABLE_NAME, null, null);
            database.close();
        }else{
            deleteTestModeGoals();
        }
        //  cClear=true;
      //  SharedPreferences.Editor editor = getSharedPreferences("clearSetting", MODE_PRIVATE).edit();
        //  editor.putBoolean("returnClear", cClear);
      //  editor.putString("returnClearDate", curDateHistory);
      //  editor.commit();
        //reload the activity instantly
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
    }
