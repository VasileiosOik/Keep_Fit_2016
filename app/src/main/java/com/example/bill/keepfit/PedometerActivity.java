package com.example.bill.keepfit;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class PedometerActivity extends AppCompatActivity implements SensorEventListener {
    private TextView textView;
    private TextView textView1;

    private SensorManager mSensorManager;

    private Sensor mStepCounterSensor;

    private Sensor mStepDetectorSensor;

    boolean activityWalking;

    private int mLastCount, mInitialCount;
    private boolean mInitialCountInitialized;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        textView =(TextView)findViewById(R.id.textview);
        textView1 =(TextView)findViewById(R.id.textview1);



        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);


    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        //my code
        int value=-1;
        final int type = event.sensor.getType();
        if (type == Sensor.TYPE_STEP_COUNTER){
            if (!mInitialCountInitialized) {
                mInitialCount = (int) event.values[0];
                mInitialCountInitialized = true;
            }

        }
        mLastCount=(int)event.values[0]-mInitialCount;
        value=mLastCount;
       // float[] values = event.values;
      //  int value = -1;

     //   if (values.length > 0) {
     //       value = (int) values[0];
     //   }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            textView1.setText("Step Counter Detected : " + value);

        }

        if(activityWalking) {
          //  textView1.setText(String.valueOf(event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {

        super.onResume();

        mSensorManager.registerListener(this, mStepCounterSensor,

                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor,

                SensorManager.SENSOR_DELAY_FASTEST);

        //activityWalking = true;
     //   mInitialCountInitialized=true;
        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null) {
            mSensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }


    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
       // activityWalking=false;
        mInitialCountInitialized=false;
    }

}
