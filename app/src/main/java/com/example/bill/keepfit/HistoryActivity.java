package com.example.bill.keepfit;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    private static final String TABLE_NAME = "tbl_WG";
    private static final String DB_NAME = "Mydb.db";
    private ArrayAdapter<String> listAdapter ;
    private ListView mainListView;
    private ArrayList<String> goalList;
    private View v1;
    private String prefName;
    private float prefNameSteps;
    private String goalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        prefName = myPrefs.getString("MyData", "0");
        prefNameSteps = myPrefs.getFloat("MyData2", 0);
        goalName=myPrefs.getString("MyData3", "0");


        //here we initialize the listview to the list in the xml file
        mainListView = (ListView) findViewById( R.id.goal_list );
       // mainListView.setChoiceMode(mainListView.CHOICE_MODE_SINGLE);
        //insert the percentage
        saveDatabase();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
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

    private void saveDatabase() {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();


        int percentage;
        //we want here to update the value with a new one given by the user
        try {
             percentage=(Integer.parseInt(prefName)*100);
        }
        catch(NumberFormatException ex) {
            percentage = 0; // default ??
        }
      //  int percentage=(Integer.parseInt(prefName));
        database.execSQL("UPDATE tbl_WG SET percentage='"+percentage+"' WHERE name='"+goalName+"'");

        //to return to previous screen
       // finish();

    }

    public void display1(View v) {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        // Toast.makeText(this, "The data base is opened successfully.", Toast.LENGTH_LONG).show();

        goalList = new ArrayList<String>();
        Cursor cursor = database.rawQuery("select * from tbl_WG", null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            do {
                String name=cursor.getString(cursor.getColumnIndex("name"));
                Integer steps=Integer.parseInt(cursor.getString(1));
                int percentage=cursor.getInt(cursor.getColumnIndex("percentage"));

                goalList.add("Name: "+name+" || Steps: "+steps+" || Percentage: "+percentage+"%");


            } while (cursor.moveToNext());
        }
        cursor.close();

        ListAdapter adapter = new ListAdapter(this, goalList);
        mainListView.setAdapter(adapter);


    }

}
