package com.necromyd.tempart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<Cell> galleryList;
    private Context context;

    public ImageAdapter(Context context, ArrayList<Cell> galleryList) {
        this.context = context;
        this.galleryList = galleryList;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell, parent, false);
        return new ImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, final int position) {
        holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        setImageFromPath(galleryList.get(position).getPath(), holder.img);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
                Toast.makeText(context, "" + galleryList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }

    private void setImageFromPath(String path, ImageView image){
        File imgFile = new File(path);

        try {
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeStream(new FileInputStream(imgFile));
                image.setImageBitmap(myBitmap);
                //            ImageView imageView = (ImageView)findViewById(R.id.imageViewSelect);
//            imageView.setImageBitmap(bitmap);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}












