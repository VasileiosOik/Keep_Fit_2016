package com.example.bill.keepfit;

/**
 * Created by Bill on 22/01/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Listadapter extends ArrayAdapter implements View.OnClickListener{
        private  ArrayList<String> goalList= null;
        private  Context context;
        private TextView name;
        private Integer selected_position = -1;
        private SQLiteDatabase database;
        private static final String TABLE_NAME = "tbl_WG";
        private static final String DB_NAME = "Mydb.db";
        View v1;
        CheckBox chk1;




public Listadapter(Context context, ArrayList<String> resource) {
        super(context,R.layout.row,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        goalList=new ArrayList<String>();
        this.goalList =resource;
}

@Override
public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        /*
        if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row, null);
        }
        */
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);
        v1=convertView;
        chk1 = (CheckBox)convertView.findViewById(R.id.chk_box);
        TextView name = (TextView) convertView.findViewById(R.id.textview1);
        name.setText(goalList.get(position).toString());

        return convertView;
        }




        @Override
        public Object getItem(int position) {
                // TODO Auto-generated method stub
                return position;
        }

        @Override
        public void onClick(View v) {


        }
}