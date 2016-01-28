package com.example.bill.keepfit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChooseActivity extends AppCompatActivity {
    private ListView mainListView;
    ArrayList<String> goalList;
    private SQLiteDatabase database;
    private static final String TABLE_NAME = "tbl_WG";
    private static final String DB_NAME = "Mydb.db";
    private ArrayAdapter<String> listAdapter ;
    View v1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        mainListView = (ListView) findViewById( R.id.goal_list1 );
        mainListView.setChoiceMode(mainListView.CHOICE_MODE_SINGLE);


        display(v1);


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
                Integer steps=Integer.parseInt(cursor.getString(1));

                goalList.add("Name: "+name+" || Steps: "+steps);


            } while (cursor.moveToNext());
        }
        cursor.close();
        //create an ArrayAdaptar from the String Array
        Listadapter adapter = new Listadapter(this, goalList);
        // Assign adapter to ListView
        mainListView.setAdapter(adapter);

        // Set the ArrayAdapter as the ListView's adapter.
    //    listAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview, goalList);
    //    mainListView.setAdapter( listAdapter );
    }
}
