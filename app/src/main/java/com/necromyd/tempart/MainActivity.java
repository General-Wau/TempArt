package com.necromyd.tempart;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button newArt, gallery, openEdit;
    final int IMAGE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        newArt = (Button) findViewById(R.id.btn_mainArt);
        gallery = (Button) findViewById(R.id.btn_mainGallery);
        openEdit = (Button) findViewById(R.id.btn_editFromGallery);
        newArt.setOnClickListener(this);
        gallery.setOnClickListener(this);
        openEdit.setOnClickListener(this);

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
            case R.id.btn_editFromGallery:
                pickAnImage();
                break;
        }
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMAGE && data!=null){
            Uri selectedImageUri = data.getData( );
            String picturePath = getPath( getApplicationContext(), selectedImageUri );
            Log.d("Picture Path", picturePath);

            if(picturePath != null){
                Intent intent = new Intent(getApplicationContext(), ArtActivity.class);
                intent.putExtra("image", picturePath);
                startActivity(intent);
//                finish();
            }else{
                Toast.makeText(MainActivity.this, "Intent is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    public void pickAnImage() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, IMAGE);
    }


}
