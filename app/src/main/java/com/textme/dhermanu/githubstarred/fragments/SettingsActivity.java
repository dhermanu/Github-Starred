package com.textme.dhermanu.githubstarred.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.textme.dhermanu.githubstarred.R;

/**
 * Created by dhermanu on 6/16/16.
 */
public class SettingsActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new MyPreferenceFragment())
                .commit();
    }


    public static class MyPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            bindPreferenceSummary(findPreference("key"));
            bindPreferenceSummary(findPreference("order"));
        }
          /*attach a listener so the summary value is always updated with prefernce value*/

        public void bindPreferenceSummary(Preference preference){
            //set the listener for value change
            preference.setOnPreferenceChangeListener(this);
            onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences
                    (preference.getContext()).getString(preference.getKey(), ""));
//            preference.setOnPreferenceChangeListener(this);
//
//            onPreferenceChange(preference,
//                    PreferenceManager
//                            .getDefaultSharedPreferences(preference.getContext())
//                            .getString(preference.getKey(), ""));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String stringValue = o.toString();

            if(preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if(prefIndex >= 0 ){
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }

            else{
                preference.setSummary(stringValue);
            }

            return true;
//            if(preference instanceof ListPreference){
//                // For list preferences, look up the correct display value in
//                // the preference's 'entries' list (since they have separate labels/values).
//                ListPreference listPreference = (ListPreference) preference;
//                int prefIndex = listPreference.findIndexOfValue(stringValue);
//                if (prefIndex >= 0) {
//                    preference.setSummary(listPreference.getEntries()[prefIndex]);
//                }
//            }
//
//            else {
//                // For other preferences, set the summary to the value's simple string representation.
//                preference.setSummary(stringValue);
//            }
//            return true;
        }

    }
}

