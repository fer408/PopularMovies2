package com.example.android.popularmovies2;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import butterknife.Bind;
import butterknife.ButterKnife;

//Code that creates my DetailActivity class
public class DetailActivity extends AppCompatActivity {

    Bundle params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);//Code that connects to my detail_activity.xml file
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DetailFragment fragment=new DetailFragment();
        Fragment detailsFragment=getFragmentManager().findFragmentByTag("details");
        if (detailsFragment==null) {
            params=getIntent().getExtras();//Code that
            fragment.setArguments(params);//set's my params

            getFragmentManager().beginTransaction().add(R.id.container,fragment,"details").commit();
        }

    }

    @Override//Code for when this hook is called whenever an item in your options menu is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return (super.onOptionsItemSelected(item));
    }


    //Code for my DetailFragment class
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<APIClient.Model> {


        private View rootView;
        @Bind(R.id.title)
        TextView titleView;

        @Bind(R.id.poster)
        ImageView posterImageView;
        private APIClient.Model movie;
        @Bind(R.id.year)
        TextView yearView;
        @Bind(R.id.rating)
        TextView ratingView;
        @Bind(R.id.overview)
        TextView overviewView;
        @Bind(R.id.runtime)
        TextView runtimeView;

        boolean detailsLoaded=false;

        public DetailFragment() {

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Code that inflates the layout for this fragment
            rootView = inflater.inflate(R.layout.detail_fragment, container, false);
            ButterKnife.bind(this, rootView);
            if (savedInstanceState == null) {
                movie = getArguments().getParcelable("movie");
            } else {
                movie = savedInstanceState.getParcelable("movie");
                ratingView.setText(movie.getVoteAverage() + "/10");
                runtimeView.setText(String.valueOf(movie.getRuntime()) + "min");
                yearView.setText(movie.getReleaseYear());
                detailsLoaded=true;

            }
            //Code that connects this DetailActivity.java file with the detail_activity.xml file
            titleView.setText(movie.getTitle());
            overviewView.setText(movie.getOverview());
            Picasso.with(getActivity()).load(movie.getCachedPosterPath()).placeholder(R.drawable.notification_template_icon_bg).into(posterImageView);//Code that brings up Posters

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        @Override
        public void onStart() {
            super.onStart();
            // Code that loads details from API server
            if (!detailsLoaded) {
                getLoaderManager().initLoader(0, null, this).forceLoad();
            }
        }

        @Override
        public Loader<APIClient.Model> onCreateLoader(int id, Bundle args) {//Code that loads data to app

            MovieLoader loader = new MovieLoader(getActivity());//Code that loads the movies
            loader.setId(movie.getId());
            return loader;

        }

        @Override
        public void onLoadFinished(Loader<APIClient.Model> loader, APIClient.Model data) {
            if (data == null) {
                Toast.makeText(getActivity(), getResources().getString(R.string.error), Toast.LENGTH_LONG).show();//Code that shows error message when user isn't connected to a network
                return;
            }
            movie.setVoteAvarage( data.getVoteAverage());
            movie.setReleaseDate(data.getReleaseDate());
            movie.setRuntime(data.getRuntime());
            ratingView.setText(data.getVoteAverage() + "/10");
            runtimeView.setText(String.valueOf(data.getRuntime()) + "min");
            yearView.setText(data.getReleaseYear());


        }

        @Override
        public void onLoaderReset(Loader<APIClient.Model> loader) {

        }

        @Override
        public void onSaveInstanceState(Bundle outState) {//This code is called when an activity is about to be killed
            super.onSaveInstanceState(outState);
            outState.putParcelable("movie", movie);
        }

        static class MovieLoader extends AsyncTaskLoader<APIClient.Model> {//Code for MovieLoader class

            String id;

            public void setId(String id) {
                this.id = id;
            }

            public MovieLoader(Context context) {
                super(context);
            }

            @Override
            public APIClient.Model loadInBackground() {
                try {
                    return APIClient.getInstance().getMovieDetails(id);
                } catch (Exception ex) {
                    return null;
                }
            }

        }


    }
}