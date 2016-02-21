package com.example.bill.keepfit;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private SQLiteDatabase database;
    private static final String DB_NAME = "Mydb.db";
    private Integer helpInt;
    private String helpName;
    private EditText et1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    }


    private void editDatabase() {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        Integer helpSteps=Integer.parseInt(et1.getText().toString());
        et1.setText("");

        //we want here to update the value with a new one given by the user

        database.execSQL("UPDATE tbl_WG SET steps='"+helpSteps+"' WHERE steps="+helpInt+"");
        database.close();
        //to return to previous screen
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.save:
                System.out.println(et1.getText().toString());
                if(et1.getText().toString().trim().equals("")){
                    Toast.makeText(EditActivity.this, "You have to enter a value",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    editDatabase();
                }

                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    @Override
    public void onClick(View v) {

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.check_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID save_button was selected
            case R.id.save_btn:
                System.out.println(et1.getText().toString());
                if(et1.getText().toString().trim().equals("")){
                    Toast.makeText(EditActivity.this, "You have to enter a value",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    editDatabase();
                }

                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }

        return true;
    }
    */
    @Override
    public void onBackPressed() {

        EditActivity.this.finish();
    }

}
