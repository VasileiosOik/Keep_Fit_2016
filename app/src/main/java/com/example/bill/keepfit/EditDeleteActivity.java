package com.example.bill.keepfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditDeleteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DB_NAME = "Mydb.db";
    private ListView mainListView;
    private View v1;
    private ArrayList<String> goalList;
    private String name;
    private String name1;
    private Boolean statusChecked;
    private boolean my_checkbox_preference = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editdelete);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //here we retrieve the state of the editable
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        my_checkbox_preference = mySharedPreferences.getBoolean("switchRef", false);
        System.out.println("H epilogh mou einai: " + my_checkbox_preference);

        //here we initialize the listview to the list in the xml file
        mainListView = (ListView) findViewById(R.id.goal_list);
        registerForContextMenu(mainListView);
        mainListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        //here the list of goals is appeared in the main screen
        display(v1);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (my_checkbox_preference) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_context_menu, menu);
        } else {
            System.out.println("Den ginetai tpt");
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int index;
        //!!!!!!!!!!!!!!!!!because it is an object you have to cast!!!!!!!!!!!!!!!
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();


        //it returns the names
        updateChoices(info, goalList);

        if (name == null && name1 == null) {
            switch (item.getItemId()) {
                case R.id.edit_btn:
                    //here i store the position of each of the items in the row
                    index = info.position;

                    Intent a = new Intent(EditDeleteActivity.this, EditActivity.class);
                    a.putExtra("string", goalList.get(index));
                    startActivity(a);
                    break;

                case R.id.delete_btn:
                    index = info.position;


                    Intent b = new Intent(EditDeleteActivity.this, DeleteActivity.class);
                    b.putExtra("string", goalList.get(index));
                    startActivity(b);


                    break;
                default:
                    return super.onContextItemSelected(item);

            }
        } else {
            if (name.equals(name1)) {
                switch (item.getItemId()) {
                    case R.id.edit_btn:
                        //here i store the position of each of the items in the row
                        index = info.position;
                        if (name.equals(name1) && statusChecked) {
                            Toast.makeText(EditDeleteActivity.this, "You cannot edit a current goal",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Intent a = new Intent(EditDeleteActivity.this, EditActivity.class);
                            a.putExtra("string", goalList.get(index));
                            startActivity(a);
                            return true;
                        }
                        break;
                    case R.id.delete_btn:
                        index = info.position;
                        if (name.equals(name1) && statusChecked) {
                            Toast.makeText(EditDeleteActivity.this, "You cannot delete a current goal",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Intent b = new Intent(EditDeleteActivity.this, DeleteActivity.class);
                            b.putExtra("string", goalList.get(index));
                            startActivity(b);
                            return true;
                        }
                        break;
                    default:
                        return super.onContextItemSelected(item);

                }
            } else {
                switch (item.getItemId()) {
                    case R.id.edit_btn:
                        //here i store the position of each of the items in the row
                        index = info.position;

                        Intent a = new Intent(EditDeleteActivity.this, EditActivity.class);
                        a.putExtra("string", goalList.get(index));
                        startActivity(a);
                        break;

                    case R.id.delete_btn:
                        index = info.position;


                        Intent b = new Intent(EditDeleteActivity.this, DeleteActivity.class);
                        b.putExtra("string", goalList.get(index));
                        startActivity(b);


                        break;
                    default:
                        return super.onContextItemSelected(item);

                }
            }
        }


        return super.onContextItemSelected(item);

    }

    //checks here if it is a currnet goal or not
    private void updateChoices(AdapterView.AdapterContextMenuInfo info, ArrayList<String> goalList) {
        int index;
        SharedPreferences sharedPreferences = EditDeleteActivity.this.getSharedPreferences("status", MODE_PRIVATE);
        statusChecked = sharedPreferences.getBoolean("MyGoal", false);
        String prefName = sharedPreferences.getString("MyGoal1", "0");
        index = info.position;
        String dataValue = prefName;
        if (!dataValue.equals("0")) {

            //split the whole string to parts
            String data[] = dataValue.split(" ");
            //store the int value that we want to edit
            name = data[data.length - 4];

            String dataValue1 = goalList.get(index);
            //split the whole string to parts
            String data1[] = dataValue1.split(" ");
            //store the int value that we want to edit
            name1 = data1[data1.length - 4];
            System.out.println("Here: " + name);
            System.out.println("Here: " + name1);

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }


    // Physical back button click handler
    @Override
    public void onBackPressed() {
        //this.finish();
        super.finish();

    }


    @Override
    public void onClick(View v) {
    }


    public void display(View v) {
        //The database is open!
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        SQLiteDatabase database = dbOpenHelper.openDataBase();
        goalList = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from tbl_WG", null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String unit = cursor.getString(cursor.getColumnIndex("unit"));
                Integer steps = Integer.parseInt(cursor.getString(1));
                goalList.add("Name: " + name + " || " + unit + ": " + steps);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        ListAdapter adapter = new ListAdapter(this, goalList);
        mainListView.setAdapter(adapter);

    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());

    }
}
