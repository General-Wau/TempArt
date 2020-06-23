package com.necromyd.tempart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button newArt, gallery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newArt = (Button) findViewById(R.id.btn_mainArt);
        gallery = (Button) findViewById(R.id.btn_mainGallery);
        newArt.setOnClickListener(this);
        gallery.setOnClickListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mainArt:
                Intent a = new Intent(getApplicationContext(), ArtActivity.class);
                startActivity(a);
                break;
            case R.id.btn_mainGallery:
                Intent b = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(b);
                break;
        }
    }
}
