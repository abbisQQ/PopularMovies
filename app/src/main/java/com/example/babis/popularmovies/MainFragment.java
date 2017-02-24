package com.example.babis.popularmovies;


import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
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
public class MainFragment extends Fragment{


    public MainFragment() {
        // Required empty public constructor
    }

    private ArrayList listData;
    private RecyclerView recyclerView;
    private ImageAdaper adapter;
    int width;
    final String API_KEY = "f780f720bae7adbe3ff65f2c74337c36";
    static ArrayList<String> posters;
    static boolean sortByPop = true;
    ProgressBar bar;



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


        return view;
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null &&activeNetworkInfo.isConnected();
    }


    @Override
    public void onStart() {
        super.onStart();

        if (isNetworkAvailable()) {
            new ImageLoadTask().execute();
        } else {
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
        }

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
                adapter =  new ImageAdaper(result,getActivity(),width);
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
            String JSONResults;


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
