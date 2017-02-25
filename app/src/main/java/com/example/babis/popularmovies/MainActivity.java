package com.example.babis.popularmovies;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {



    public static boolean TABLET = false;


    //method to check if it's a phone or a tablet
    public boolean isTablet(Context context)
    {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)==4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)==Configuration.SCREENLAYOUT_SIZE_LARGE);
        return(xlarge||large);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TABLET=isTablet(this);

        //start the main fragment
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new MainFragment()).commit();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int menuItem = item.getItemId();

        switch (menuItem){
            case R.id.most_popular_menu_item:
                if( !MainFragment.sortByPop) {
                    MainFragment.sortByPop = true;
                    setTitle(R.string.most_popular);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,new MainFragment()).commit();
                }
                break;
            case R.id.top_rated_menu_item:
                if( MainFragment.sortByPop) {
                    MainFragment.sortByPop = false;
                    setTitle(R.string.top_rated);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,new MainFragment()).commit();
                }
                break;



        }




        return true;
        }





    }

