package com.example.bill.keepfit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Bill on 12/03/2016.
 */
public class TabFragment3 extends Fragment {
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
    private int currentMonth;
    private SQLiteDatabase database;
    private static final String TABLE_NAME = "history_tbl_WG";
    private static final String DB_NAME = "MyHistorydb.db";
    private static DecimalFormat df2 = new DecimalFormat(".##");
    private int count = 0;
    private Double min = Double.MAX_VALUE;
    private Double max = Double.MIN_VALUE;
    private Double total = 0.0;
    private String[] state1={  "More Views", "Specific Period", "Kilometres", "Miles", "Meters", "Yards","Steps"};
    private Spinner spinnerUnit;
    private int value=1;
    private int unitForMenu;
    private String  unitToInsert;
    private int stateOption=0;
    private String stDate1;
    private String stDate2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.tab_fragment_3, container,false);

        spinnerUnit = (Spinner) v.findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter_state1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, state1);
        adapter_state1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter_state1);

        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                spinnerUnit.setSelection(pos);
                String selState1 = (String) spinnerUnit.getSelectedItem();

                if(selState1.equals("More Views") ) {
                    stateOption = 0;
                }else if(selState1.equals("Specific Period")){
                    SharedPreferences preferences = getActivity().getSharedPreferences("nbRepet", getContext().MODE_PRIVATE);
                    stateOption = 1;
                    if(value==1){
                        value = preferences.getInt("nbRepet", 0);
                    }else{
                        value=0;
                    }


                    if(value<1)
                    {
                        Intent b = new Intent(getActivity(), DatePickerActivity.class);
                        startActivity(b);
                    }else{
                        System.out.println("Bika mia fora");
                        display1(v1);
                    }

                }else if(selState1.equals("Kilometres")){
                    unitForMenu=1;
                    unitToInsert="Kilometres";
                }else if(selState1.equals("Meters")){
                    unitForMenu=2;
                    unitToInsert="Meters";
                }else if(selState1.equals("Miles")){
                    unitForMenu=3;
                    unitToInsert="Miles";
                }else if(selState1.equals("Yards")){
                    unitForMenu=4;
                    unitToInsert="Yards";
                }else if(selState1.equals("Steps")){
                    unitForMenu=5;
                    unitToInsert="Steps";
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        //return the data from the pedometer activity (last goal is active there)
        SharedPreferences myPrefs = getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        prefName = myPrefs.getString("MyData", "0");//percentage
        prefNameSteps = Double.valueOf(myPrefs.getString("MyData2", String.valueOf(0.0)));//current steps
        goalName=myPrefs.getString("MyData3", "0");//name of the current goal

        //hereeeeeeeeeeeeeeeeeeeeeeeeee i open the shared preferences of the test mode
        SharedPreferences testModePreferences = getActivity().getSharedPreferences("textModeSetting", Context.MODE_PRIVATE);
        dateTM=testModePreferences.getString("date", null);
        numberTM=testModePreferences.getInt("testM",0);

        //here we initialize the listview to the list in the xml file


        mainListView = (ListView)v.findViewById(R.id.goal_list);



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


        //  return inflater.inflate(R.layout.tab_fragment_1, container, false);
     //   display1(v);
        return v;
    }

    public String getCurrentDate(){
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String DateToStr = format.format(curDate);
        return DateToStr;
    }

    public void display1(View v){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("status", getContext().MODE_PRIVATE);
        String goal_to_remember=sharedPreferences.getString("MyGoal1", "0");
        if(goal_to_remember.equals("0")){
            //dont show anything
        }else {
            String data[] = goal_to_remember.split(" ");
            //store the name of the goal that was chosen
            String nameOfCurrentGoal = data[data.length - 4];
            System.out.println(nameOfCurrentGoal);

            //The database is open!
            ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(getActivity(), DB_NAME);
            database = dbOpenHelper.openDataBase();
            //here i retrieve the data i want
            goalList = new ArrayList<String>();

            Cursor cursor = database.rawQuery("select * from history_tbl_WG where active='" + 1 + "'", null);
            cursor.moveToFirst();
            String name="";
            int steps=0;
            double stepsDid=0.0;
            String unit="";
            float percentage=0f;
            int modeTest=0;
            long daysDifference=0;
            if (!cursor.isAfterLast()) {
                do {
                    System.out.println("Retrieve data now and checking the date...");
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    steps = cursor.getInt(cursor.getColumnIndex("allsteps"));
                    stepsDid = cursor.getDouble(cursor.getColumnIndex("didsteps"));
                    unit=cursor.getString(cursor.getColumnIndex("unit"));
                    percentage = cursor.getFloat(cursor.getColumnIndex("percentage"));
                    modeTest=cursor.getInt(cursor.getColumnIndex("testMode"));
                    dateTime = cursor.getString(cursor.getColumnIndex("date"));
                    String[] splitDate=dateTime.split("/");
                    int month=Integer.parseInt(splitDate[splitDate.length-2]);
                    daysDifference=Daybetween(curDateHistory,dateTime);
                    System.out.println("oi meres einai: " +daysDifference);

                  //  if(!dateTime.equals(curDateHistory)) {
                        if (stepsDid > max) {
                            max = stepsDid;

                        }
                        if (stepsDid < min) {
                            min = stepsDid;

                        }
                        System.out.println("Min : " + min + " Max : " + max);

                        count++; // increment counter
                        total += stepsDid; // accumulate the sum
               //     }



                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();



                findMinAndPrint(min);
                findMaxAndPrint(max);
                Double average = (Double) total / count;

            // ListAdapter adapter = new ListAdapter(getActivity(), goalList);
            //  mainListView.setAdapter(adapter);
        }


    }

    private void findMaxAndPrint(Double max) {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(getActivity(), DB_NAME);
        database = dbOpenHelper.openDataBase();
        //here i retrieve the data i want
        //  goalList = new ArrayList<String>();

        Cursor cursor = database.rawQuery("select * from history_tbl_WG where didsteps='" + max + "'", null);
        cursor.moveToFirst();
        String name="";
        int steps=0;
        double stepsDid=0.0;
        String unit="";
        float percentage=0f;
        int modeTest=0;
        long daysDifference=0;
        if (!cursor.isAfterLast()) {
            do {
                System.out.println("Retrieve data now and checking the date...");
                name = cursor.getString(cursor.getColumnIndex("name"));
                steps = cursor.getInt(cursor.getColumnIndex("allsteps"));
                stepsDid = cursor.getDouble(cursor.getColumnIndex("didsteps"));
                unit=cursor.getString(cursor.getColumnIndex("unit"));
                percentage = cursor.getFloat(cursor.getColumnIndex("percentage"));
                modeTest=cursor.getInt(cursor.getColumnIndex("testMode"));
                dateTime = cursor.getString(cursor.getColumnIndex("date"));
                String[] splitDate=dateTime.split("/");
                int month=Integer.parseInt(splitDate[splitDate.length-2]);
                daysDifference=Daybetween(curDateHistory,dateTime);
                System.out.println("oi meres einai: " +daysDifference);
                Boolean checkRange= isWithinRange(dateTime);
                System.out.println("Einai to apot: " +checkRange);
                //hereeeeeeee test mode again
                if(numberTM==1  && modeTest==2 && checkRange==true){
                    System.out.println("here in test mode");


                    if(unitReturn().equals("Steps")) {
                        goalList.add("Max" +" " +dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + new Double(stepsDid).longValue());
                    }else{
                        goalList.add("Max" +" " +dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                    }
                }

                if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory)<0) && numberTM==0 && checkRange==true) {
                    System.out.println("difference in days in if: " + differenceInDays());
                    if(unitReturn().equals("Steps")){
                        goalList.add("Max" +" " +dateTime+ "\n" +"Name: " + name + " || "+unitReturn()+": " + steps + " || Percentage: " + (int) ((percentage*100)+0.5) + "%"  +" ||" +"\n" +unitReturn()+" Walked: " + new Double(stepsDid).longValue());

                    }else{
                        goalList.add("Max" +" " +dateTime+ "\n" +"Name: " + name + " || "+unitReturn()+": " + steps + " || Percentage: " + (int) ((percentage*100)+0.5) + "%"  +" ||" +"\n" +unitReturn()+" Walked: " + df2.format(stepsDid));
                    }

                } else {
                    //nothing
                    System.out.println("difference in days on else: " + differenceInDays());
                }


            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        ListAdapter adapter = new ListAdapter(getActivity(), goalList);
        mainListView.setAdapter(adapter);
    }

    private void findMinAndPrint(Double min) {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(getActivity(), DB_NAME);
        database = dbOpenHelper.openDataBase();
        //here i retrieve the data i want
        goalList = new ArrayList<String>();

        Cursor cursor = database.rawQuery("select * from history_tbl_WG where didsteps='" + min + "'", null);
        cursor.moveToFirst();
        String name="";
        int steps=0;
        double stepsDid=0.0;
        String unit="";
        float percentage=0f;
        int modeTest=0;
        long daysDifference=0;
        if (!cursor.isAfterLast()) {
            do {
                System.out.println("Retrieve data now and checking the date...");
                name = cursor.getString(cursor.getColumnIndex("name"));
                steps = cursor.getInt(cursor.getColumnIndex("allsteps"));
                stepsDid = cursor.getDouble(cursor.getColumnIndex("didsteps"));
                unit=cursor.getString(cursor.getColumnIndex("unit"));
                percentage = cursor.getFloat(cursor.getColumnIndex("percentage"));
                modeTest=cursor.getInt(cursor.getColumnIndex("testMode"));
                dateTime = cursor.getString(cursor.getColumnIndex("date"));
                String[] splitDate=dateTime.split("/");
                int month=Integer.parseInt(splitDate[splitDate.length-2]);
                daysDifference=Daybetween(curDateHistory,dateTime);
                System.out.println("oi meres einai: " +daysDifference);
                Boolean checkRange= isWithinRange(dateTime);
                System.out.println("Einai to apot: " +checkRange);

                //hereeeeeeee test mode again
                if(numberTM==1  && modeTest==2 &&  checkRange==true){
                    System.out.println("here in test mode");


                    if(unitReturn().equals("Steps")) {
                        goalList.add("Min" +" " +dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + new Double(stepsDid).longValue());
                    }else{
                        goalList.add("Min" +" " +dateTime + "\n" + "Name: " + name + " || " + unit + ": " + steps + " || Percentage: " + (int) ((percentage * 100) + 0.5) + "%" + " ||" + "\n" + unit + " Walked: " + df2.format(stepsDid));

                    }
                }

                if (differenceInDays() >= 1 && (dateTime.compareTo(curDateHistory)<0) && numberTM==0 && checkRange==true) {
                    System.out.println("difference in days in if: " + differenceInDays());
                    if(unitReturn().equals("Steps")){
                        goalList.add("Min" +" " +dateTime+ "\n" +"Name: " + name + " || "+unitReturn()+": " + steps + " || Percentage: " + (int) ((percentage*100)+0.5) + "%"  +" ||" +"\n" +unitReturn()+" Walked: " + new Double(stepsDid).longValue());

                    }else{
                        goalList.add("Min" +" " +dateTime+ "\n" +"Name: " + name + " || "+unitReturn()+": " + steps + " || Percentage: " + (int) ((percentage*100)+0.5) + "%"  +" ||" +"\n" +unitReturn()+" Walked: " + df2.format(stepsDid));
                    }

                } else {
                    //nothing
                    System.out.println("difference in days on else: " + differenceInDays());
                }


            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        //  ListAdapter adapter = new ListAdapter(getActivity(), goalList);
        //  mainListView.setAdapter(adapter);
    }

    public String unitReturn() {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(getActivity(), "MyHistorydb.db");
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

    boolean isWithinRange(String testDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SharedPreferences datePickerPref = getActivity().getSharedPreferences("myDatePicker", getContext().MODE_PRIVATE);
        stDate1=datePickerPref.getString("date1", curDateHistory);
        stDate2=datePickerPref.getString("date2",curDateHistory);
        Date convertedDate1 = null;
        Date convertedDate2=null;
        Date tDate=null;
        try {
            convertedDate1 = dateFormat.parse(stDate1);
            convertedDate2= dateFormat.parse(stDate2);
            tDate=dateFormat.parse(testDate);
//            System.out.println("Einai h prwth: " +convertedDate1);
//            System.out.println("Einai h Deuterh: " +convertedDate2);
//            System.out.println("Einai h eksetazomenh: " +tDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tDate.after(convertedDate1) && tDate.before(convertedDate2);
    }

    @Override
    public void onPause() {
        super.onPause();
        save(stateOption);
    }

    @Override
    public void onResume() {
        super.onResume();
        spinnerUnit.setSelection(load());
    }

    private void save(int isSelected) {
        int selectedPosition;
        SharedPreferences preferences = getActivity().getSharedPreferences("nbRepet", getContext().MODE_PRIVATE);
        value = preferences.getInt("nbRepet", 0);

        if(value==1){
            selectedPosition=   isSelected;
        }else{
            selectedPosition =isSelected-1;
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("spinnerUnitState",getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
      //  selectedPosition = isSelected;
        editor.putInt("spinnerUnitSelection", selectedPosition);
        editor.commit();
    }

    private int load() {


        SharedPreferences prefs = getActivity().getSharedPreferences("spinnerUnitState", getContext().MODE_PRIVATE);
        int iPos=prefs.getInt("spinnerUnitSelection",0);
        return iPos;
    }



}