package com.necromyd.tempart.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.necromyd.tempart.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class ArtView extends View {

    public static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintScreen;
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;

    public ArtView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        paintScreen = new Paint();

        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setColor(Color.BLACK);
        paintLine.setStrokeWidth(1);
        paintLine.setStrokeCap(Paint.Cap.ROUND);

        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);

        for(Integer key: pathMap.keySet()){
            canvas.drawPath(pathMap.get(key), paintLine);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getActionMasked(); // event type
        int actionIndex = event.getActionIndex(); // pointer (finger , mouse)

        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP){
            touchStarted(event.getX(actionIndex),
                    event.getY(actionIndex),
                    event.getPointerId(actionIndex));
        }else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP){
            touchEnded(event.getPointerId(actionIndex));
        }else{
            touchMoved(event);
        }

        invalidate(); // redraw the screen
        return true;
    }

    private void touchMoved(MotionEvent event){

        for (int i = 0; i < event.getPointerCount(); i++){

            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            if (pathMap.containsKey(pointerId)){
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerId);

                //Calculate how far the user moved from the last update

                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                //if the distance is significant enough to be considered movement
                if(deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE){
                    // move the path to the new location
                    if(event.getX() < 0 | event.getX() > bitmapCanvas.getWidth()){
                        break;
                    }
                    path.quadTo(point.x , point.y,
                            (newX + point.x)/2,
                            (newY + point.y)/2);

                    //store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }


    public void setDrawingColor(int color){
        paintLine.setColor(color);
    }

    public int getDrawingColor(){
        return paintLine.getColor();
    }

    public void setLineWidth(int width){
        paintLine.setStrokeWidth(width);
    }

    public int getLineWidth(){
        return (int) paintLine.getStrokeWidth();
    }

    public void clear(){
        pathMap.clear(); // removes all of the paths
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate(); // refresh the screen
    }
    private void touchEnded(int pointerId){
        Path path = pathMap.get(pointerId); // get corresponding path
        bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas
        path.reset();
    }
    private void touchStarted(float x, float y, int pointerId){
        Path path; // Store the path for given touch
        Point point; // Store the last point in path

        if (pathMap.containsKey(pointerId)){
            path = pathMap.get(pointerId);
            point = previousPointMap.get(pointerId);
        }else {
            path = new Path();
            pathMap.put(pointerId, path);
            point = new Point();
            previousPointMap.put(pointerId, point);
        }

        //move to the coordinates of the touch
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

   public void saveImage() {
       ContextWrapper cw = new ContextWrapper(getContext());
       String filename = "TempART" + System.currentTimeMillis();
       // path to /data/data/yourapp/app_data/imageDir
       File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
       // create imageDir
       File myPath = new File(directory, filename + ".jpg");

       FileOutputStream fileOutputStream = null;
       try {
           fileOutputStream = new FileOutputStream(myPath);
           //Use the compress method on the BitMap object to write image to the OutputStream
           bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
       }catch(Exception e){
           e.printStackTrace();
       }finally {
           try {
               fileOutputStream.flush();
               fileOutputStream.close();
               Log.d("Image:", directory.getAbsolutePath());
               Toast message = Toast.makeText(getContext(),"Image Saved +" + directory.getAbsolutePath(), Toast.LENGTH_LONG);
               message.setGravity(Gravity.CENTER, message.getXOffset() / 2 , message.getYOffset() / 2);
               message.show();
           }catch (IOException e){
               e.printStackTrace();
           }
       }
   }

   private void loadImage(String path) {
        try {
            File file = new File(path, "profile.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
//            ImageView imageView = (ImageView)findViewById(R.id.imageViewSelect);
//            imageView.setImageBitmap(bitmap);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
   }

}
