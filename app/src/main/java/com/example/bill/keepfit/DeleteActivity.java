package com.example.bill.keepfit;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeleteActivity extends AppCompatActivity implements View.OnClickListener{
    private SQLiteDatabase database;
    private static final String TABLE_NAME = "tbl_WG";
    private static final String DB_NAME = "Mydb.db";
    private Integer helpInt;
    private String helpName;
    private EditText et1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        //retract the incoming intent
        Intent intent = getIntent();
        //recover the row that we want to edit
        String dataValue = intent.getExtras().getString("string");

        //split the whole string to parts
        String data[] =dataValue.split(" ");
        //store the int value that we want to delete
        helpInt=Integer.parseInt(data[data.length-1]);
        helpName=data[data.length-4];
        System.out.println(helpName);



    }

    @Override
    public void onClick(View v) {

    }

    private void deleteDatabase() {

        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        database.delete(TABLE_NAME, "name" + "='" + helpName + "'", null);

        database.close();
        Toast.makeText(this, "goal deleted successfully!", Toast.LENGTH_LONG).show();
        finish();
    }


    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")


                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteDatabase();
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

    @Override
    public void onRestart() {
        super.onRestart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my2_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID save_button was selected
            case R.id.delete_btn:
                AlertDialog diaBox = AskOption();
                diaBox.show();
                //Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                //.show();
                break;
            default:
                break;
        }

        return true;
    }
}
