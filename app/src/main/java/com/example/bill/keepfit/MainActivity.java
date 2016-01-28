package com.example.bill.keepfit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button exitButton;
    private Button startButton;
    private SQLiteDatabase database;
    private static final String TABLE_NAME = "tbl_WG";
    private static final String DB_NAME = "Mydb.db";
    private ArrayAdapter<String> listAdapter ;
    Listadapter listad;
    private ListView mainListView;
    private View v1;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    ArrayList<String> goalList;
    private boolean is_enabled=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the toolbar
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);



        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
       // Toast.makeText(this, "The data base is opened successfully.", Toast.LENGTH_LONG).show();



        //here we initialize the listview to the list in the xml file
        mainListView = (ListView) findViewById( R.id.goal_list );
        registerForContextMenu(mainListView);
        mainListView.setChoiceMode(mainListView.CHOICE_MODE_SINGLE);

        //here the list of goals is appeared in the main screen
        display(v1);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int index;
        //!!!!!!!!!!!!!!!!!because it is an object you have to cast!!!!!!!!!!!!!!!
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId())
        {
            case R.id.edit_btn:
               index=info.position;
               Intent a = new Intent(MainActivity.this, EditActivity.class);
               a.putExtra("string", goalList.get(index).toString());
               startActivity(a);
                return true;
            case R.id.delete_btn:
                index=info.position;
                Intent b = new Intent(MainActivity.this, DeleteActivity.class);
                b.putExtra("string", goalList.get(index).toString());
                startActivity(b);
                return true;
            default:
                return super.onContextItemSelected(item);

        }
     //   return super.onContextItemSelected(item);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Physical back button click handler
    @Override
    public void onBackPressed() {
        //Beginning.this.finish();
        super.finish();

    }


    @Override
    public void onClick(View v) {
    }

    private void startPedometer() {
        Intent i = new Intent(MainActivity.this,PedometerActivity.class);
        startActivity(i);
    }


    public void display(View v) {

        goalList = new ArrayList<String>();
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

        Listadapter adapter = new Listadapter(this, goalList);
        mainListView.setAdapter(adapter);

        // Set the ArrayAdapter as the ListView's adapter.
       listAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview, goalList);
        mainListView.setAdapter( listAdapter );
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());

    }

}
