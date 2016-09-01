package com.example.android.measureit;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static final int SELECTED_IMAGE=1;
    private static final int CAMERA_INTENT=1888;
    ImageView iv;
    Bitmap mutable;
    private static int numLines=0;
    private String s;
    float downx=0;
    float downy=0;
    float upx=0;
    float upy=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView)findViewById(R.id.imageView);
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //if(numLines<=2) {
                //    numLines++;
                    return onImageTouch(view, motionEvent);
                //}
                //return false;
            }
        });
    }
    public class MyCanvas extends View{
        public MyCanvas(Context context){
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas){
            super.onDraw(canvas);
            Paint painter=new Paint();
            painter.setColor(Color.WHITE);
            canvas.drawRect(0,0,512,512,painter);
            Paint pText=new Paint();
            pText.setColor(Color.BLACK);
            pText.setTextSize(20);
            canvas.drawText(s,100,100,pText);
        }
    }



    public void onGallery(View view){
        boolean result=Utility.checkPermission(MainActivity.this);
        if(result)
        {
            galleryIntent();
        }
    }


    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECTED_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECTED_IMAGE:
                if (resultCode==RESULT_OK){
                    Uri uri=data.getData();
                    String[]projection={MediaStore.Images.Media.DATA};
                    Cursor cursor=getContentResolver().query(uri,projection,null,null,null);
                    cursor.moveToFirst();
                    int columnIndex=cursor.getColumnIndex(projection[0]);
                    String filePath=cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap selectedImage= BitmapFactory.decodeFile(filePath);
                    //Drawable drawable=new BitmapDrawable(getResources(),selectedImage);
                    mutable=selectedImage.copy(Bitmap.Config.ARGB_8888,true);//converts bitmap to mutable for use in canvas
//                    iv.setImageBitmap(mutable);
//                    canvas=new Canvas(mutable);
                    /*View v=new MyCanvas(getApplicationContext());
                    Canvas canvas=new Canvas(mutable);
                    v.draw(canvas);*/
                    iv.setImageBitmap(mutable);
                }
                break;
        }
    }
    private boolean onImageTouch(View view,MotionEvent motionEvent)
    {

        int action=motionEvent.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                downx=motionEvent.getX();
                downy=motionEvent.getY();
                View v=new MyCanvas(getApplicationContext());
                Canvas canvas=new Canvas(mutable);
                s=String.valueOf(downx)+" "+String.valueOf(downy);
                v.draw(canvas);
                iv.setImageBitmap(mutable);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                upx=motionEvent.getX();
                upy=motionEvent.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return false;
    }
//    private boolean onImageTouch(View view,MotionEvent motionEvent){
//        View v=new MyCanvas(getApplicationContext());
//        Canvas canvas=new Canvas(mutable);
//        s="Srinivas";
//        v.draw(canvas);
//        iv.setImageBitmap(mutable);
//        return false;
//    }
}
