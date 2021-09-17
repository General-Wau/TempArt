package com.necromyd.tempart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ArtActivity extends AppCompatActivity implements View.OnClickListener {

    private ArtView artView;
    private AlertDialog.Builder currentAlertDialog;
    private ImageView widthImageView;
    private AlertDialog dialogLineWidth;
    private AlertDialog colorDialog;
    private View colorView;
    private boolean declinedToSaveImage;
    public static int initialColor;
    private SeekBar seekbarWidth, seekbarAlpha;
    private boolean visible = true;
    static FloatingActionButton fab;
    private static final String TAG = "ArtActivity";
    public static ArrayList<Integer> colorArrayList;

    static ImageView btn_brush, btn_palette, btn_picker, btn_eraser, btn_redo, btn_undo,
            btn_clear, btn_save, btn_layers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        artView = findViewById(R.id.artView);
        artView.setLayerType(View.LAYER_TYPE_HARDWARE,null);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Floating action button functionality
        LinearLayout tools = (LinearLayout) findViewById(R.id.Layout_Tools);
        LinearLayout utils = (LinearLayout) findViewById(R.id.Layout_Util);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (visible) {
                tools.setVisibility(View.INVISIBLE);
                utils.setVisibility(View.INVISIBLE);
                visible = false;
            } else {
                tools.setVisibility(View.VISIBLE);
                utils.setVisibility(View.VISIBLE);
                visible = true;
            }
        });

        // Init
        btn_brush = findViewById(R.id.btn_brush);
        btn_palette = findViewById(R.id.btn_palette);
        btn_picker = findViewById(R.id.btn_picker);
//        btn_eraser = findViewById(R.id.btn_eraser);
        btn_redo = findViewById(R.id.btn_redo);
        btn_undo = findViewById(R.id.btn_undo);
        btn_clear = findViewById(R.id.btn_clear);
        btn_save = findViewById(R.id.btn_save);
        btn_layers = findViewById(R.id.btn_layers);
        initialColor = Color.BLACK;

        //Listeners
        btn_brush.setOnClickListener(this);
        btn_palette.setOnClickListener(this);
        btn_picker.setOnClickListener(this);
//        btn_eraser.setOnClickListener(this);
        btn_redo.setOnClickListener(this);
        btn_undo.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_layers.setOnClickListener(this);

        colorArrayList = setUpColors();

        // Used when choosing to edit a picture from the gallery . It will fetch it's file path
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.getString("image") != null) {
                String path = extras.getString("image");
                Bitmap myBitmap = BitmapFactory.decodeFile(path);
                myBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
                artView.init(metrics, myBitmap);
            }
        } else {
            artView.init(metrics, null);
        }
    }



    //Handle tool buttons
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_brush) showLineWidthDialog();
        else if (v.getId() == R.id.btn_undo) {
            artView.undo();
        } else if (v.getId() == R.id.btn_redo) {
            artView.redo();
        } else if (v.getId() == R.id.btn_palette) {
            showColorDialog(v);
        } else if (v.getId() == R.id.btn_layers){
            artView.changeLayer();
        } else if (v.getId() == R.id.btn_clear){
            artView.clear();
        } else if (v.getId() == R.id.btn_save){
            artView.saveImage();
        }
//        else if (v.getId() == R.id.btn_eraser){
//            artView.erase(true);
//        }
        else if (v.getId() == R.id.btn_picker){
            showPickerDialog();
        }
    }

    // Line width and transparency dialog
    void showLineWidthDialog() {
        currentAlertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.width_dialog, null);
        seekbarWidth = view.findViewById(R.id.seekBarId);
        seekbarAlpha = view.findViewById(R.id.alphaSeekBar);
        seekbarWidth.setProgress(artView.getLineWidth());
        seekbarAlpha.setProgress(artView.alphaSetting);

        Button setLineWidthButton = view.findViewById(R.id.buttonDialogId);
        widthImageView = view.findViewById(R.id.imageViewId);
        setLineWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setLineWidth(seekbarWidth.getProgress(), seekbarAlpha.getProgress());
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });

        seekbarWidth.setOnSeekBarChangeListener(widthSeekBarChange);
        seekbarAlpha.setOnSeekBarChangeListener(widthSeekBarChange);
        currentAlertDialog.setView(view);
        dialogLineWidth = currentAlertDialog.create();
        dialogLineWidth.setTitle("Set Line Width");
        dialogLineWidth.show();
    }

    void showPickerDialog() {
        currentAlertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.color_dialog, null);

        Button cancel = view.findViewById(R.id.setColorButton);
        ImageView img1 = view.findViewById(R.id.imgView1);
        ImageView img2 = view.findViewById(R.id.imgView2);
        ImageView img3 = view.findViewById(R.id.imgView3);
        ImageView img4 = view.findViewById(R.id.imgView4);
        ImageView img5 = view.findViewById(R.id.imgView5);
        ImageView img6 = view.findViewById(R.id.imgView6);
        ImageView img7 = view.findViewById(R.id.imgView7);
        ImageView img8 = view.findViewById(R.id.imgView8);
        ImageView img9 = view.findViewById(R.id.imgView9);
        ImageView img10 = view.findViewById(R.id.imgView10);
        ImageView img11 = view.findViewById(R.id.imgView11);
        ImageView img12 = view.findViewById(R.id.imgView12);
        ImageView img13 = view.findViewById(R.id.imgView13);
        ImageView img14 = view.findViewById(R.id.imgView14);
        ImageView img15 = view.findViewById(R.id.imgView15);

        ArrayList<ImageView> imageViews = new ArrayList<>();
        imageViews.add(img1);
        imageViews.add(img2);
        imageViews.add(img3);
        imageViews.add(img4);
        imageViews.add(img5);
        imageViews.add(img6);
        imageViews.add(img7);
        imageViews.add(img8);
        imageViews.add(img9);
        imageViews.add(img10);
        imageViews.add(img11);
        imageViews.add(img12);
        imageViews.add(img13);
        imageViews.add(img14);
        imageViews.add(img15);

        int count = 0;
        for(ImageView img : imageViews){
            img.setBackgroundColor(colorArrayList.get(count));
            count++;
        }

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(0));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(1));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(2));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(3));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(4));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(5));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(6));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(7));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(8));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(9));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(10));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(11));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(12));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(13));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });
        img15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artView.setDrawingColor(colorArrayList.get(14));
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLineWidth.dismiss();
                currentAlertDialog = null;
            }
        });

        currentAlertDialog.setView(view);
        dialogLineWidth = currentAlertDialog.create();
        dialogLineWidth.setTitle("Select previously used color");
        dialogLineWidth.show();
    }

    // Color Dialog
    void showColorDialog(View v) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // color is the color selected by the user.
                artView.setDrawingColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // cancel was selected by the user
            }
        });
        dialog.show();
    }

    // Line width seekbar listener
    private final SeekBar.OnSeekBarChangeListener widthSeekBarChange = new SeekBar.OnSeekBarChangeListener() {
        final Bitmap bitmap = Bitmap.createBitmap(300, 100, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            Paint p = new Paint();
            p.setColor(artView.getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(seekbarWidth.getProgress());
            p.setAlpha(seekbarAlpha.getProgress());

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

    //Save image before exit
    @Override
    public void onBackPressed() {
        if (artView.imageSaved || artView.getPath().isEmpty()) {
            super.onBackPressed();
        }
        else {
            currentAlertDialog = new AlertDialog.Builder(this);
            currentAlertDialog.setCancelable(false);
            View view = getLayoutInflater().inflate(R.layout.back_button_dialog, null);
            Button yesButton = view.findViewById(R.id.btn_dialogYes);
            Button noButton = view.findViewById(R.id.btn_dialogNo);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    artView.saveImage();
                    dialogLineWidth.dismiss();
                    currentAlertDialog = null;
                    ArtActivity.super.onBackPressed();
                }
            });
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    declinedToSaveImage = true;
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

    // Check if image is saved, save if not
    @Override
    protected void onStop() {
        Log.d(TAG, "onDestroy");
        if (!artView.getPath().isEmpty()) {
            if (!declinedToSaveImage & !artView.imageSaved) {
                artView.saveImage();
            }
        }
        super.onStop();
    }

    // Save image before destroying activity
    @Override
    public boolean isFinishing() {
        Log.d(TAG, "onDestroy");
        if (!artView.getPath().isEmpty()) {
            if (!declinedToSaveImage & !artView.imageSaved) {
                artView.saveImage();
            }
        }
        return super.isFinishing();
    }

    private ArrayList<Integer> setUpColors(){
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add( -16777216);
        temp.add( -16777216);
        temp.add( -16777216);
        temp.add( -16777216);
        temp.add( -16777216);

        temp.add( -16777216);
        temp.add( -16777216);
        temp.add( -16777216);
        temp.add( -16777216);
        temp.add( -16777216);

        temp.add( -16777216);
        temp.add( -16777216);
        temp.add( -16777216);
        temp.add( -16777216);
        temp.add( -16777216);

        return temp;
    }
}
