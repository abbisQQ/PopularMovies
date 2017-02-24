package com.example.babis.popularmovies;

import android.content.Context;
import android.preference.ListPreference;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Babis on 2/24/2017.
 */

public class ImageAdaper extends RecyclerView.Adapter<ImageAdaper.MyViewHolder>{

    ArrayList<String> myResult;
     LayoutInflater inflater;
    Context myContext;
    private int width;



     ItemClickCallBack itemClickCallBack;



    public  interface ItemClickCallBack {
        void onItemClick(int p);

    }


    public void setItemClickCallBack(final ItemClickCallBack itemClickCallBack) {
        this.itemClickCallBack = itemClickCallBack;
    }



    public ImageAdaper(ArrayList<String> result, Context context,int x) {
        myResult = result;
        myContext = context;
        width = x;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.movie_poster,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String item = myResult.get(position);
        Picasso.with(myContext)
                .load("http://image.tmdb.org/t/p/w185/" + item)
                .resize(width, (int)(width*1.5))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return myResult.size();
    }

     class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

         ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.movie_image_view);
            itemView.setOnClickListener(this);



        }


         @Override
         public void onClick(View v) {
             itemClickCallBack.onItemClick(getAdapterPosition());
         }
     }
}
