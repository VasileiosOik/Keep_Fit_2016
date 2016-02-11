package com.example.bill.keepfit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PedometerActivity extends AppCompatActivity implements SensorEventListener {


    private static SensorManager mManager;
    private static Sensor mStepCounter;
    private static long STEP_COUNT = 0;
    private TextView mTvStep;
    private TextView tvchoicestep;
    private int helpInt;
    private String dataValue;
    private long stepsToStartAgain;
    private String helpName;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        //retract the incoming intent
        Intent intent = getIntent();
        //recover the row that we want to edit
        dataValue = intent.getExtras().getString("string");
        //split the whole string to parts
        String data[] =dataValue.split(" ");
        //store the int value that we want to edit
        helpInt=Integer.parseInt(data[data.length-1].replace("]",""));//the total steps of one specific goal
        //store the name of the goal that was chosen
        helpName=data[data.length-4];

        mManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // After it turns off and the total count
//        mStepCounter = mManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        // Ascend from the moment you start by 1
        mStepCounter = mManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        tvchoicestep=(TextView) findViewById(R.id.tv_choice_step);

        //shared preferences
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        float prefNameSteps = myPrefs.getFloat("MyData2", 0);
        stepsToStartAgain=(long) prefNameSteps;

        mTvStep = (TextView) findViewById(R.id.tv_step);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Steps are: " + stepsToStartAgain);
                if(stepsToStartAgain==0) {
                    STEP_COUNT = 0;
                    setStep(STEP_COUNT);
                }
                else
                {
                    if(stepsToStartAgain>=helpInt)
                    {
                        STEP_COUNT = 0;
                        setStep(STEP_COUNT);

                    }else{
                        STEP_COUNT = stepsToStartAgain;
                        setStep(STEP_COUNT);
                    }

                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(" ", sensor.getName() + " sensor is activated. the Accuracy is " + accuracy);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(STEP_COUNT==helpInt){
            onBackPressed();
        }else {
            //print step by step informations e.x time and number of steps
            StringBuilder sb = new StringBuilder();
            sb.append("========================================\n");
            sb.append("Sensor name : ").append(event.sensor.getName()).append("\n");
            sb.append("TimeStamp : ").append(event.timestamp).append("\n");
            for (float v : event.values) {
                sb.append(" value >>> ").append(v).append("\n");
                STEP_COUNT = STEP_COUNT + ((long) v);
            }
            sb.append("Cumulative footsteps : ").append(STEP_COUNT).append("\n");
            Log.d("Number: ", sb.toString());
            setStep(STEP_COUNT);
        }
    }

    private void setStep(long step) {
        mTvStep.setText("STEPS : " + String.valueOf(STEP_COUNT));
        tvchoicestep.setText(" / " +"GOAL : " + String.valueOf(helpInt));
    }

    // Physical back button click handler
    @Override
    public void onBackPressed() {
        //split the whole string to parts
        if (mTvStep.getText().toString().trim().equals("")) {
                //nothing
            stepsToStartAgain=0;
            super.finish();
        }
        else {
            String data[] = mTvStep.getText().toString().split(" ");
            String dataa[] = tvchoicestep.getText().toString().split(" ");
            //store the int value that we want to edit
            float st = Float.parseFloat(data[data.length - 1]);
            float st1 = Float.parseFloat(dataa[dataa.length - 1]);
            //percentage of the current steps/total steps
            String data1 = String.valueOf((st / st1));
            //use of shared preferences
            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            prefsEditor.putString("MyData", data1);//percentage
            prefsEditor.putFloat("MyData2", st);//current steps
            prefsEditor.putString("MyData3", helpName);//name of the current goal
            prefsEditor.commit();
            super.finish();
        }
    }


}
