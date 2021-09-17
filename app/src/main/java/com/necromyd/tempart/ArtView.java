package com.necromyd.tempart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ArtView extends View {

    public static final float TOUCH_TOLERANCE = 5;
    private Bitmap bitmap, loadedBitmap;
    private Path mPath;
    private static int layer;
    private float mX, mY;
    public boolean imageSaved;
    private Paint paintLine, tPaintline;
    private static ArrayList<Brush> path;
    private static ArrayList<Brush> layer1;
    private static ArrayList<Brush> layer2;
    private static ArrayList<Brush> layer3;
    private ArrayList<Brush> undo;
    private boolean eraseMode = false;
    private int lastPaintColor;
    public static String pathString;
    private Context context;
    public int alphaSetting = 100;

    public ArtView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void init(DisplayMetrics metrics, Bitmap bitmap) {
        layer = 1;
        layer1 = new ArrayList<>();
        layer2 = new ArrayList<>();
        layer3 = new ArrayList<>();
        path = layer1;
        undo = new ArrayList<>();

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;


        if (bitmap != null) {
            loadedBitmap = scale(bitmap, width, height);
        }else {
            this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888, true);
        }
        tPaintline = new Paint();
        tPaintline.setColor(Color.TRANSPARENT);
        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setDither(true);
        paintLine.setStrokeJoin(Paint.Join.ROUND);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setColor(Color.BLACK);
        paintLine.setStrokeWidth(1);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
        paintLine.setXfermode(null);
        paintLine.setAlpha(255);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if (loadedBitmap != null) {
            canvas.drawBitmap(loadedBitmap, 0, 0, paintLine);
            canvas.clipRect(0,0,loadedBitmap.getWidth(),loadedBitmap.getHeight());
        } else {
            canvas.drawBitmap(bitmap, 0, 0, tPaintline);
        }


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

        canvas.restore();
    }

//    public void erase(boolean eraseMode) {
//        if(eraseMode){
//            paintLine.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//            paintLine.setColor(Color.TRANSPARENT);
//        }else {
//            paintLine.setXfermode(null);
//            paintLine.setColor(lastPaintColor);
//        }
//    }

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
        imageSaved = false;
        mPath = new Path();
        Brush draw = new Brush(getDrawingColor(), (int) getLineWidth(), mPath);
        path.add(draw);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    public void setLineWidth(int width, int alpha) {
//        erase(false);
        paintLine.setStrokeWidth(width);
        paintLine.setAlpha(alpha);
        alphaSetting = alpha;
    }

    public ArrayList<Brush> getPath() {
        return path;
    }
    public void setDrawingColor(int color) {
        paintLine.setColor(color);
        ArtActivity.fab.setBackgroundTintList(ColorStateList.valueOf(color));
        ArtActivity.initialColor = color;
        lastPaintColor = color;
        ArtActivity.colorArrayList.remove(0);
        ArtActivity.colorArrayList.add(color);
    }
//    public void dropSelectColor(View v) {
////        this.setDrawingCacheEnabled(true);
//        v.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
////                Bitmap bitmapTemp = getDrawingCache(true);
//                int color = bitmap.getPixel((int) v.getX(), (int) v.getY());
//                Toast.makeText(getContext(), "Color : " + color, Toast.LENGTH_SHORT).show();
//                setDrawingColor(color);
//                v.setOnTouchListener(null);
//                return true;
//            }
//        });
//    }
    public void clear() {
        AlertDialog.Builder clearConfirm = new AlertDialog.Builder(getContext());
        clearConfirm.setCancelable(false);
        clearConfirm.setTitle("Save the image and clear everything ?");
        clearConfirm.setPositiveButton("Save and Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
                layer1.clear();
                layer2.clear();
                layer3.clear();
                invalidate();
            }
        });
        clearConfirm.setNeutralButton("Just clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //clear code here
                layer1.clear();
                layer2.clear();
                layer3.clear();
                invalidate();
            }
        });
        clearConfirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = clearConfirm.create();
        dialog.show();
    }
    public void changeLayer() {

        if (layer == 1) {
            layer++;
            path = layer2;
            ArtActivity.btn_layers.setImageResource(R.drawable.ic_baseline_layer2);
        } else if (layer == 2) {
            layer++;
            path = layer3;
            ArtActivity.btn_layers.setImageResource(R.drawable.ic_baseline_layer3);
        } else if (layer == 3) {
            layer = 1;
            path = layer1;
            ArtActivity.btn_layers.setImageResource(R.drawable.ic_baseline_layer1);
        }
    }
    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }
    public int getDrawingColor() {
        return paintLine.getColor();
    }
    public void redo() {
        if (undo.size() > 0) {
            path.add(undo.remove(undo.size() - 1));
            invalidate();
        } else {
            Snackbar.make(this, "Nothing to redo !", Snackbar.LENGTH_SHORT).show();
        }
    }
    public void undo() {
        if (path.size() > 0) {
            undo.add(path.remove(path.size() - 1));
            invalidate();
        } else {
            Snackbar.make(this, "Nothing to undo in this layer !", Snackbar.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("WrongThread")
    public void saveImage() {
        ContextWrapper cw = new ContextWrapper(getContext());
        String filename = "TempART" + System.currentTimeMillis();
        // path to /data/data/your app/app_data/imageDir
        File directory = cw.getDir("files", Context.MODE_PRIVATE);
        pathString = cw.getDir("files", Context.MODE_PRIVATE).toString();
        // create imageDir
        File myPath = new File(directory, filename + ".jpg");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(myPath);
            //Use the compress method on the BitMap object to write image to the OutputStream
//            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            draw(canvas);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, filename , "made with TempArt");
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

        imageSaved = true;
    }

    // Scale a bitmap preserving the aspect ratio.
    public Bitmap scale(Bitmap bitmap, int maxWidth, int maxHeight) {
        // Determine the constrained dimension, which determines both dimensions.
        int width;
        int height;
        float widthRatio = (float) bitmap.getWidth() / maxWidth;
        float heightRatio = (float) bitmap.getHeight() / maxHeight;
        // Width constrained.
        if (widthRatio >= heightRatio) {
            width = maxWidth;
            height = (int) (((float) width / bitmap.getWidth()) * bitmap.getHeight());
        }
        // Height constrained.
        else {
            height = maxHeight;
            width = (int) (((float) height / bitmap.getHeight()) * bitmap.getWidth());
        }
        Bitmap scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        float ratioX = (float) width / bitmap.getWidth();
        float ratioY = (float) height / bitmap.getHeight();
        float middleX = width / 2.0f;
        float middleY = height / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }
}
