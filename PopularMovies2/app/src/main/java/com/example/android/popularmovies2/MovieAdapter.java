package com.example.android.popularmovies2;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


//Code for my movie adapter
public class MovieAdapter extends ArrayAdapter<APIClient.Model> {
    private int imageHeight;

    public MovieAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.poster);
            viewHolder.title=(TextView)convertView.findViewById(R.id.title);
            convertView.setTag(viewHolder);
        }
        viewHolder= (ViewHolder) convertView.getTag();
        APIClient.Model movie=getItem(position);
        if (movie.getCachedPosterPath()==null) {
            movie.setCachedPosterPath("http://image.tmdb.org/t/p/w" + String.valueOf(imageWidth) + "/" + movie.getPosterPath());
        }
        viewHolder.title.setText(movie.getTitle());
        viewHolder.image.setMinimumHeight(imageHeight);
        viewHolder.image.setMinimumWidth(imageWidth);
        Picasso.with(getContext()).load(movie.getCachedPosterPath()).into(viewHolder.image);
        if (movie.getCachedPosterPath()==null) {
            movie.setCachedPosterPath("http://image.tmdb.org/t/p/w" + String.valueOf(imageWidth) + "/" + movie.getMovieTrailer());
        }
        return convertView;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    static class ViewHolder {
        public ImageView image;
        public TextView title;
    }
    int imageWidth;

    public void setImageWidth(int imageWidth) {
        //code to make poster size
        if (imageWidth>701) {
            this.imageWidth = 500;
        }else if(imageWidth>501){
            this.imageWidth = 500;
        }else if (imageWidth>301){
            this.imageWidth=350;
        }else{
            this.imageWidth=200;
        }

    }


    public static class MovieFragment extends Fragment implements
            LoaderManager.LoaderCallbacks<List<APIClient.Model>>{

        APIClient.Model[] data;


        //Code for fragment gridview
        @Bind(R.id.grid)
        GridView gridView;



        private MovieAdapter mAdapter;
        private View rootView;
        private Callback callback;
        private String currentOrder;


        public MovieFragment() {
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);



        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public void onStart() {
            super.onStart();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String order = prefs.getString("sort_order", "");
            if (currentOrder==null) currentOrder=order;
            // reload list if settings are changed
            if (!currentOrder.equals(order)){
                getLoaderManager().initLoader(0, null, MovieFragment.this).forceLoad();
                currentOrder=order;
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.movie_fragment, container, false);
            ButterKnife.bind(this,rootView);
            mAdapter=new MovieAdapter(getActivity(),R.layout.list_item);
            gridView.setAdapter(mAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (callback != null) callback.movieSelected(mAdapter.getItem(position));
                }
            });

            ViewTreeObserver vto = rootView.getViewTreeObserver();
            //wait for layout rendiring to obtain grid view width in pixels
            if (savedInstanceState!=null){
                data= (APIClient.Model[]) savedInstanceState.getParcelableArray("movies");
            }

            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int width = gridView.getMeasuredWidth()/ gridView.getNumColumns();
                    mAdapter.setImageWidth(width);
                    mAdapter.setImageHeight((int) (width * 1.58));
                    if (data==null){
                        getLoaderManager().initLoader(0, null, MovieFragment.this).forceLoad();
                    }else{
                        mAdapter.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }


                }
            });


            return rootView;
        }

        @Override
        public void onDetach() {
            super.onDetach();

        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelableArray("movies", data);
        }

        @Override
        public Loader<List<APIClient.Model>> onCreateLoader(int id, Bundle args) {
            Loader<List<APIClient.Model>> loader=new MovieLoader(getActivity());
            return  loader;
        }

        @Override
        public void onLoadFinished(Loader<List<APIClient.Model>> loader, List<APIClient.Model> loadedData) {
            if (loadedData.size()==0) {
                Toast.makeText(getActivity(),getResources().getString(R.string.error),Toast.LENGTH_LONG).show();
                return;
            }
            mAdapter.clear();
            mAdapter.addAll(loadedData);
            this.data=new APIClient.Model[loadedData.size()];
            loadedData.toArray(this.data);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<APIClient.Model>> loader) {

        }

        public interface Callback {
            // TODO: Update argument type and name
            void movieSelected(APIClient.Model movie);
        }
        public static class MovieLoader extends AsyncTaskLoader<List<APIClient.Model>> {

            public MovieLoader(Context context) {
                super(context);
            }

            @Override
            public List<APIClient.Model> loadInBackground() {
                try {

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                    String order = prefs.getString("sort_order", "");
                    if (order.equals("top_rated")) {
                        return APIClient.getInstance().getTopRated();
                    } else {
                        return APIClient.getInstance().getPopular();
                    }
                }catch (Exception ex){

                    return new ArrayList<APIClient.Model>();
                }
            }

        }

        public void setCallback(Callback callback) {
            this.callback = callback;
        }
    }
}