package com.example.bill.keepfit;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private SQLiteDatabase database;
    private static final String TABLE_NAME = "tbl_WG";
    private static final String DB_NAME = "Mydb.db";
    private Integer helpInt;
    private String helpName;
    private EditText et1;
    private Object mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //retract the incoming intent
        Intent intent = getIntent();
        //recover the row that we want to edit
        String dataValue = intent.getExtras().getString("string");

        //split the whole string to parts
        String data[] =dataValue.split(" ");
        //store the int value that we want to edit
        helpInt=Integer.parseInt(data[data.length-1]);
        helpName=data[data.length-4];
        System.out.println(helpName);


        //initialize the editText
        et1 = (EditText) findViewById(R.id.editsteps);


      //  mActionMode=EditActivity.this.startActionMode((android.view.ActionMode.Callback) mActionModeCallback);

    }


    private void editDatabase() {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        Integer helpSteps=Integer.parseInt(et1.getText().toString());
        et1.setText("");

        //we want here to update the value with a new one given by the user

        database.execSQL("UPDATE tbl_WG SET steps='"+helpSteps+"' WHERE steps="+helpInt+"");

        //to return to previous screen
        finish();

    }



    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID save_button was selected
            case R.id.save_btn:
                editDatabase();
                //Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                //.show();
                break;
            default:
                break;
        }

        return true;
    }

    private ActionMode.Callback mActionModeCallback= new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater= mode.getMenuInflater();
            inflater.inflate(R.menu.my2_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };
}
