package com.example.bill.keepfit;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private SQLiteDatabase database;
    private static final String DB_NAME = "Mydb.db";
    private String helpName;
    private EditText et1;
    private EditText et2;
    private String nameCh = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //retract the incoming intent
        Intent intent = getIntent();
        //recover the row that we want to edit
        String dataValue = intent.getExtras().getString("string");

        //split the whole string to parts
        String data[] = dataValue != null ? dataValue.split(" ") : new String[0];
        //store the int value that we want to edit
        Integer helpInt = Integer.parseInt(data[data.length - 1]);
        helpName = data[data.length - 4];


        //initialize the editText
        et1 = (EditText) findViewById(R.id.editName);
        et1.setText(helpName);
        et2 = (EditText) findViewById(R.id.editsteps);
        et2.setText(String.valueOf(helpInt));
        TextView tv = (TextView) findViewById(R.id.Number_of_steps);
        tv.setText("New Number of " + unitReturn());

    }


    private void editGoal() {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        //Give new name
        String name = et1.getText().toString();
        //Give new number of steps
        Integer helpSteps = Integer.parseInt(et2.getText().toString());
        et1.setText("");
        et2.setText("");

        //we want here to update the values with new ones given by the user

        database.execSQL("UPDATE tbl_WG SET steps='" + helpSteps + "' WHERE name='" + helpName + "'");
        if (helpName.equals(name)) {
            //dont do anything
            System.out.println("Name is the same");
        } else {
            database.execSQL("UPDATE tbl_WG SET name='" + name + "' WHERE name='" + helpName + "'");
        }

        Toast.makeText(this, "Changes applied successfully!", Toast.LENGTH_LONG).show();
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
                String regex = "[0-9]+";
                if (et1.getText().toString().trim().equals("") || et2.getText().toString().trim().equals("")) {
                    Toast.makeText(EditActivity.this, "You have to specify a value",
                            Toast.LENGTH_LONG).show();
                } else if (et1.getText().toString().trim().equals("0")) {
                    Toast.makeText(EditActivity.this, "Wrong name",
                            Toast.LENGTH_LONG).show();
                } else if ((Integer.parseInt(et2.getText().toString().trim())) <= 0) {
                    Toast.makeText(EditActivity.this, "Wrong number of steps",
                            Toast.LENGTH_LONG).show();
                } else if (et1.getText().toString().trim().contains("~") || et1.getText().toString().trim().contains("@")
                        || et1.getText().toString().trim().contains("#") || et1.getText().toString().trim().contains("$")
                        || et1.getText().toString().trim().contains("%") || et1.getText().toString().trim().contains("^")
                        || et1.getText().toString().trim().contains("&") || et1.getText().toString().trim().contains("*")
                        || et1.getText().toString().trim().contains("(") || et1.getText().toString().trim().contains(")")
                        || et1.getText().toString().trim().contains("[") || et1.getText().toString().trim().contains("]")
                        || et1.getText().toString().trim().contains("{") || et1.getText().toString().trim().contains("}")
                        || et1.getText().toString().trim().contains(";") || et1.getText().toString().trim().contains(":")
                        || et1.getText().toString().trim().contains("'") || et1.getText().toString().trim().contains("|")
                        || et1.getText().toString().trim().contains(",") || et1.getText().toString().trim().contains("<")
                        || et1.getText().toString().trim().contains(">") || et1.getText().toString().trim().contains(".")
                        || et1.getText().toString().trim().contains("?") || et1.getText().toString().trim().contains("/")
                        || et1.getText().toString().trim().contains("-") || et1.getText().toString().trim().contains("_")
                        || et1.getText().toString().trim().contains("+") || et1.getText().toString().trim().contains("=")) {
                    Toast.makeText(EditActivity.this, "Name cannot contain special characters",
                            Toast.LENGTH_LONG).show();
                } else if (nameChecked(et1.getText().toString().trim()) != null && !nameChecked(et1.getText().toString().trim()).isEmpty() && !nameChecked(et1.getText().toString().trim()).equals(helpName)) {
                    Toast.makeText(EditActivity.this, "Name already exists", Toast.LENGTH_LONG).show();
                } else if (helpName.equals(et1.getText().toString().trim())) {
                    System.out.println("Bainw edw");
                    editGoal();
                } else if (et1.getText().toString().trim().matches(regex)) {
                    Toast.makeText(EditActivity.this, "Name cannot contain only numbers",
                            Toast.LENGTH_LONG).show();
                } else {
                    editGoal();
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


    @Override
    public void onBackPressed() {

        EditActivity.this.finish();
    }

    public String nameChecked(String name) {
        //The database is open!
        boolean isFound = false;
        System.out.println("To name pou pernaw einai: " + name);
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        Cursor cursor = database.rawQuery("select name from tbl_WG", null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                nameCh = cursor.getString(cursor.getColumnIndex("name"));

                if (nameCh.equals(name)) {
                    System.out.println("to vrika sth vasi");
                    isFound = true;
                    break;
                }


            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        if (isFound) {
            System.out.println("To name pou epistrefw einai: " + nameCh);
            return nameCh;
        } else {
            nameCh = "";
            System.out.println("To name pou epistrefw einai: " + nameCh);
            return nameCh;
        }

    }

    public String unitReturn() {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, "Mydb.db");
        database = dbOpenHelper.openDataBase();

        //put cursor
        Cursor cursor = database.rawQuery("select unit from tbl_WG WHERE name='" + helpName + "'", null);
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

}
