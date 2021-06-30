package com.rahul.media.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rahul.media.R;
import com.rahul.media.gestures.MoveGestureDetector;
import com.rahul.media.gestures.RotateGestureDetector;
import com.rahul.media.utils.MediaUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HidePlateActivity extends AppCompatActivity implements View.OnTouchListener {
    private ImageView img_scale;
    private ImageView img_base;
    private float mScaleFactor = 0.5f;
    private float mRotationDegree = 0.f;
    private float mFocusX = 0.f;
    private float mFocusY = 0.f;
    private int mScreenHeight;
    private int mScreenWidth;
    private Matrix matrix = new Matrix();//Các lớp Matrix giữ một ma trận 3x3 để di chuyển tọa độ.
    private int mImageWidth, mImageHeight;
    private ScaleGestureDetector mScaleDetector;
    private RotateGestureDetector mRotateDetector;
    private MoveGestureDetector mMoveDetector;
//    private LinearLayout base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_plate);

//        TextView backTitle = (TextView) findViewById(R.id.backTitle);
//
//        backTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setResult(RESULT_CANCELED);
//                finish();
//            }
//        });


        img_scale =  findViewById(R.id.img_scale);
        img_base =  findViewById(R.id.img);
//        base = findViewById(R.id.base);

        if(getIntent().hasExtra("picture")){
            if(getIntent().getStringExtra("picture") != null){

                Glide.with(this).load(Uri.parse("file://" + getIntent().getStringExtra("picture"))).into(img_base);

                //img_base.setImageURI(Uri.parse("file://" + getIntent().getStringExtra("picture")));


            }
        }

        Button button = findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             save();

            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        img_scale.setOnTouchListener(this);
        //// Lấy kích thước màn hình bằng pixel.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;

        //load anh mau
        Bitmap loadTempImg = BitmapFactory.decodeResource(getResources(), R.drawable.blur);
        mImageHeight = loadTempImg.getHeight();
        mImageWidth = loadTempImg.getWidth();
        img_scale.setImageBitmap(loadTempImg);
        //view anh thu nho lai boi ma tran so voi anh goc
        matrix.postScale(mScaleFactor, mScaleFactor);
        float scaleImageCenterX = (mImageWidth) / 2;
        float scaleImageCenterY = (mImageHeight) / 2;

        matrix.postTranslate(((mScreenWidth)  - scaleImageCenterX) / 2,((mScreenHeight) - scaleImageCenterY) / 2);
        mFocusX = mScreenWidth / 2 ;
        mFocusY = mScreenHeight / 2;
        img_scale.setImageMatrix(matrix);

        // Thiết lập Detectors Gesture
        mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
        mRotateDetector = new RotateGestureDetector(getApplication(), new RotateListener());
        mMoveDetector = new MoveGestureDetector(getApplication(), new MoveListener());

        //قم بتحريك المستطيل على على لوحة السيارة المراد اخفاءها علما بانه يمكنك تحريك وتحجيم وتدوير المستطيل باستخدام اللمس

        boolean isShowed = getBooleanPreferences(HidePlateActivity.this,"hide_plate_isShowed",false);
        if(!isShowed) {
            try {

                addBooleanPreferences(HidePlateActivity.this,"hide_plate_isShowed",true);
                AlertDialog.Builder builder = new AlertDialog.Builder(HidePlateActivity.this);
                builder.setMessage(Html.fromHtml("اخفي لوحة السيارة ،قم بتحريك المستطيل على لوحة السيارة المراد اخفاءها. علما بانه يمكنك تحريك وتدوير وتغير حجم المستطيل باستخدام اللمس"))
                        .setCancelable(true)
                        .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

    }

    public static void addBooleanPreferences(Context context, String key,
                                             boolean value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

    public static boolean getBooleanPreferences(Context context, String key, boolean def) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, def);
    }


    private void save() {

        BitmapDrawable drawable = (BitmapDrawable) img_base.getDrawable();
        Bitmap bmp1 = drawable.getBitmap();

        BitmapDrawable drawable2 = (BitmapDrawable) img_scale.getDrawable();
        Bitmap bmp2 = drawable2.getBitmap();
        //Bitmap bmp2 = Bitmap.createBitmap(bmp,0,0,bmp.getWidth() ,bmp.getHeight() ,matrix,true);//(bmp2.getWidth() * img_base.getWidth()) / bmp1.getWidth(),(bmp2.getHeight() * img_base.getHeight()) / bmp1.getHeight()
        //bmp2.setHasAlpha(true);

        //Bitmap bmp2 = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
        //bmp2.setHasAlpha(true);


//        String path = getFilesDir().getPath();
        OutputStream fOut = null;

        try {
            //File file = new File(path, "saved"+counter+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            File file = MediaUtility.createImageFile2(HidePlateActivity.this);
            fOut = new FileOutputStream(file);

            Bitmap pictureBitmap = overlay(bmp1, bmp2);
            Bitmap pictureBitmapMerged = resizeBitmap(pictureBitmap,bmp1.getWidth(),bmp1.getHeight());
            pictureBitmapMerged.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            //pictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

            Intent intent = new Intent();
            intent.putExtra("plate_picture",file.getAbsolutePath());

            setResult(RESULT_OK,intent);
            finish();
            //MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

//            Intent intent = new Intent(HidePlateActivity.this, PreviewActivity.class);
//            intent.putExtra("picture", file.getAbsolutePath());
//            startActivity(intent);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
    }

    public Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {

        View content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);

        int reqHigh = content.getHeight();//this.getWindow().getDecorView().getHeight();
        int reqWidth = content.getWidth(); //this.getWindow().getDecorView().getWidth();

        Bitmap bmOverlay = Bitmap.createBitmap(reqWidth, reqHigh, bmp1.getConfig());

        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(resizeBitmap(bmp1,reqWidth,reqHigh),0,0, null);

        //Bitmap rotated = rotateImage(bmp2,mRotationDegree);
        Bitmap bmp = Bitmap.createBitmap(bmp2,0,0,bmp2.getWidth() ,bmp2.getHeight() ,matrix,true);//(bmp2.getWidth() * img_base.getWidth()) / bmp1.getWidth(),(bmp2.getHeight() * img_base.getHeight()) / bmp1.getHeight()
        bmp.setHasAlpha(true);
        //Bitmap mask = Bitmap.createScaledBitmap(bmp,bmp.getWidth() + 450,bmp.getHeight() + 170
        //        ,true);//(bmp2.getWidth() * img_base.getWidth()) / bmp1.getWidth(),(bmp2.getHeight() * img_base.getHeight()) / bmp1.getHeight()
        //(img_base.getWidth() - rotated.getWidth() ) + rotated.getWidth(),((img_base.getHeight() - rotated.getHeight()) + rotated.getHeight()) / 2
        //mask.setHasAlpha(true);
        // Canvas canvasmask = new Canvas(mask);
       // canvasmask.drawBitmap(mask, new Matrix(), null);
       // rotated.setHeight(rotated.getHeight() - (img_scale.getHeight() - bmp1.getHeight()));

        /*Rect rect = new Rect();
        rect.left = img_scale.getLeft();
        rect.top = img_scale.getTop();
        rect.bottom = img_scale.getBottom();
        rect.right = img_scale.getRight();*/
        //canvas.drawBitmap(rotated,rect,rect,null);
       // Bitmap mask = Bitmap.createBitmap(rotated.getWidth() , rotated.getHeight() , bmOverlay.getConfig());

        RectF rectF = getImageBounds(img_scale);
        float top = rectF.top;
        float left = rectF.left;
        canvas.drawBitmap(bmp,left,top , null);
        return bmOverlay;
    }

    private RectF getImageBounds(ImageView imageView) {
        RectF bounds = new RectF();
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            imageView.getImageMatrix().mapRect(bounds, new RectF(drawable.getBounds()));
        }
        return bounds;
    }

    Bitmap resizeBitmap(Bitmap originalImage, int width, int height) {

        Bitmap background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        background.setHasAlpha(true);
        float originalWidth = originalImage.getWidth();
        float originalHeight = originalImage.getHeight();

        Canvas canvas = new Canvas(background);

        float scale = width / originalWidth;

        float xTranslation = 0.0f;
        float yTranslation = (height - originalHeight * scale) / 2.0f;

        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(originalImage, transformation, paint);

        return background;
    }

    public Bitmap rotateImage(Bitmap src, float degree)
    {
        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        Bitmap bitmap = Bitmap.createBitmap((src), 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        bitmap.setHasAlpha(true);
        //bitmap.setConfig(Bitmap.Config.ARGB_8888);
        return bitmap;
    }

    public Bitmap scaleImage(Bitmap src)
    {
        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postScale(mScaleFactor,mScaleFactor);
        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        bitmap.setHasAlpha(true);
        //bitmap.setConfig(Bitmap.Config.ARGB_8888);
        return bitmap;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 1.0f));
            return true;
        }
    }

    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            mRotationDegree -= detector.getRotationDegreesDelta();
            return true;
        }
    }

    private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {
        @Override
        public boolean onMove(MoveGestureDetector detector) {
            PointF d = detector.getFocusDelta();
            mFocusX += d.x;
            mFocusY += d.y;
            //isFirst = false;
            return true;
        }
    }

    //boolean isFirst = true;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        mRotateDetector.onTouchEvent(event);
        mMoveDetector.onTouchEvent(event);
        float scaleImageCenterX = (mImageWidth * mScaleFactor) / 2;
        float scaleImageCenterY = (mImageHeight * mScaleFactor) / 2;

        matrix.reset();//Thiết lập ma trận để tính
        matrix.postScale(mScaleFactor, mScaleFactor);//Bài concats ma trận với quy mô quy định.
        matrix.postRotate(mRotationDegree, scaleImageCenterX, scaleImageCenterY);//Postconcats ma trận với vòng xoay cảng quy định.
        matrix.postTranslate(mFocusX - scaleImageCenterX, mFocusY - scaleImageCenterY);//Postconcats ma trận với các dịch quy định.


        ImageView view = (ImageView) v;
        view.setImageMatrix(matrix);
        return true;
    }
}
