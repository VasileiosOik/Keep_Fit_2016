package com.example.bill.keepfit;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
    private Spinner spinnerDays;
    private Spinner spinnerPercentage;
    private String[] state = { "Normal View" ,"Monthly View", "Week View", "Specific Period"};
    private String[] state1={  "More Views", "Completed","% Above", "% Below", "Kilometres", "Miles", "Meters", "Yards","Steps"};
    private int currentMonth;
    private int year;
    private int month;
    private int day;
    private static final int DATE_DIALOG_ID = 1;
    private static final int DATE_DIALOG_ID2 = 2;
    private int cur=0;
    private String date1,date2;
    private int stateOption=0;
    private int perCentage;
    private String stDate1;
    private String stDate2;
    private int positionSpinner;
    private int value=1;
    private int unitForMenu;
    private String  unitToInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the content of the history screen
        setContentView(R.layout.activity_history);
        //make visible the back button in the action bar <-
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //spinner 2
        spinnerDays = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, state);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDays.setAdapter(adapter_state);
      //  spinnerDays.setOnItemSelectedListener(this);

        spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {


                spinnerDays.setSelection(pos);
                String selState = (String) spinnerDays.getSelectedItem();

                if(selState.equals("Monthly View")){
                    stateOption=2;
                    positionSpinner=0;
                    value=0;
                    choiceView(v1);
                }else if(selState.equals("Week View")){
                    stateOption=3;
                    positionSpinner=0;
                    value=0;
                    choiceView(v1);
                }else if(selState.equals("Normal View")){
                    stateOption=1;
                    positionSpinner=0;
                    value=0;
                    display1(v1);
                }else if(selState.equals("Specific Period")){
                    positionSpinner=3;
                    stateOption=4;

                    SharedPreferences preferences = HistoryActivity.this.getSharedPreferences("nbRepet", MODE_PRIVATE);
                    if(value==1){
                        value = preferences.getInt("nbRepet", 0);
                    }else{
                        value=0;
                    }


                    if(value<1)
                    {
                        Intent b = new Intent(HistoryActivity.this, DatePickerActivity.class);
                        startActivity(b);
                    }else{
                        System.out.println("Bika mia fora");
                        choiceView(v1);
                    }

                    


                }

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        //spinner 1
        spinnerPercentage = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter_state1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, state1);
        adapter_state1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPercentage.setAdapter(adapter_state1);
      //  spinnerPercentage.setOnItemSelectedListener(this);

        spinnerPercentage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                spinnerPercentage.setSelection(pos);
                String selState1 = (String) spinnerPercentage.getSelectedItem();

                if(selState1.equals("More Views") ) {
                    stateOption=1;
                  //  display1(v1);
                }else if(selState1.equals("Completed")){
                    stateOption=6;
                    choiceView(v1);
                }else if(selState1.equals("% Above")){
                    stateOption=7;
                    showEdit();
                }else if(selState1.equals("% Below")) {
                    stateOption = 8;
                    showEdit();
                }else if(selState1.equals("Kilometres")){
                    unitForMenu=1;
                    unitToInsert="Kilometres";
                    unitChoiceToDisplay(v1);
                }else if(selState1.equals("Meters")){
                    unitForMenu=2;
                    unitToInsert="Meters";
                    unitChoiceToDisplay(v1);
                }else if(selState1.equals("Miles")){
                    unitForMenu=3;
                    unitToInsert="Miles";
                    unitChoiceToDisplay(v1);
                }else if(selState1.equals("Yards")){
                    unitForMenu=4;
                    unitToInsert="Yards";
                    unitChoiceToDisplay(v1);
                }else if(selState1.equals("Steps")){
                    unitForMenu=5;
                    unitToInsert="Steps";
                    unitChoiceToDisplay(v1);
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

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
            String[] data=curDateHistory.split("/");
            currentMonth=Integer.parseInt(data[data.length-2]);
        }else{
            curDateHistory=dateTM;
            String[] data=curDateHistory.split("/");
            currentMonth=Integer.parseInt(data[data.length-2]);
        }

        //see what history table has inside
        print();


        //here the list of goals is appeared in the main screen
     //   display1(v1);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clear, menu);

        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        prefs = getSharedPreferences("nbRepet",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putInt("nbRepet", 0);
        editor.commit();
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


    private void choiceView(View v) {
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
                    System.out.println("To pososto einai: " +percentage);
                    Integer modeTest=cursor.getInt(cursor.getColumnIndex("testMode"));
                    dateTime = cursor.getString(cursor.getColumnIndex("date"));
                    String[] splitDate=dateTime.split("/");
                    int month=Integer.parseInt(splitDate[splitDate.length-2]);
                    long daysDifference=Daybetween(curDateHistory,dateTime);
                    System.out.println("oi meres einai: " +daysDifference);

                    if(stateOption==2){


                        //hereeeeeeee test mode again
                        if(numberTM==1  && modeTest==2 && (currentMonth-month==1) ){
                            System.out.println("here in test mode");
                            if(unitReturn().equals("Steps")) {
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + new Double(stepsDid).longValue());
                            }else{
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                            }
                        }

                        if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory)<0) && numberTM==0 && (currentMonth-month==1)) {
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

                    }else if(stateOption==3){
                        //hereeeeeeee test mode again
                        if(numberTM==1  && modeTest==2 && (daysDifference>=1 && daysDifference<=7) ){
                            System.out.println("here in test mode");
                            if(unitReturn().equals("Steps")) {
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + new Double(stepsDid).longValue());
                            }else{
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                            }
                        }

                        if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory)<0) && numberTM==0 && (daysDifference>=1 && daysDifference<=7)) {
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

                    }else if(stateOption==6){
                        //hereeeeeeee test mode again
                        if(numberTM==1  && modeTest==2 && percentage>=1.00 ){
                            System.out.println("here in test mode");
                            if(unitReturn().equals("Steps")) {
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + new Double(stepsDid).longValue());
                            }else{
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                            }
                        }

                        if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory)<0) && numberTM==0 && percentage>=1.00) {
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

                    }else if(stateOption==7){
                        //hereeeeeeee test mode again
                        if(numberTM==1  && modeTest==2 && (percentage*100>=perCentage) ){
                            System.out.println("here in test mode");
                            if(unitReturn().equals("Steps")) {
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + new Double(stepsDid).longValue());
                            }else{
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                            }
                        }

                        if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory)<0) && numberTM==0 && (percentage*100>=perCentage) ) {
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

                    }else if(stateOption==8) {
                        //hereeeeeeee test mode again
                        if (numberTM == 1 && modeTest == 2 && (percentage * 100 <= perCentage)) {
                            System.out.println("here in test mode");
                            if (unitReturn().equals("Steps")) {
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + new Double(stepsDid).longValue());
                            } else {
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                            }
                        }

                        if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory) < 0) && numberTM == 0 && (percentage * 100 <= perCentage)) {
                            System.out.println("difference in days in if: " + differenceInDays());
                            if (unitReturn().equals("Steps")) {
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitReturn() + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitReturn() + " Walked: " + new Double(stepsDid).longValue());

                            } else {
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitReturn() + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitReturn() + " Walked: " + df2.format(stepsDid));
                            }

                        } else {
                            //nothing
                            System.out.println("difference in days on else: " + differenceInDays());
                        }
                    }else if(stateOption==4){
                       Boolean checkRange= isWithinRange(dateTime);
                        System.out.println("Einai to apot: " +checkRange);
                        //hereeeeeeee test mode again
                        if (numberTM == 1 && modeTest == 2 && checkRange==true) {
                            System.out.println("here in test mode");
                            if (unitReturn().equals("Steps")) {
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + new Double(stepsDid).longValue());
                            } else {
                                System.out.println("here in test mode againnnnn");
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                            }
                        }

                        if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory) < 0) && numberTM == 0 && checkRange==true) {
                            System.out.println("difference in days in if: " + differenceInDays());
                            if (unitReturn().equals("Steps")) {
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitReturn() + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitReturn() + " Walked: " + new Double(stepsDid).longValue());

                            } else {
                                goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitReturn() + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitReturn() + " Walked: " + df2.format(stepsDid));
                            }

                        } else {
                            //nothing
                            System.out.println("difference in days on else: " + differenceInDays());
                        }
                    }


                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();


            ListAdapter adapter = new ListAdapter(this, goalList);
            mainListView.setAdapter(adapter);
        }


    }


    public long Daybetween(String date1,String date2)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date Date1 = null,Date2 = null;
        try{
            Date1 = sdf.parse(date1);
            Date2 = sdf.parse(date2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return (Date1.getTime() - Date2.getTime())/(24*60*60*1000);
    }

   public void showEdit(){
       AlertDialog.Builder alert = new AlertDialog.Builder(this);


       alert.setTitle("Give a percentage %");
       //alert.setMessage("Message");

       // Set an EditText view to get user input
       final EditText input = new EditText(this);
       input.setInputType(InputType.TYPE_CLASS_NUMBER);
       alert.setView(input);

       alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
               View v2=null;
               // Do something with value!
               perCentage=Integer.parseInt(input.getText().toString().trim());
               choiceView(v2);
           }
       });

       alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
               // Canceled.
           }
       });

       alert.show();
   }

    boolean isWithinRange(String testDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SharedPreferences datePickerPref = getSharedPreferences("myDatePicker", MODE_PRIVATE);
        stDate1=datePickerPref.getString("date1", null);
        stDate2=datePickerPref.getString("date2",null);
        Date convertedDate1 = null;
        Date convertedDate2=null;
        Date tDate=null;
        try {
            convertedDate1 = dateFormat.parse(stDate1);
            convertedDate2= dateFormat.parse(stDate2);
            tDate=dateFormat.parse(testDate);
            System.out.println("Einai h prwth: " +convertedDate1);
            System.out.println("Einai h Deuterh: " +convertedDate2);
            System.out.println("Einai h eksetazomenh: " +tDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tDate.after(convertedDate1) && tDate.before(convertedDate2);
    }

    @Override
    public void onPause() {
        super.onPause();
        save(positionSpinner);
    }

    @Override
    public void onResume() {
        super.onResume();
        spinnerDays.setSelection(load());
    }

    private void save(final int isSelected) {
        int selectedPosition;
        SharedPreferences preferences = HistoryActivity.this.getSharedPreferences("nbRepet", MODE_PRIVATE);
        value = preferences.getInt("nbRepet", 0);

        if(value==1){
            selectedPosition=3;
        }else{
            selectedPosition=0;
        }
        SharedPreferences sharedPreferences = this.getSharedPreferences("spinnerDaysState",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
       // selectedPosition = isSelected;
        editor.putInt("spinnerDaysSelection", selectedPosition);
        editor.commit();
    }

    private int load() {


        SharedPreferences prefs = getSharedPreferences("spinnerDaysState", MODE_PRIVATE);
        int iPos=prefs.getInt("spinnerDaysSelection",0);
        return iPos;
    }

    public void unitChoiceToDisplay(View v){
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
                    System.out.println("Total vimata: " +steps);
                    Double stepsDid = cursor.getDouble(cursor.getColumnIndex("didsteps"));
                    System.out.println("Ekane vimata: " +stepsDid);
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
                            if(unitForMenu==1){
                                if(unit.equals("Yards")){
                                    System.out.println("BIKA1");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*(0.0009144)) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*0.0009144));

                                }else if(unit.equals("Meters")){
                                    System.out.println("BIKA2");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.001))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.001)));

                                }else if(unit.equals("Miles")){
                                    System.out.println("BIKA3");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1.609344))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1.609344)));

                                }else if(unit.equals("Kilometres")){
                                    System.out.println("BIKA4");
                                      goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                                }else{
                                    System.out.println("BIKA5");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.000762))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.000762)));
                                }

                            }else if(unitForMenu==2){
                                if(unit.equals("Yards")){
                                    System.out.println("BIKA1");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*(0.000568181818)) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*0.000568181818));

                                }else if(unit.equals("Meters")){
                                    System.out.println("BIKA2");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.000621371192))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.000621371192)));

                                }else if(unit.equals("Miles")){
                                    System.out.println("BIKA3");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));


                                }else if(unit.equals("Kilometres")){
                                    System.out.println("BIKA4");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.621371192))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.621371192)));

                                }else{
                                    System.out.println("BIKA5");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.0004734848484848485))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.0004734848484848485)));
                                }

                            }else if(unitForMenu==3){
                                if(unit.equals("Yards")){
                                    System.out.println("BIKA1");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*(0.9144)) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*0.9144));

                                }else if(unit.equals("Meters")){
                                    System.out.println("BIKA2");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));


                                }else if(unit.equals("Miles")){
                                    System.out.println("BIKA3");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1609))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1609)));


                                }else if(unit.equals("Kilometres")){
                                    System.out.println("BIKA4");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1000))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1000)));

                                }else{
                                    System.out.println("BIKA5");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.762))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.762)));
                                }

                            }else if(unitForMenu==4){

                                if(unit.equals("Yards")){
                                    System.out.println("BIKA1");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));


                                }else if(unit.equals("Meters")){
                                    System.out.println("BIKA2");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*(1.0936133)) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*1.0936133));


                                }else if(unit.equals("Miles")){
                                    System.out.println("BIKA3");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1760))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1760)));


                                }else if(unit.equals("Kilometres")){
                                    System.out.println("BIKA4");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1093))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1093)));

                                }else{
                                    System.out.println("BIKA5");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.8333333333333334))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.8333333333333334)));
                                }

                            }else{
                                if(unit.equals("Yards")){
                                    System.out.println("BIKA1");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1.2))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1.2)));


                                }else if(unit.equals("Meters")){
                                    System.out.println("BIKA2");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*(1.31)) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*1.31));


                                }else if(unit.equals("Miles")){
                                    System.out.println("BIKA3");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((2112))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(2112)));


                                }else if(unit.equals("Kilometres")){
                                    System.out.println("BIKA4");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1312))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1312)));

                                }else{
                                    System.out.println("BIKA5");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                                }

                            }
                          //  goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                        }
                    }

                    if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory)<0) && numberTM==0) {
                        System.out.println("difference in days in if: " + differenceInDays());
                        if(unitReturn().equals("Steps")){
                            goalList.add(dateTime+ "\n" +"Name: " + name + " || "+unitReturn()+": " + steps + " || Percentage: " + (int) ((percentage*100)+0.5) + "%"  +" ||" +"\n" +unitReturn()+" Walked: " + new Double(stepsDid).longValue());

                        }else{
                            if(unitForMenu==1){
                                if(unit.equals("Yards")){
                                    System.out.println("BIKA1");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*(0.0009144)) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*0.0009144));

                                }else if(unit.equals("Meters")){
                                    System.out.println("BIKA2");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.001))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.001)));

                                }else if(unit.equals("Miles")){
                                    System.out.println("BIKA3");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1.609344))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1.609344)));

                                }else if(unit.equals("Kilometres")){
                                    System.out.println("BIKA4");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                                }else{
                                    System.out.println("BIKA5");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.000762))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.000762)));
                                }

                            }else if(unitForMenu==2){
                                if(unit.equals("Yards")){
                                    System.out.println("BIKA1");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*(0.000568181818)) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*0.000568181818));

                                }else if(unit.equals("Meters")){
                                    System.out.println("BIKA2");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.000621371192))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.000621371192)));

                                }else if(unit.equals("Miles")){
                                    System.out.println("BIKA3");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));


                                }else if(unit.equals("Kilometres")){
                                    System.out.println("BIKA4");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.621371192))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.621371192)));

                                }else{
                                    System.out.println("BIKA5");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.0004734848484848485))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.0004734848484848485)));
                                }

                            }else if(unitForMenu==3){
                                if(unit.equals("Yards")){
                                    System.out.println("BIKA1");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*(0.9144)) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*0.9144));

                                }else if(unit.equals("Meters")){
                                    System.out.println("BIKA2");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));


                                }else if(unit.equals("Miles")){
                                    System.out.println("BIKA3");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1609))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1609)));


                                }else if(unit.equals("Kilometres")){
                                    System.out.println("BIKA4");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1000))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1000)));

                                }else{
                                    System.out.println("BIKA5");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.762))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.762)));
                                }

                            }else if(unitForMenu==4){

                                if(unit.equals("Yards")){
                                    System.out.println("BIKA1");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));


                                }else if(unit.equals("Meters")){
                                    System.out.println("BIKA2");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*(1.0936133)) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*1.0936133));


                                }else if(unit.equals("Miles")){
                                    System.out.println("BIKA3");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1760))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1760)));


                                }else if(unit.equals("Kilometres")){
                                    System.out.println("BIKA4");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1093))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1093)));

                                }else{
                                    System.out.println("BIKA5");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((0.8333333333333334))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(0.8333333333333334)));
                                }

                            }else{
                                if(unit.equals("Yards")){
                                    System.out.println("BIKA1");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1.2))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1.2)));


                                }else if(unit.equals("Meters")){
                                    System.out.println("BIKA2");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*(1.31)) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*1.31));


                                }else if(unit.equals("Miles")){
                                    System.out.println("BIKA3");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((2112))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(2112)));


                                }else if(unit.equals("Kilometres")){
                                    System.out.println("BIKA4");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unitToInsert + ": " + df2.format(steps*((1312))) + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unitToInsert + " Walked: " + df2.format(stepsDid*(1312)));

                                }else{
                                    System.out.println("BIKA5");
                                    goalList.add(dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                                }

                            }
                           // goalList.add(dateTime+ "\n" +"Name: " + name + " || "+unitReturn()+": " + steps + " || Percentage: " + (int) ((percentage*100)+0.5) + "%"  +" ||" +"\n" +unitReturn()+" Walked: " + df2.format(stepsDid));
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

}
