package com.example.bill.keepfit;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.widget.TextView;

public class Pedometer2Activity extends AppCompatActivity  {

    private long timestamp;

    private TextView textViewStepCounter;

    private TextView textViewStepDetector;

    private Thread detectorTimeStampUpdaterThread;

    private Handler handler;

    private boolean isRunning = true;
    private int mLastCount, mInitialCount;
    private boolean mInitialCountInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer2);

        textViewStepCounter = (TextView) findViewById(R.id.textView2);
        textViewStepDetector = (TextView) findViewById(R.id.textView4);

        registerForSensorEvents();

        setupDetectorTimestampUpdaterThread();
    }

    public void registerForSensorEvents() {
        SensorManager sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Step Counter
        sManager.registerListener(new SensorEventListener() {

                                      @Override
                                      public void onSensorChanged(SensorEvent event) {

                                          final int type = event.sensor.getType();
                                          if (type == Sensor.TYPE_STEP_COUNTER){
                                              if (!mInitialCountInitialized) {
                                                  mInitialCount = (int) event.values[0];
                                                  mInitialCountInitialized = true;
                                              }

                                          }
                                          mLastCount=(int)event.values[0]-mInitialCount;

                                          float steps = mLastCount;
                                          textViewStepCounter.setText((int) steps + "");
                                      }

                                      @Override
                                      public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                      }
                                  }, sManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_UI);

        // Step Detector
        sManager.registerListener(new SensorEventListener() {

                                      @Override
                                      public void onSensorChanged(SensorEvent event) {




                                          // Time is in nanoseconds, convert to millis
                                          timestamp = event.timestamp / 1000000;
                                      }

                                      @Override
                                      public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                      }
                                  }, sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_UI);
    }

    private void setupDetectorTimestampUpdaterThread() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                textViewStepDetector.setText(DateUtils
                        .getRelativeTimeSpanString(timestamp));
            }
        };

        detectorTimeStampUpdaterThread = new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(5000);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        detectorTimeStampUpdaterThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        detectorTimeStampUpdaterThread.interrupt();
    }



}
