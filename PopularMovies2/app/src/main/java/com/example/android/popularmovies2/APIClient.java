package com.example.android.popularmovies2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

//Code that allows the movieDB API to connect to app
public class APIClient implements RequestInterceptor {
    public static  final String API_KEY="5d3b2f3565afd08da3888d5a62c01c0f";//API key
    public static  final String KEY_PARAM="api_key";
    public static final String BASE_URL="https://api.themoviedb.org/3";//Base URL
    private static APIMethods api;
    private static APIClient instance;
    private APIClient() {

    }
    public static  APIClient getInstance(){//Code for instance
        if (instance==null){
            instance=new APIClient();
        }
        return instance;
    }
    private APIMethods getApi(){//Code that calls the API
        if (api==null){
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setRequestInterceptor(this)
                    .setEndpoint(APIClient.BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            api=restAdapter.create(APIMethods.class);
        }
        return api;
    }
    @Override
    public void intercept(RequestFacade request) {//Code that implements from retrofit.RequestInterceptor method
        request.addQueryParam(APIClient.KEY_PARAM,APIClient.API_KEY);
    }
    public List<Model> getPopular(){//Code that gets most popular movie info from the MovieDB API database
        Response result=getApi().getPopular();
        return  result.getResults();
    }
    public List<Model> getTopRated(){//Code to switch from most popular to Top rated infro from the MovieDB API
        Response result=getApi().getTopRated();
        return  result.getResults();
    }

    public Model getMovieDetails(String id) {//Code that gets movie details from API
        Model m=getApi().getMovieDetails(id);
        return m;
    }

    private   interface APIMethods{//Codes that connects API to their desired app function
        @GET("/movie/popular")
        Response getPopular();
        @GET("/movie/top_rated")
        Response getTopRated();
        @GET("/movie/{id}")
        Model getMovieDetails(@Path("id") String id);
    }

    //Code for my model class which will oversee my app functions
    public static class  Model implements Parcelable {

        public static final Creator<Model> CREATOR = new Creator<Model>() {

            public Model createFromParcel(Parcel in) {

                return new Model(in);
            }

            public Model[] newArray(int size) {
                return new Model[size];
            }
        };

        private String overview;
        private String title;
        @SerializedName("poster_path")
        private String posterPath;
        @SerializedName("vote_average")
        private String voteAverage;
        @SerializedName("release_date")
        private String releaseDate="";
        @SerializedName("movie_trailer")
        private String movieTrailer;
        private String id;
        private int runtime;
        private String cachedPosterPath;

        public Model() {

        }

        // Code for parcel construtor
        protected Model(Parcel p) {
            id = p.readString();
            overview = p.readString();
            posterPath = p.readString();
            title = p.readString();
            voteAverage = p.readString();
            movieTrailer = p.readString();
            releaseDate=p.readString();
            runtime=p.readInt();
            cachedPosterPath=p.readString();
        }

        public String getCachedPosterPath() {
            return cachedPosterPath;
        }

        public void setCachedPosterPath(String cachedPosterPath) {
            this.cachedPosterPath = cachedPosterPath;
        }




        //Code for strings that will be used troughout the App
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVoteAverage() {
            return voteAverage;
        }

        public void setVoteAvarage(String voteAverage) {
            this.voteAverage = voteAverage;
        }

        public String getMovieTrailer(){return movieTrailer;}

        public void setMovieTrailer(String movieTrailer){this.movieTrailer}

        public String getReleaseYear(){
            return releaseDate.substring(0,4);
        }

        public int getRuntime() {
            return runtime;
        }

        public void setRuntime(int runtime) {
            this.runtime = runtime;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(overview);
            dest.writeString(posterPath);
            dest.writeString(title);
            dest.writeString(voteAverage);
            dest.writeString(movieTrailer);
            dest.writeString(releaseDate);
            dest.writeInt(runtime);
            dest.writeString(cachedPosterPath);
        }


    }

    //Code for my Response class
    public static class Response {
        List<Model> results;
        int page;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<Model> getResults() {
            return results;
        }

        public void setResults(List<Model> results) {
            this.results = results;
        }
    }
}
