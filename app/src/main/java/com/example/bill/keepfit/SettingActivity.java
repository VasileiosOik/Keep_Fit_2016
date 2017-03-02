package com.example.bill.keepfit;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingActivity extends PreferenceActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

    }


}
