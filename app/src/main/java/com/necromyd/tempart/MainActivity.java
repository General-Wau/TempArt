package com.necromyd.tempart;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button newArt, gallery, openEdit;
    ImageView about;
    final int IMAGE = 1;
    public static final String TAG = "MyActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        newArt = (Button) findViewById(R.id.btn_mainArt);
        gallery = (Button) findViewById(R.id.btn_mainGallery);
        openEdit = (Button) findViewById(R.id.btn_editFromGallery);
        about = (ImageView) findViewById(R.id.img_about);
        newArt.setOnClickListener(this);
        gallery.setOnClickListener(this);
        openEdit.setOnClickListener(this);
        about.setOnClickListener(this);
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
            case R.id.btn_editFromGallery:
                Log.d(TAG, "Button Click");
                pickAnImage();
                break;
            case R.id.img_about:
                Intent c = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(c);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMAGE && data != null) {
            Log.d(TAG, "Data from picked image is not null, creating uri and path");
            Uri selectedImageUri = data.getData();
            String picturePath = getPath(getApplicationContext(), selectedImageUri);
//            Log.d("Picture Path", picturePath);

            if (picturePath != null) {
                Log.d(TAG, "Path creating success, calling art activity");
                Intent intent = new Intent(getApplicationContext(), ArtActivity.class);
                intent.putExtra("image", picturePath);
                startActivity(intent);
            } else {
                Log.d(TAG, "Path was null");
                finish();
            }
        }else{
            Log.d(TAG, "Data seems to be null, aborting");
        }
    }

    private static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
                Toast.makeText(context, "" + result, Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }
        else {
            Toast.makeText(context.getApplicationContext(), "Failed to get image path , result is null or permission problem ?", Toast.LENGTH_SHORT).show();
            result = "Not found";
            Toast.makeText(context, "" + result, Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void pickAnImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "SDK >= Q , requesting permission");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "SDK < Q , requesting permission");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        } else {
            Log.d(TAG, "Permission exists, starting pick intent");
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            Log.d(TAG, "result code good");
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission is granted, starting pick");
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, IMAGE);
            } else {
                Log.d(TAG, "While result code is good, permission was denied");
                Toast.makeText(MainActivity.this, "Permission Denied !", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
