package com.example.bill.keepfit;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter implements View.OnClickListener{
        private  ArrayList<String> goalList= null;
        private  Context context;


        public ListAdapter(Context context, ArrayList<String> resource) {
        super(context,R.layout.row,resource);
        this.context = context;
        goalList=new ArrayList<>();
        this.goalList =resource;
}
        @NonNull
@Override
public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);
        View v1 = convertView;
        TextView name = (TextView) convertView.findViewById(R.id.textview1);
        name.setText(goalList.get(position));

        return v1;
        }


        @Override
        public Object getItem(int position) {
                return position;
        }

        @Override
        public void onClick(View v) {


        }

        public int getCount() {
                return goalList.size();
        }
}