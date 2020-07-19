package com.necromyd.tempart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.necromyd.tempart.view.ArtView;

public class ArtActivity extends AppCompatActivity {

    private ArtView artView;
    private AlertDialog.Builder currentAlertDialog;
    private ImageView widthImageView;
    private AlertDialog dialogLineWidth;
    private AlertDialog colorDialog;
    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private View colorView;
    private boolean imageSaved;
    private static final String TAG = "ArtActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art);

        artView = findViewById(R.id.artView);
        imageSaved = false;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.getString("image") != null) {
                String path = extras.getString("image");
                Bitmap myBitmap = BitmapFactory.decodeFile(path);
                myBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
                artView.init(metrics , myBitmap);
            }
        }else{
            artView.init(metrics , null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.clearid:
                artView.clear();
                break;
            case R.id.saveid:
                artView.saveImage();
                break;
            case R.id.colorid:
                showColorDialog();
                break;
            case R.id.lineWidth:
                showLineWidthDialog();
                break;
            case R.id.eraseid:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void showLineWidthDialog() {
        currentAlertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.width_dialog, null);
        final SeekBar widthSeekbar = view.findViewById(R.id.seekBarId);
        widthSeekbar.setProgress(artView.getLineWidth());
        Button setLineWidthButton = view.findViewById(R.id.buttonDialogId);
        widthImageView = view.findViewById(R.id.imageViewId);
        setLineWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setLineWidth(widthSeekbar.getProgress());
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });

        widthSeekbar.setOnSeekBarChangeListener(widthSeekBarChange);
        currentAlertDialog.setView(view);
        dialogLineWidth = currentAlertDialog.create();
        dialogLineWidth.setTitle("Set Line Width");
        dialogLineWidth.show();
    }

    void showColorDialog() {
        currentAlertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.color_dialog, null);
        alphaSeekBar = view.findViewById(R.id.alphaSeekBar);
        redSeekBar = view.findViewById(R.id.redSeekBar);
        greenSeekBar = view.findViewById(R.id.greenSeekBar);
        blueSeekBar = view.findViewById(R.id.blueSeekBar);
        colorView = view.findViewById(R.id.colorView);

        //register SeekBar event Listeners
        alphaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);

        int color = artView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        Button setColorButton = view.findViewById(R.id.setColorButton);
        setColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(Color.argb(
                        alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                        greenSeekBar.getProgress(), blueSeekBar.getProgress()
                ));

                colorDialog.dismiss();
            }
        });

        currentAlertDialog.setView(view);
        currentAlertDialog.setTitle("Choose Color");
        colorDialog = currentAlertDialog.create();
        colorDialog.show();
    }

    private SeekBar.OnSeekBarChangeListener colorSeekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            artView.setBackgroundColor(Color.argb(
                    alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                    greenSeekBar.getProgress(), blueSeekBar.getProgress()
            ));

            //display the current color
            colorView.setBackgroundColor(Color.argb(
                    alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                    greenSeekBar.getProgress(), blueSeekBar.getProgress()
            ));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private SeekBar.OnSeekBarChangeListener widthSeekBarChange = new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap = Bitmap.createBitmap(300, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            Paint p = new Paint();
            p.setColor(artView.getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);

            bitmap.eraseColor(Color.WHITE);
            canvas.drawLine(10, 50, 287, 50, p);
            widthImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public void onBackPressed() {
        if (artView.getPathMap().isEmpty()) {
//            Intent c = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(c);
            super.onBackPressed();
        } else {
            currentAlertDialog = new AlertDialog.Builder(this);
            currentAlertDialog.setCancelable(false);
            View view = getLayoutInflater().inflate(R.layout.back_button_dialog, null);
            Button yesButton = view.findViewById(R.id.btn_dialogYes);
            Button noButton = view.findViewById(R.id.btn_dialogNo);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    artView.saveImage();
                    imageSaved = true;
                    dialogLineWidth.dismiss();
                    currentAlertDialog = null;
                    ArtActivity.super.onBackPressed();
                }
            });
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageSaved = true;
                    dialogLineWidth.dismiss();
                    currentAlertDialog = null;
                    ArtActivity.super.onBackPressed();
                }
            });

            currentAlertDialog.setView(view);
            dialogLineWidth = currentAlertDialog.create();
            dialogLineWidth.setTitle("Save image before exiting ?");
            dialogLineWidth.show();
        }
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean isFinishing() {
        Log.d(TAG, "onDestroy");
        if (!artView.getPathMap().isEmpty()) {
            if (!imageSaved) {
                artView.saveImage();
                imageSaved = true;
            }
        }
        return super.isFinishing();
    }


}
