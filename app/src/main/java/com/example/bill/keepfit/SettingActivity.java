package com.example.bill.keepfit;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import java.util.prefs.Preferences;

public class SettingActivity  extends PreferenceActivity {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
      //  getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
      //  PreferenceManager.setDefaultValues(SettingActivity.this, R.xml.preferences, false);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

    }


}
