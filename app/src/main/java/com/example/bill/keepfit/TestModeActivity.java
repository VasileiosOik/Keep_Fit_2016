package com.example.bill.keepfit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class TestModeActivity extends AppCompatActivity {
    private EditText et;
    private Integer activeTestMode=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize the editText
        et = (EditText) findViewById(R.id.date);
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
                System.out.println(et.getText().toString());
                if(et.getText().toString().trim().equals("")){
                    //nothing happened!
                    Toast.makeText(TestModeActivity.this, "Enter a proper Date", Toast.LENGTH_LONG).show();
                }else{
                    storeTheDate();
                    finish();
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

    private void storeTheDate() {
        String currentDate;
        currentDate=et.getText().toString().trim();

        String data[] =currentDate.split("/");
        String year=data[data.length-1];
        String month=data[data.length-2];
        String day=data[data.length-3];
        activeTestMode=1;

        System.out.println(year + " " + month + " " +day);

    }
}
