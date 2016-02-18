package com.example.bill.keepfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Pedometer2Activity extends AppCompatActivity {

    private static SensorManager mManager;
    private static Sensor mStepCounter;
    private static long STEP_COUNT = 0;
    private EditText editText;
    private TextView mTvStep;
    private TextView tvchoicestep;
    private int helpInt;
    private String dataValue;
    private long stepsToStartAgain;
    private String helpName;
    private long newStepsStore;
    private SQLiteDatabase database;
    private String curDate;
    private static final String DB_NAME = "MyHelpdb.db";
    private static final String TABLE_NAME = "time_tbl_WG";
    private int activeGoal=0;
    private float st;
    private float st1;
    private String dateTime;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer2);


        //retract the incoming intent
        Intent intent = getIntent();
        //recover the row that we want to edit
        dataValue = intent.getExtras().getString("string");
        //split the whole string to parts
        String data[] =dataValue.split(" ");
        //store the int value that we want to edit
        helpInt=Integer.parseInt(data[data.length-1].replace("]",""));//the total steps of one specific goal
        //store the name of the goal that was chosen
        helpName=data[data.length-4];

        //initialize the Texts
        editText=(EditText) findViewById(R.id.editgoal);
        tvchoicestep=(TextView) findViewById(R.id.tv_choice_step);
        mTvStep=(TextView) findViewById(R.id.tv_current);

        //shared preferences
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        float prefNameSteps = myPrefs.getFloat("MyData2", 0);
        stepsToStartAgain=(long) prefNameSteps;

        //return the current date that the goal started
        curDate=getCurrentDate();



        if(checkIsTheSame() != null && !checkIsTheSame().isEmpty() && checkIsTheSame().equals(helpName) ) {
            activeGoal=myPrefs.getInt("MyData4", 0);
        }else{
            activeGoal=0;
        }

        //here i play with time
        if(checkDifInTime()>=1 && activeGoal==1){
            System.out.println("Date changed! clear the data from the table!");
            stepsToStartAgain=0;
            activeGoal=0;
            clearCurrentTable();
        }


        //previous steps
        mTvStep.setText("Previous Steps: " + String.valueOf(stepsToStartAgain));

       //here was the current date before


        //here i chekc if the n-th goal that has entered is the same as the previous active goal and
        //if it is then continue to be one
        //otherwise make it innactive by giving zero to the active state
        System.out.println(helpName);
        System.out.println(checkIsTheSame());
        if(!helpName.equals(checkIsTheSame()) && checkDifInTime()<1 && checkIsTheSame() != null){
           // System.out.println(checkDifInTime());
            //The database is open!
            ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
            database = dbOpenHelper.openDataBase();

            try {
                database.execSQL("UPDATE time_tbl_WG SET active='"+0+"' WHERE name='"+checkIsTheSame()+"'");
                System.out.println("The previous is not active, 0 has inserted: " +checkIsTheSame());
                System.out.println("null is the name of the checkIsTheSame  after 0 has inserted " +checkIsTheSame());
            } catch (SQLException e) {
                System.out.println("An error occured");
            }

        }

      //  System.out.println("I am the " + activeGoal);

        tvchoicestep.setText(" / " +"GOAL : " + String.valueOf(helpInt));
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                        //nothing happened
                    newStepsStore=stepsToStartAgain+0;
                } else {


               // System.out.println("Current Steps are: " + stepsToStartAgain);
              //  System.out.println("Total Goal steps are: " + helpInt);
                if (stepsToStartAgain == 0) {
                    //  STEP_COUNT = 0;
                    newStepsStore = Long.parseLong(editText.getText().toString().trim());
                   // if(newStepsStore>helpInt){
                   //     newStepsStore=helpInt;
                  //  }
                    // setStep(STEP_COUNT);
                    System.out.println("1");

                } else {
                    System.out.println("2");
                    if (stepsToStartAgain > helpInt) {
                      //  STEP_COUNT = 0;
                       // newStepsStore = 0;
                        //add the previous steps
                        newStepsStore = stepsToStartAgain+Long.parseLong(editText.getText().toString().trim());
                        System.out.println("3");
                        //   setStep(STEP_COUNT);

                        //  newStepsStore=STEP_COUNT;
                    } else {
                        STEP_COUNT = stepsToStartAgain;
                        newStepsStore = (Long.parseLong(editText.getText().toString().trim()) + STEP_COUNT);
                        if (newStepsStore > helpInt) {
                          //  newStepsStore = helpInt;
                            //new
                            System.out.println("4");
                          //  newStepsStore=0;
                        } else if (newStepsStore == helpInt) {
                         //   newStepsStore = helpInt;
                            //new
                            System.out.println("5");
                         //   newStepsStore=0;
                        } else {
                            //nothing
                            System.out.println("6");
                        }
                        //   setStep(STEP_COUNT);
                    }

                }
            }
                //editText.setText("");
                System.out.println("Current steps: " + newStepsStore);
                System.out.println("Total Goal steps are: " + helpInt);

            }
        });


    }


    // Physical back button click handler
    @Override
    public void onBackPressed() {

        if (editText.getText().toString().trim().equals("")) {
            //default value 0 if user doesnt insert a number
            newStepsStore=stepsToStartAgain+0;
        }

        //split the whole string to parts
     //   if (editText.getText().toString().trim().equals("")) {
      //      Toast.makeText(Pedometer2Activity.this, "Insert a number", Toast.LENGTH_LONG).show();

    //    }
      //  else {
          //  String data[] = editText.getText().toString().split(" ");
            String dataa[] = tvchoicestep.getText().toString().split(" ");
            //store the int value that we want to edit
          //  float st = Float.parseFloat(data[data.length - 1]);
            st=newStepsStore;
            st1 = Float.parseFloat(dataa[dataa.length - 1]);
            //percentage of the current steps/total steps
            String data1 = String.valueOf((st / st1));
            //use of shared preferences
            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            prefsEditor.putString("MyData", data1);//percentage
            prefsEditor.putFloat("MyData2", st);//current steps
            prefsEditor.putString("MyData3", helpName);//name of the current goal
            System.out.println("Steps to start again are: " + (int) st);
            //hereeeeeeeeeeeeeeeeeeeeeeeeee
            if(activeGoal==0){
                activeGoal=1;
                prefsEditor.putInt("MyData4", activeGoal);
                System.out.println("Turn out to  1 <<ACTIVE>> " +helpName);
                boolean b=rowNameExists(helpName);
                if(b==true){
                    updateDB(helpName,helpInt, (int) st, Float.parseFloat(data1),activeGoal,curDate);
                }else{
                    //fill the database with the info needed
                    fillDatabase(helpName,helpInt, (int) st, Float.parseFloat(data1),activeGoal,curDate);
                }
            }else{
                updateDB(helpName,helpInt, (int) st, Float.parseFloat(data1),activeGoal,curDate);
            }
            prefsEditor.commit();
            //clear the edit text
            editText.setText("");
            super.finish();
      //  }

    }

    public String checkIsTheSame() {
        String name = null;
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        Cursor cursor = database.rawQuery("select name from time_tbl_WG where active='" + 1 + "'", null);
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
      //  database.close();
        System.out.println("checkIsTheSame: " +name);
        return name;
    }

    public long checkDifInTime() {
        String nameFIrstActive = "";
        nameFIrstActive = checkIsTheSame();


        Cursor cursor = database.rawQuery("select date from time_tbl_WG where name='" + nameFIrstActive + "'", null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {

            do {
                // String name = cursor.getString(cursor.getColumnIndex("name"));
                // Integer steps = cursor.getInt(cursor.getColumnIndex("allsteps"));
                //  Integer stepsDid = cursor.getInt(cursor.getColumnIndex("didsteps"));
                //   Integer percentage = cursor.getInt(cursor.getColumnIndex("percentage"));
                dateTime = cursor.getString(cursor.getColumnIndex("date"));


            } while (cursor.moveToNext());
        }
        cursor.close();
      //  database.close();

        //check the time difference
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long days = 0;


            if(dateTime != null && !dateTime.isEmpty()) {

                try {
                    System.out.println("Enter sto if");
                    Date oldDate = dateFormat.parse(dateTime);
                    //   System.out.println(oldDate);

                    Date currentDate = dateFormat.parse(curDate);

                 //   long diff = currentDate.getTime() - oldDate.getTime();
                 //   long seconds = diff / 1000;
                //    long minutes = seconds / 60;
               //     long hours = minutes / 60;
              //      days = hours / 24;

                    if(oldDate.compareTo(currentDate)==0){
                        days=0;

                    }else{
                        days=1;
                    }

                    /*
                    if (oldDate.before(currentDate)) {

                        //  Log.e("oldDate", "is previous date");
                        //  Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes + " hours: " + hours + " days: " + days);
                        System.out.println("The days passed are: " + days);

                    }
                    */
                } catch (ParseException e) {

                    e.printStackTrace();
                }

            }else{
                //didnt exist
                System.out.println("Enter sto else");
            }

        System.out.println("Days: " + days);
        return days;
    }

    public void updateDB(String name, int allSteps, int didSteps, float percentageSteps,int active ,String curDate){
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        database.execSQL("UPDATE time_tbl_WG SET allsteps='"+allSteps+"' WHERE name='"+helpName+"'");
        database.execSQL("UPDATE time_tbl_WG SET didsteps='"+didSteps+"' WHERE name='"+helpName+"'");
        database.execSQL("UPDATE time_tbl_WG SET percentage='"+percentageSteps+"' WHERE name='"+helpName+"'");
        //refresh the active part
       // database.execSQL("UPDATE time_tbl_WG SET active='"+active+"' WHERE name='"+helpName+"'");
    }


    public void fillDatabase(String name, int allSteps, int didSteps, float percentageSteps,int active ,String curDate ){
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        //insert data into able
        database.execSQL("insert into time_tbl_WG values('"+name+"','"+allSteps+"','"+didSteps+"','"+percentageSteps+"','"+active+"','"+curDate+"')");
        //display Toast
      //  Toast.makeText(this, "Stored successfully in table2!", Toast.LENGTH_LONG).show();
    }

    public String getCurrentDate(){
        Date curDate = new Date();
      //  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String DateToStr = format.format(curDate);
     //   System.out.println(DateToStr);
        return DateToStr;
    }

    public boolean rowNameExists(String name){
        Boolean bValue=false;
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        Cursor cursor = database.rawQuery("select name from time_tbl_WG", null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {

            do {
                 String nameRow = cursor.getString(cursor.getColumnIndex("name"));
                if(nameRow.equals(name)){
                    bValue=true;
                }
                // Integer steps = cursor.getInt(cursor.getColumnIndex("allsteps"));
                //  Integer stepsDid = cursor.getInt(cursor.getColumnIndex("didsteps"));
                //   Integer percentage = cursor.getInt(cursor.getColumnIndex("percentage"));
               // dateTime = cursor.getString(cursor.getColumnIndex("date"));


            } while (cursor.moveToNext());
        }
        cursor.close();

        return bValue;
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
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
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
        ExternalDbOpenHelper dbOpenHelper1 = new ExternalDbOpenHelper(this, "MyHistorydb.db");
        databasehelp = dbOpenHelper1.openDataBase();

        databasehelp.execSQL("insert into history_tbl_WG values('"+name+"','"+steps+"','"+stepsDid+"','"+percentage+"','"+activeNumber+"','"+dateSearch+"')");
        System.out.println("insert the data to history");



    }

    public void clearCurrentTable(){
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        database.execSQL("delete from "+ TABLE_NAME);
        database.close();
    }


}
