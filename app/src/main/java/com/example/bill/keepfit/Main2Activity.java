package com.example.bill.keepfit;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    private Button exitButton;
    private Button startButton;
    private SQLiteDatabase database;
    private static final String TABLE_NAME = "tbl_WG";
    private static final String DB_NAME = "Mydb.db";
    private ArrayAdapter<String> listAdapter ;
    private ListView mainListView;
    private View v1;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    SeriesItem seriesItem;
    /**
     * DecoView animated arc based chart
     */
    private DecoView mDecoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //animated bar
        DecoView decoView = (DecoView) findViewById(R.id.dynamicArcView);

         seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, 50, 0)
                .build();

        int backIndex = decoView.addSeries(seriesItem);

        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        decoView.addEvent(new DecoEvent.Builder(50)
                .setIndex(backIndex)
                .build());

        int series1Index = decoView.addSeries(seriesItem);

        //creating a custom floating button
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.ic_add_white_24dp);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();
        actionButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));


        ImageView iconSortCreate= new ImageView(this);
        iconSortCreate.setImageResource(R.drawable.newg);

        ImageView iconSortGoal= new ImageView(this);
        iconSortGoal.setImageResource(R.drawable.edit);

        ImageView iconSortStart= new ImageView(this);
        iconSortStart.setImageResource(R.drawable.ic_touch_app_black_24dp);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        SubActionButton button1 = itemBuilder.setContentView(iconSortCreate).build();
        SubActionButton button2 = itemBuilder.setContentView(iconSortGoal).build();
        SubActionButton button3 = itemBuilder.setContentView(iconSortStart).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .attachTo(actionButton)
                .build();

        //buttons events handler
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Main2Activity.this,GoalActivity.class);
                startActivity(i);

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Main2Activity.this,MainActivity.class);
                startActivity(i);

            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Main2Activity.this,PedometerActivity.class);
                startActivity(i);

            }
        });

    }

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else { Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }



    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}