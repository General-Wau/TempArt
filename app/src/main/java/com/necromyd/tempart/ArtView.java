package com.necromyd.tempart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ArtView extends View {

    public static final float TOUCH_TOLERANCE = 5;
    private Bitmap bitmap;
    private Path mPath;
    private static int layer;
    private float mX, mY;
    private Paint paintLine;
    private static ArrayList<Brush> path;
    private static ArrayList<Brush> layer1;
    private static ArrayList<Brush> layer2;
    private static ArrayList<Brush> layer3;
    private ArrayList<Brush> undo;
    public static String pathString;
    public int alphaSetting = 100;
    private int lineWidthSetting = 100;

    public ArtView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public ArrayList<Brush> getPath() {
        return path;
    }

    public void init(DisplayMetrics metrics, Bitmap bitmap) {
        layer = 1;
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        if (bitmap == null) {
            this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } else {
            this.bitmap = Bitmap.createBitmap(bitmap);
        }

        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setDither(true);
        paintLine.setStrokeJoin(Paint.Join.ROUND);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setColor(Color.BLACK);
        paintLine.setStrokeWidth(1);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
        paintLine.setXfermode(null);
        paintLine.setAlpha(0xff);

        layer1 = new ArrayList<>();
        layer2 = new ArrayList<>();
        layer3 = new ArrayList<>();
        path = layer1;
        undo = new ArrayList<>();

    }

//    //select color from bitmap via touch
    public void dropSelectColor(View v){
//        this.setDrawingCacheEnabled(true);
        v.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Bitmap bitmapTemp = getDrawingCache(true);
                int color = bitmap.getPixel((int)v.getX(), (int)v.getY());
                Toast.makeText(getContext(),"Color : " + color,Toast.LENGTH_SHORT).show();
                setDrawingColor(color);
                v.setOnTouchListener(null);
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
// change layer1 to path for rollback
        for (Brush brush : layer1) {
            paintLine.setColor(brush.color);
            paintLine.setStrokeWidth(brush.strokeWidth);
            paintLine.setMaskFilter(null);
            canvas.drawPath(brush.path, paintLine);
        }
        for (Brush brush : layer2) {
            paintLine.setColor(brush.color);
            paintLine.setStrokeWidth(brush.strokeWidth);
            paintLine.setMaskFilter(null);
            canvas.drawPath(brush.path, paintLine);
        }
        for (Brush brush : layer3) {
            paintLine.setColor(brush.color);
            paintLine.setStrokeWidth(brush.strokeWidth);
            paintLine.setMaskFilter(null);
            canvas.drawPath(brush.path, paintLine);
        }
        canvas.drawBitmap(bitmap, 0, 0, paintLine);

        canvas.restore();
    }

    public void undo() {
        if (path.size() > 0) {
            undo.add(path.remove(path.size() - 1));
            invalidate();
        } else {
            Snackbar.make(this, "Nothing to undo !", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void changeLayer(){

        if(layer == 1) {
            layer++;
            path = layer2;
            ArtActivity.btn_layers.setImageResource(R.drawable.ic_baseline_layer2);
        }
        else if (layer == 2) {
            layer++;
            path = layer3;
            ArtActivity.btn_layers.setImageResource(R.drawable.ic_baseline_layer3);
        }
        else if (layer == 3) {
            layer = 1;
            path = layer1;
            ArtActivity.btn_layers.setImageResource(R.drawable.ic_baseline_layer1);
        }
    }

    public void redo() {
        if (undo.size() > 0) {
            path.add(undo.remove(undo.size() - 1));
            invalidate();
        } else {
            Snackbar.make(this, "Nothing to redo !", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
        }
        return true;
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }

    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        Brush draw = new Brush(paintLine.getColor(), (int) paintLine.getStrokeWidth(), mPath);
        path.add(draw);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }


    //set drawing color
    public void setDrawingColor(int color) {
        paintLine.setColor(color);
        ArtActivity.fab.setBackgroundTintList(ColorStateList.valueOf(color));
        ArtActivity.initialColor = color;
    }

    //return current color
    public int getDrawingColor() {
        return paintLine.getColor();
    }

    public void setLineWidth(int width, int alpha) {
        paintLine.setStrokeWidth(width);
        paintLine.setAlpha(alpha);
        alphaSetting = alpha;
        lineWidthSetting = width;
    }

    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }

//    public void clear() {
//        pathMap.clear(); // removes all of the paths
//        previousPointMap.clear();
//        bitmap.eraseColor(Color.WHITE);
//        invalidate(); // refresh the screen
//    }


    @SuppressLint("WrongThread")
    public void saveImage() {
        ContextWrapper cw = new ContextWrapper(getContext());
        String filename = "TempART" + System.currentTimeMillis();
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("files", Context.MODE_PRIVATE);
        pathString = cw.getDir("files", Context.MODE_PRIVATE).toString();
        // create imageDir
        File myPath = new File(directory, filename + ".jpg");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(myPath);
            //Use the compress method on the BitMap object to write image to the OutputStream

            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            draw(canvas);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fileOutputStream != null;
                fileOutputStream.flush();
                fileOutputStream.close();
                Log.d("Image:", directory.getAbsolutePath());
                Toast message = Toast.makeText(getContext(), "Image Saved +" + directory.getAbsolutePath(), Toast.LENGTH_LONG);
                message.setGravity(Gravity.BOTTOM, message.getXOffset() / 2, message.getYOffset() / 2);
                message.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
