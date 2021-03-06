package com.example.bill.keepfit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;


public class ListAdapter2 extends ArrayAdapter implements CompoundButton.OnCheckedChangeListener {
    private ArrayList<String> goalList = null;
    private Context context;
    private Boolean[] checkedStatus;


    public ListAdapter2(Context context, ArrayList<String> resource, Boolean[] checkedStatus) {
        super(context, R.layout.toggle_button_row, resource);
        this.context = context;
        goalList = new ArrayList<>();
        this.goalList = resource;
        this.checkedStatus = checkedStatus;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.toggle_button_row, parent, false);
        View v1 = convertView;
        TextView name = (TextView) v1.findViewById(R.id.textview1);
        name.setText(goalList.get(position));
        //switches
        Switch sw = (Switch) v1.findViewById(R.id.switch1);
        sw.setTag(position);
        sw.setOnCheckedChangeListener(ListAdapter2.this);
        sw.setChecked(checkedStatus[position]);

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
        editor.putString("MyGoal1", goalList.get(index).trim());
        editor.apply();
    }


    @Override
    public Object getItem(int position) {
        return position;
    }


    public int getCount() {
        return goalList.size();
    }

}
