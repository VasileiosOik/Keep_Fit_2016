package com.example.bill.keepfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SQLiteDatabase database;
    private static final String TABLE_NAME = "tbl_WG";
    private static final String DB_NAME = "Mydb.db";
    private ArrayAdapter<String> listAdapter;
    private ListView mainListView;
    private View v1;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private int mBackIndex;
    private int mSeries1Index;
    private DecoView mDecoView;
    private final float mSeriesMax = 50f;
    private float steps_count = 0f;
    private float Steps_so_far=0;
    private String prefName;
    private float prefNameSteps;
    private String goalName;
    private ArrayList<String>  goalList1;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //create basic table
        createTableRow();

        //create table history
        createTableHistory();

        //create help history table
        createHelpTableHistory();

        //for animation
        preferences();

        //animated bar grey one
        mDecoView = (DecoView) findViewById(R.id.dynamicArcView);


        // Create required data series on the DecoView
        createBackSeries();
        createDataSeries1();

        // Setup events to be fired on a schedule
        createEvents();

        //initialize a custom floating button
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.ic_add_white_24dp);
        //creating a button
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();
        actionButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

        //initialize the 3 options of the floating button
        ImageView iconSortCreate = new ImageView(this);
        iconSortCreate.setImageResource(R.drawable.create);

        ImageView iconSortGoal = new ImageView(this);
        iconSortGoal.setImageResource(R.drawable.edit);

        ImageView iconSortStart = new ImageView(this);
        iconSortStart.setImageResource(R.drawable.start);

        //build the button
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        SubActionButton button1 = itemBuilder.setContentView(iconSortCreate).build();
        SubActionButton button2 = itemBuilder.setContentView(iconSortGoal).build();
        SubActionButton button3 = itemBuilder.setContentView(iconSortStart).build();

        //add the 3 buttons
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

                Intent i = new Intent(StartActivity.this, GoalActivity.class);
                startActivity(i);

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(StartActivity.this, EditDeleteActivity.class);
                startActivity(i);

            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(StartActivity.this, ChooseActivity.class);
                startActivity(i);
                //   startActivityForResult(i, 1);

            }
        });

        //navigator bar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(StartActivity.this);
        //request focus to each item
        navigationView.bringToFront();
        drawer.requestLayout();


        //here we initialize the listview to the list in the xml file
     //  mainListView = (ListView) findViewById( R.id.goal_list );
     //   registerForContextMenu(mainListView);
     //   mainListView.setChoiceMode(mainListView.CHOICE_MODE_SINGLE);

        //here the list of goals is appeared in the main screen
     //   display(v1);

    }

    private void createTableRow() {
        //create the table
        db = openOrCreateDatabase("Mydb.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.setVersion(1);
        db.setLocale(Locale.getDefault());

        //create new table if not already exist 1st way
        final String CREATE_TABLE_WalkingGoals =
                "CREATE TABLE IF NOT EXISTS tbl_WG ("
                        + "name VARCHAR PRIMARY KEY ,"
                        + "steps INTEGER, "
                        + "percentage FLOAT);";
        db.execSQL(CREATE_TABLE_WalkingGoals);
     //   System.out.println("Table has created successfully!");

        //delete the table
        // db.execSQL("DROP TABLE IF EXISTS tbl_WG");
        //  System.out.println("Table dropped!");

        //create the table 2nd way
        // db.execSQL("create table if not exists tbl_WG(name varchar primary key, steps int, percentage float)");
        //  System.out.println("Table has created successfully!");
    }

    private void createBackSeries() {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        mBackIndex = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries1() {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
               // System.out.println("here: " + percentFilled);
                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        final TextView textActivity1 = (TextView) findViewById(R.id.textActivity1);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textActivity1.setText("Steps walked so far: " +String.valueOf(Steps_so_far));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries1Index = mDecoView.addSeries(seriesItem);
    }

    private void createEvents() {
        mDecoView.executeReset();

        mDecoView.addEvent(new DecoEvent.Builder(mSeriesMax)
                .setIndex(mBackIndex)
                .setDuration(3000)
                .setDelay(100)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(mSeries1Index)
                .setDuration(2000)
                .setDelay(1250)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(steps_count * 100)
                .setIndex(mSeries1Index)
                .setDelay(3250)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(0)
                .setIndex(mSeries1Index)
                .setDelay(20000)
                .setDuration(1000)
                .setInterpolator(new AnticipateInterpolator())
                .setListener(new DecoEvent.ExecuteEventListener() {
                    @Override
                    public void onEventStart(DecoEvent decoEvent) {

                    }

                    @Override
                    public void onEventEnd(DecoEvent decoEvent) {
                           resetText();

                    }
                })
                .build());

        /*
        if((steps_count*100f)==mSeriesMax) {
            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
                    .setIndex(mSeries1Index)
                    .setDelay(21000)
                    .setDuration(3000)
                    .setDisplayText("GOAL!")
                    .setListener(new DecoEvent.ExecuteEventListener() {
                        @Override
                        public void onEventStart(DecoEvent decoEvent) {

                        }

                        @Override
                        public void onEventEnd(DecoEvent decoEvent) {
                            //  createEvents();
                        }
                    })
                    .build());
        }
        */
        resetText();
    }

    private void resetText() {
        ((TextView) findViewById(R.id.textActivity1)).setText("");
        ((TextView) findViewById(R.id.textPercentage)).setText("");
        ((TextView) findViewById(R.id.textRemaining)).setText("");
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
            }

            mBackPressed = System.currentTimeMillis();

        }

    }


    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());


    }

    public void preferences()
    {
        //check if the app starts for the first time
        SharedPreferences runCheck; //load the preferences
        runCheck = getSharedPreferences("hasRunBefore", 0);
        Boolean hasRun = runCheck.getBoolean("hasRun", false); //see if it's run before, default no
        if (!hasRun) {
            SharedPreferences settings = getSharedPreferences("hasRunBefore", 0);
            SharedPreferences.Editor edit = settings.edit();
            edit.putBoolean("hasRun", true); //set to has run
            edit.commit(); //apply
            //code for if this is the first time the app has run
            steps_count=0f;
        }
        else {
            //code if the app HAS run before
            // shared preferences
            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
            prefName = myPrefs.getString("MyData", "0");
            prefNameSteps = myPrefs.getFloat("MyData2", 0);
            goalName=myPrefs.getString("MyData3", "0");
            if (prefName != null) {
                steps_count = (Float.parseFloat(prefName)) / 2;
                Steps_so_far=prefNameSteps;
                System.out.println("The percentage: " + steps_count);
                System.out.println("The steps have walked so far: " + Steps_so_far);
              //  saveDatabase();
            }

        }
    }

    public void createTableHistory(){
        //create the table
        database = openOrCreateDatabase("MyHelpdb.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        database.setVersion(1);
        database.setLocale(Locale.getDefault());

        //create new table if not already exist 1st way
        final String CREATE_TABLE_TIME_Goals =
                "CREATE TABLE IF NOT EXISTS time_tbl_WG ("
                        + "name VARCHAR PRIMARY KEY ,"
                        + "allsteps INTEGER, "
                        + "didsteps INTEGER, "
                        + "percentage FLOAT, "
                        + "active INTEGER,"
                        + "date STRING);";
        database.execSQL(CREATE_TABLE_TIME_Goals);
     //   System.out.println("Table2 has created successfully!");
    }

    public void createHelpTableHistory(){
        //create the table
        database = openOrCreateDatabase("MyHistorydb.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        database.setVersion(1);
        database.setLocale(Locale.getDefault());

        //create new table if not already exist 1st way
        final String CREATE_TABLE_HISTORY_Goals =
                "CREATE TABLE IF NOT EXISTS history_tbl_WG ("
                        + "name VARCHAR  ,"
                        + "allsteps INTEGER, "
                        + "didsteps INTEGER, "
                        + "percentage FLOAT, "
                        + "active INTEGER,"
                        + "date STRING);";
        database.execSQL(CREATE_TABLE_HISTORY_Goals);
        //   System.out.println("Table2 has created successfully!");
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            // Handle the camera action
            Intent i = new Intent(StartActivity.this, HistoryActivity.class);
            startActivity(i);

        }
        /*else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //no inspection Simplifiable If Statement
        if (id == R.id.action_testMode) {
            System.out.println("Test mode!");
            /*
            ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
            database = dbOpenHelper.openDataBase();
            //clear the history
            database.execSQL("delete from "+ TABLE_NAME);
            database.close();
            //reload the activity instantly
            Intent intent = getIntent();
            overridePendingTransition(0, 0);
            //no animation
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            overridePendingTransition(0, 0);
            //finish the activity and restart it
            finish();
            startActivity(intent);

            */
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

}