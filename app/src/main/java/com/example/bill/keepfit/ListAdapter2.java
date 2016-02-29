package com.example.bill.keepfit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Bill on 07/02/2016.
 */
public class ListAdapter2 extends ArrayAdapter implements CompoundButton.OnCheckedChangeListener{//, View.OnClickListener{
    private  ArrayList<String> goalList= null;
    private Context context;
    private View v1;
    private SharedPreferences.Editor editor;
    private ViewHolder holder;
    private TextView name;
    private Switch sw;
    Boolean[] checkedStatus;
    private int mFieldId = 0;




    public ListAdapter2(Context context,ArrayList<String> resource, Boolean[] checkedStatus) {
        super(context,R.layout.toggle_button_row,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        goalList=new ArrayList<String>();
        this.goalList =resource;
        this.checkedStatus=checkedStatus;
      //  mFieldId=textViewResourceId;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.toggle_button_row, parent, false);
        v1=convertView;
        TextView name = (TextView) v1.findViewById(R.id.textview1);
        name.setText(goalList.get(position).toString());
        sw= (Switch) v1.findViewById(R.id.switch1);
        sw.setTag(position);
        sw.setOnCheckedChangeListener(ListAdapter2.this);
        sw.setChecked(checkedStatus[position]);
        //new 
       // sw.setOnCheckedChangeListener(null);
       // sw.setChecked(checkedStatus[position]);
      //  sw.setOnCheckedChangeListener(ListAdapter2.this);


        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


        Integer index = (Integer) buttonView.getTag();
        checkedStatus[index] = isChecked;
        notifyDataSetChanged();
        String key = index.toString();
        //save the data for the status
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.putBoolean("MyGoal", isChecked);
        editor.putString("MyGoal1", goalList.get(index).toString().trim());
        editor.apply();

    }

/*
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        // First turn off the switch of any other item that may be on
      //  Arrays.fill(checkedStatus, false);

        Integer index = (Integer) buttonView.getTag();
        checkedStatus[index] = isChecked;
        notifyDataSetChanged();  // don't forget to tell ListView that data is updated



        String key = index.toString();

        //save the data for the status
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isChecked) {
            editor.putInt("last_index", index);
          //  editor.putBoolean("MyGoal", isChecked);
        } else {
            editor.remove("last_index");// nothing's checked, so erase pref
          //  editor.remove("MyGoal");
        }
        editor.putBoolean(key, isChecked);
        editor.putBoolean("MyGoal", isChecked);
        editor.putString("MyGoal1", goalList.get(index).toString().trim());
        editor.apply();
    }
*/
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }
/*
    @Override
    public void onClick(View v) {
        for(int i=0;i<goalList.size(); i++) {
            if (sw.isChecked()) {
                checkedStatus[i] = true;
              //  selected_position = pos;
            } else {
                checkedStatus[i] = false;
            }

        }
    }
*/

    public int getCount() {
        // TODO Auto-generated method stub
        return goalList.size();
    }

    public class ViewHolder{
        //put all of your textviews and image views and
        //all views here like this
        TextView textName;
        Switch switchButton;
    }


}
