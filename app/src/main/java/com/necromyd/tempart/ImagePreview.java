package com.necromyd.tempart;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ImagePreview extends AppCompatActivity implements View.OnClickListener {
    Button btn1, btn2;
    ImageView imageView;
    String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btn1 = (Button) findViewById(R.id.btn_showImageEdit);
        btn2 = (Button) findViewById(R.id.btn_showImageDelete);
        imageView = (ImageView) findViewById(R.id.showImageId);


        Intent intent = getIntent();
        name = intent.getStringExtra("imagePath");

        Bitmap myBitmap = BitmapFactory.decodeFile(name);
        imageView.setImageBitmap(myBitmap);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_showImageEdit:
                Intent intent = new Intent(getApplicationContext(), ArtActivity.class);
                intent.putExtra("image", name);
                startActivity(intent);
                finish();
                break;

            case R.id.btn_showImageDelete:
                File file = new File(name);
                if (file.exists()) {
                    file.delete();
                    finish();
                }
                break;
        }

    }
}
