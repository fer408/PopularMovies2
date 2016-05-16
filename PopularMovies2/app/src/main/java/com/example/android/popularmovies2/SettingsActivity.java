package com.example.android.popularmovies2;

import android.app.Fragment;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.example.android.popularmovies2.R;

public class SettingsActivity extends AppCompatActivity {
    //Code that connects to settings activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Code for when this hook is called whenever an item in your options menu is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return (super.onOptionsItemSelected(item));
    }


      //Code for a  simple {@link Fragment} subclass.

    public static class SettingsFragment extends PreferenceFragment {


        public SettingsFragment() {
            // Required empty public constructor
        }

      //Code that connects to my preference file in my xml folder
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }
    }
}