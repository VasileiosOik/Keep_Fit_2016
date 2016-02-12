package com.example.bill.keepfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Pedometer2Activity extends AppCompatActivity {

    private static SensorManager mManager;
    private static Sensor mStepCounter;
    private static long STEP_COUNT = 0;
    private EditText editText;
 //   private TextView mTvStep;
    private TextView tvchoicestep;
    private int helpInt;
    private String dataValue;
    private long stepsToStartAgain;
    private String helpName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer2);

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

        editText=(EditText) findViewById(R.id.editgoal);
        tvchoicestep=(TextView) findViewById(R.id.tv_choice_step);

        //shared preferences
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        float prefNameSteps = myPrefs.getFloat("MyData2", 0);
        stepsToStartAgain=(long) prefNameSteps;

      //  mTvStep = (TextView) findViewById(R.id.tv_step);

        Integer helpSteps=Integer.parseInt(editText.getText().toString());
        editText.setText("");

        tvchoicestep.setText(" / " +"GOAL : " + String.valueOf(helpInt));
     //   findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
       //     @Override
        //    public void onClick(View v) {
                System.out.println("Steps are: " + stepsToStartAgain);
                if(stepsToStartAgain==0) {
                    STEP_COUNT = 0;
                   // setStep(STEP_COUNT);
                }
                else
                {
                    if(stepsToStartAgain>=helpInt)
                    {
                        STEP_COUNT = 0;
                     //   setStep(STEP_COUNT);

                    }else{
                        STEP_COUNT = stepsToStartAgain;
                     //   setStep(STEP_COUNT);
                    }

                }
       //     }
       // });


    }



  //  private void setStep(long step) {
  //      mTvStep.setText("STEPS : " + String.valueOf(STEP_COUNT));
  //      tvchoicestep.setText(" / " +"GOAL : " + String.valueOf(helpInt));
 //   }

    // Physical back button click handler
    @Override
    public void onBackPressed() {
        //split the whole string to parts
        if (editText.getText().toString().trim().equals("")) {
            //nothing
            stepsToStartAgain=0;
            super.finish();
        }
        else {
            String data[] = editText.getText().toString().split(" ");
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
