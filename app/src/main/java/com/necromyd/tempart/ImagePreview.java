package com.necromyd.tempart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ImagePreview extends AppCompatActivity {
    Button btn1 , btn2;
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview);
        btn1 = (Button) findViewById(R.id.btn_showImageEdit);
        btn2 = (Button) findViewById(R.id.btn_showImageDelete);
        imageView = (ImageView) findViewById(R.id.showImageId);


        Intent intent = getIntent();
        String name = intent.getStringExtra("imagePath");

        Bitmap myBitmap = BitmapFactory.decodeFile(name);
        imageView.setImageBitmap(myBitmap);
    }

}
