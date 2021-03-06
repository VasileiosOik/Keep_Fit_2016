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
import android.widget.TextView;
import android.widget.Toast;

public class DeleteActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TABLE_NAME = "tbl_WG";
    private static final String DB_NAME = "Mydb.db";
    private String helpName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        //back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView tv = (TextView) findViewById(R.id.tv);
        //retract the incoming intent
        Intent intent = getIntent();
        //recover the row that we want to edit
        String dataValue = intent.getExtras().getString("string");

        //split the whole string to parts
        if (dataValue != null) {
            String data[] = dataValue.split(" ");
            helpName = data[data.length - 4];
            System.out.println(helpName);
            tv.setText(dataValue);
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void deleteDatabase() {

        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        SQLiteDatabase database = dbOpenHelper.openDataBase();

        database.delete(TABLE_NAME, "name" + "='" + helpName + "'", null);
        database.close();
        Toast.makeText(this, "goal deleted successfully!", Toast.LENGTH_LONG).show();
        finish();
    }


    private AlertDialog AskOption() {
        return new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete Action")
                .setMessage("Do you want to delete the Goal?")


                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //my deleting code
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

    }

    @Override
    public void onRestart() {
        super.onRestart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.delete_btn:
                AlertDialog diaBox = AskOption();
                diaBox.show();
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
    public void onBackPressed() {
        DeleteActivity.this.finish();
    }
}
