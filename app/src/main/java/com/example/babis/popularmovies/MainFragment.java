package com.example.babis.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements ImageAdaper.ItemClickCallBack{


    public MainFragment() {
        // Required empty public constructor
    }


    private RecyclerView recyclerView;
    private ImageAdaper adapter;
    int width;
    final String API_KEY = "";
    static boolean sortByPop = true;
    ProgressBar bar;
    static ArrayList<String> posters, ratings, titles, overviews, dates;
    String JSONResults;
    ArrayList<String> resultsForAdapter = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_main, container, false);


        bar = (ProgressBar)view.findViewById(R.id.my_bar);
        recyclerView = (RecyclerView)view.findViewById(R.id.my_recycler_view);
        GridLayoutManager manager =  new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(manager);


        WindowManager wm =(WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if(MainActivity.TABLET)
        {
            width = size.x/6;
        }
        else width=size.x/2;



        if(savedInstanceState==null) {
            if (isNetworkAvailable()) {
                new ImageLoadTask().execute();
            } else {
                Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            }

        }else {

            resultsForAdapter = savedInstanceState.getStringArrayList(String.valueOf(R.string.results_for_the_adapter_key));
            adapter =  new ImageAdaper(resultsForAdapter,getActivity(),width);
            adapter.setItemClickCallBack(MainFragment.this);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            bar.setVisibility(View.INVISIBLE);
        }



        return view;

    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null &&activeNetworkInfo.isConnected();
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(String.valueOf(R.string.results_for_the_adapter_key),resultsForAdapter);
    }




    @Override
    public void onItemClick(int p) {
        Intent intent = new Intent(getActivity(),MovieDetailsActivity.class)
                .putExtra("overview",overviews.get(p))
                .putExtra("rating",ratings.get(p))
                .putExtra("title",titles.get(p))
                .putExtra("date",dates.get(p))
                .putExtra("poster",posters.get(p));

        startActivity(intent);

    }


    public class ImageLoadTask extends AsyncTask<Void,Void,ArrayList<String>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setVisibility(View.INVISIBLE);
            bar.setVisibility(View.VISIBLE);


        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while(true){
                try{
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPop)));
                    return posters;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if(result!=null&&getActivity()!=null){
                resultsForAdapter = result;
                Bundle res = new Bundle();
                res.putStringArrayList(String.valueOf(R.string.results_for_the_adapter_key),result);
                adapter =  new ImageAdaper(result,getActivity(),width);
                adapter.setItemClickCallBack(MainFragment.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                bar.setVisibility(View.INVISIBLE);
            }








        }
    }
    String[] getPathsFromAPI(boolean sortByPop){
        while (true){
            HttpURLConnection httpURLConnection =null;
            BufferedReader reader = null;



            try{
                String urlString;
                if (sortByPop) {
                    urlString = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
                } else {
                    urlString = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;
                }
                URL url = new URL(urlString);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                //Read the input stream
                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream==null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line=reader.readLine())!=null){
                    buffer.append(line+"\n");
                }
                if(buffer.length()==0){
                    return null;
                }
                JSONResults = buffer.toString();


                try{


                    overviews = new ArrayList<>(Arrays.asList(getMovieDetailsFromJSON(JSONResults,"overview")));
                    dates = new ArrayList<>(Arrays.asList(getMovieDetailsFromJSON(JSONResults,"release_date")));
                    ratings = new ArrayList<>(Arrays.asList(getMovieDetailsFromJSON(JSONResults,"vote_average")));
                    titles = new ArrayList<>(Arrays.asList(getMovieDetailsFromJSON(JSONResults,"original_title")));



                    return getPathsFromJSON(JSONResults);
                }catch (JSONException e){
                    return null;
                }



            }catch (Exception e){

            }finally {
                if(httpURLConnection!=null){
                    httpURLConnection.disconnect();
                }
                if(reader!=null){
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }




    String[] getMovieDetailsFromJSON(String JSONStringParam, String param) throws JSONException{
        JSONObject JSONString = new JSONObject(JSONStringParam);

        JSONArray moviesArray = JSONString.getJSONArray("results");
        String[] result = new String[moviesArray.length()];

        for(int i = 0; i<moviesArray.length();i++)
        {
            JSONObject movie = moviesArray.getJSONObject(i);

            if(param.equals("vote_average")) {
                Double movieRating = movie.getDouble("vote_average");
                String data =  Double.toString(movieRating)+"/10";
                result[i]=data;
            }else {
                // instead of the poster_path this time we can get everything else using the second parameter param
                String data = movie.getString(param);
                result[i] = data;
            }
        }
        return result;
    }












    public String[] getPathsFromJSON(String JSONParams)throws JSONException{

        JSONObject JSONString = new JSONObject(JSONParams);

        JSONArray movieArray = JSONString.getJSONArray("results");

        String[] results = new String[movieArray.length()];

        for(int i =0; i<movieArray.length(); i++){
            JSONObject movie = movieArray.getJSONObject(i);
            String moviePath = movie.getString("poster_path");
            results[i] = moviePath;

        }
            return results;
    }




}
