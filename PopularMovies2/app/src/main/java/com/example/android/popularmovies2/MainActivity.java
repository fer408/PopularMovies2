package com.example.android.popularmovies2;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieFragment.Callback {


    //Code that is called when an activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//Code connecting to activity_main.xml
        ButterKnife.bind(this);
        MovieAdapter.MovieFragment fragment= (MovieAdapter.MovieFragment) getFragmentManager().findFragmentById(R.id.movie_list);
        fragment.setCallback(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);//Code that inflates the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Code to handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Code connecting to settings
        if (id == R.id.action_settings) {//Code connecting to the string action_setting
            Intent intent=new Intent(this,SettingsActivity.class);//Code connecting to the SettingsActivity.java
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void movieSelected(APIClient.Model movie) {//Code connecting to my API.java file Model class
        Intent intent=new Intent(this, DetailActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }
}