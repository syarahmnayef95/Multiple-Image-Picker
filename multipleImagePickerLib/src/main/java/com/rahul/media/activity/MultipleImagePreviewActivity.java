package com.rahul.media.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.msupport.MSupport;
import com.msupport.MSupportConstants;
import com.rahul.media.R;
import com.rahul.media.adapters.ImageListRecycleAdapter;
import com.rahul.media.imagemodule.ImageAlbumListActivity;
import com.rahul.media.model.CustomGallery;
import com.rahul.media.model.Define;
import com.rahul.media.utils.MediaUtility;
import com.rahul.media.utils.ViewPagerSwipeLess;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rahul on 22/5/15.
 */
public class MultipleImagePreviewActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 200;
    private static final int ACTION_REQUEST_CAMERA = 201;
    private static final int PLATE_HIDE = 233;

    RecyclerView mRecycleView;
    ArrayList<String> cameraPaths = new ArrayList<>();
    private AlertDialog alertDialog;
    private ViewPagerSwipeLess mPager;
    private HashMap<String, CustomGallery> dataT;
    private CustomPagerAdapter adapter;
    private ImageListRecycleAdapter mImageListAdapter;
    private boolean isEnableHidePlate;
    private boolean isCrop;
    private boolean isCamera;
    private Uri userPhotoUri;
    private int pickCount;
    private boolean enableCamera;
    private TextView backTitle;
    private ArrayList<String> paths;
    private View root_layout;

    private boolean isCameraButtonClicked = false;

    public void showAlertDialog(Context mContext, String text) {

        alertDialog = new AlertDialog.Builder(mContext)
                .setMessage(text)
                .setCancelable(false).
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent data2 = new Intent();
        setResult(RESULT_CANCELED, data2);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Fresco.shutDown();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiimage_preview);
        mPager = findViewById(R.id.pager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Define.ACTIONBAR_COLOR);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        backTitle = findViewById(R.id.backTitle);
        root_layout = findViewById(R.id.root_layout);

        backTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent data = new Intent();
                setResult(RESULT_CANCELED, data);
                finish();
            }
        });

        dataT = new HashMap<>();
        adapter = new CustomPagerAdapter(dataT);
        mPager.setAdapter(adapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectThump(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mImageListAdapter = new ImageListRecycleAdapter(this, dataT);
        mRecycleView = findViewById(R.id.image_hlistview);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecycleView.setAdapter(mImageListAdapter);
        mImageListAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPager.setCurrentItem(position);
                //     view.setSelected(true);
            }
        });

        try {
            isCrop = getIntent().getExtras().getBoolean("crop");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            isEnableHidePlate = getIntent().getExtras().getBoolean("isEnableHidePlate");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getIntent().getExtras().containsKey("pickCount")) {
            pickCount = getIntent().getIntExtra("pickCount", 1);
        }

        if (getIntent().getExtras().containsKey("enableCamera")) {
            enableCamera = getIntent().getBooleanExtra("enableCamera", false);
        }

        if (getIntent().getExtras().containsKey("paths")) {

            paths = getIntent().getStringArrayListExtra("paths");

            deleteImage();

            if (paths != null && !paths.isEmpty()) {

                paths.addAll(cameraPaths);

                if (pickCount == 1) {
                    dataT.clear();
                    new ProcessAllImages(paths).execute();
                } else {
                    new ProcessAllImages(paths).execute();

                }
//                    adapter.customNotify(dataT);
//                    mImageListAdapter.customNotify(dataT);
            }
        }

        isCamera = getIntent().getExtras().getBoolean("IsCamera");
        if (isCamera) {
            isCameraButtonClicked = true;
            openCamera(false);
        } else {
            openGallery();
        }


    }

    private void selectThump(int position) {
        for (int i = 0; i < mRecycleView.getChildCount(); i++) {
            boolean checked = false;
            if (i == position) {
                checked = true;
            }
            View v = mRecycleView.getChildAt(i);
            //SimpleDraweeView imageView = v.findViewById(R.id.strip_image);
            ((ImageListRecycleAdapter) mRecycleView.getAdapter()).selectedThumb = position;
            //imageView.setSelected(checked);
            mRecycleView.getAdapter().notifyDataSetChanged();

        }
    }

    private void openGallery() {
        Intent i = new Intent(this, ImageAlbumListActivity.class);
        i.putExtra("pickCount", pickCount);
        i.putExtra("enableCamera", enableCamera);
        if (paths != null) {
            i.putExtra("paths", paths);
        }
        startActivityForResult(i, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_REQUEST_CAMERA) {

                if (pickCount == 1) {
                    dataT.clear();
                }

                if (userPhotoUri != null) {
                    ArrayList<String> Path = new ArrayList<>();

                    Path.add(userPhotoUri.getPath());

                    if (paths == null)
                        paths = new ArrayList<>();

                    for (String p : Path) {
                        if (!paths.contains(p)) {
                            paths.add(p);
                        }
                    }
                    for (String p : Path) {
                        if (!cameraPaths.contains(p)) {
                            cameraPaths.add(p);
                        }
                    }
                    new ProcessAllImages(Path).execute();
                }

            } else if (requestCode == PICK_IMAGE) {

                deleteImage();

                ArrayList<String> allPath = data.getStringArrayListExtra(Define.INTENT_PATH);

                paths = new ArrayList<>();
                paths.addAll(allPath);
                for (String camp : cameraPaths) {
                    if (!paths.contains(camp)) {
                        paths.add(camp);
                    }
                }
//                ArrayList<String> deletedArray = paths;
//                deletedArray.removeAll(allPath);
//                for(String path : deletedArray){
//                    deleteImage(path);
//                }

//                for (String p : allPath) {
//                    if (!paths.contains(p)) {
//                        paths.add(p);
//                    }
//                }


                if (paths != null && !paths.isEmpty()) {
                    if (pickCount == 1) {
                        dataT.clear();
                        new ProcessAllImages(paths).execute();
                    } else {
                        new ProcessAllImages(paths).execute();

                    }
//                    adapter.customNotify(dataT);
//                    mImageListAdapter.customNotify(dataT);
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                try {
                    Uri mTargetImageUri = null;// (Uri) data.getExtras().get(MediaStore.EXTRA_OUTPUT);
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        mTargetImageUri = result.getUri();
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                    }
                    if (mTargetImageUri != null) {
                        String imagePath = mImageListAdapter.mItems.get(mPager.getCurrentItem()).sdcardPath;
                        CustomGallery item = new CustomGallery();
                        item.sdcardPath = mTargetImageUri.getPath();
                        item.sdCardUri = mTargetImageUri;

                        dataT.remove(imagePath);
                        dataT.put(mTargetImageUri.getPath(), item);
                        if (paths != null) {
                            if (paths.contains(imagePath)) {
                                paths.remove(imagePath);
                                paths.add(mTargetImageUri.getPath());
                            }
                        }
                        if (cameraPaths != null) {
                            if (cameraPaths.contains(imagePath)) {
                                cameraPaths.remove(imagePath);
                                cameraPaths.add(mTargetImageUri.getPath());
                            }
                        }
                        adapter.customNotify(dataT);
                        mImageListAdapter.customNotify(dataT);
                    }
                } catch (Exception e) {
                    String invalidImageText = (String) data.getExtras().get("invalid_image");
                    if (invalidImageText != null)
                        showAlertDialog(MultipleImagePreviewActivity.this, invalidImageText);
                }
            } else if (requestCode == PLATE_HIDE) {
                try {
                    Uri mTargetImageUri = null;// (Uri) data.getExtras().get(MediaStore.EXTRA_OUTPUT);
                    if (resultCode == RESULT_OK) {
                        mTargetImageUri = Uri.parse(data.getStringExtra("plate_picture"));
                    }
                    if (mTargetImageUri != null) {
                        String imagePath = mImageListAdapter.mItems.get(mPager.getCurrentItem()).sdcardPath;
                        CustomGallery item = new CustomGallery();
                        item.sdcardPath = mTargetImageUri.getPath();
                        item.sdCardUri = mTargetImageUri;

                        dataT.remove(imagePath);
                        dataT.put(mTargetImageUri.getPath(), item);
                        if (paths != null) {
                            if (paths.contains(imagePath)) {
                                paths.remove(imagePath);
                                paths.add(mTargetImageUri.getPath());
                            }
                        }
                        if (cameraPaths != null) {
                            if (cameraPaths.contains(imagePath)) {
                                cameraPaths.remove(imagePath);
                                cameraPaths.add(mTargetImageUri.getPath());
                            }
                        }
                        adapter.customNotify(dataT);
                        mImageListAdapter.customNotify(dataT);
                    }
                } catch (Exception e) {
                    String invalidImageText = (String) data.getExtras().get("invalid_image");
                    if (invalidImageText != null)
                        showAlertDialog(MultipleImagePreviewActivity.this, invalidImageText);
                }
            }
        } else if (resultCode == 334) {
            isCameraButtonClicked = true;
            openCamera(false);
        } else {
            if (dataT != null && dataT.size() > 0) {
            } else {
                Intent data2 = new Intent();
                setResult(RESULT_CANCELED, data2);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        menu.findItem(R.id.action_crop).setVisible(isCrop);
        menu.findItem(R.id.action_hide).setVisible(isEnableHidePlate);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ok) {
            if (dataT.size() > pickCount) {
                //Toast.makeText(this, "الحد الأقصى لرفع الصور هو 10 صور", Toast.LENGTH_SHORT).show();
                Toast.makeText(MultipleImagePreviewActivity.this, getString(R.string.image_scount_limit) + " ," + pickCount, Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }
            ArrayList<CustomGallery> mArrayList = new ArrayList<>(dataT.values());
            if (mArrayList.size() > 0) {
                ArrayList<String> allPath = new ArrayList<>();
                for (int i = 0; i < mArrayList.size(); i++) {
                    allPath.add(mArrayList.get(i).sdcardPath);
                }

                Intent data = new Intent().putStringArrayListExtra(Define.INTENT_PATH, allPath);
                setResult(RESULT_OK, data);
                finish();
            }
        } else if (id == android.R.id.home) {
            Intent data = new Intent();
            setResult(RESULT_CANCELED, data);
            finish();
        } else if (id == R.id.action_camera) {
            if (dataT.size() >= pickCount) {
                //Toast.makeText(this, "الحد الأقصى لرفع الصور/ة هو "+ pickCount +" صور/ة", Toast.LENGTH_SHORT).show();
                Toast.makeText(MultipleImagePreviewActivity.this, getString(R.string.image_scount_limit) + " ," + pickCount, Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }
            if (adapter != null && adapter.getCount() >= pickCount) {
                //Toast.makeText(this, "الحد الأقصى لرفع الصور/ة هو "+ pickCount +" صور/ة", Toast.LENGTH_SHORT).show();
                Toast.makeText(MultipleImagePreviewActivity.this, getString(R.string.image_scount_limit) + " ," + pickCount, Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }
            isCameraButtonClicked = true;
            openCamera(false);
        } else if (id == R.id.action_gallery) {
            if (dataT.size() >= pickCount) {
                //Toast.makeText(this, "الحد الأقصى لرفع الصور/ة هو "+ pickCount +" صور/ة", Toast.LENGTH_SHORT).show();
                Toast.makeText(MultipleImagePreviewActivity.this, getString(R.string.image_scount_limit) + " ," + pickCount, Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }
            if (adapter != null && adapter.getCount() >= pickCount) {
                //Toast.makeText(this, "الحد الأقصى لرفع الصور/ة هو "+ pickCount +" صور/ة", Toast.LENGTH_SHORT).show();
                Toast.makeText(MultipleImagePreviewActivity.this, getString(R.string.image_scount_limit) + " ," + pickCount, Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }
            openGallery();
        } else if (id == R.id.action_crop) {
            if (adapter != null && adapter.getCount() > 0) {
                String imagePath = mImageListAdapter.mItems.get(mPager.getCurrentItem()).sdcardPath;

                //Uri destination = null;
                try {
                    //   destination = MediaUtility.createImageFile(MultipleImagePreviewActivity.this);
                    CropImage.activity(Uri.parse("file://" + imagePath))
                            .setAllowCounterRotation(true)
                            .setAllowRotation(true)
                            .setMultiTouchEnabled(true)
                            .start(this);
                    /*Crop.of((Uri.parse("file://" + imagePath)), destination).
                            asSquare().start(MultipleImagePreviewActivity.this);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (id == R.id.action_hide) {
            if (adapter != null && adapter.getCount() > 0) {
                String imagePath = mImageListAdapter.mItems.get(mPager.getCurrentItem()).sdcardPath;

                //Uri destination = null;
                try {
                    //   destination = MediaUtility.createImageFile(MultipleImagePreviewActivity.this);
                    Intent intent = new Intent(MultipleImagePreviewActivity.this, HidePlateActivity.class);
                    intent.putExtra("picture", imagePath);
                    startActivityForResult(intent, PLATE_HIDE);
                    /*Crop.of((Uri.parse("file://" + imagePath)), destination).
                            asSquare().start(MultipleImagePreviewActivity.this);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (id == R.id.action_delete) {
            if (adapter != null && adapter.getCount() > 0) {
                String imagePath = mImageListAdapter.mItems.get(mPager.getCurrentItem()).sdcardPath;

                if (dataT != null) {
                    if (dataT.containsKey(imagePath)) {
                        dataT.remove(imagePath);
                    }
                }
                mImageListAdapter.mItems.remove(mPager.getCurrentItem());
                mImageListAdapter.notifyDataSetChanged();

                adapter.dataT.remove(mPager.getCurrentItem());
                adapter.notifyDataSetChanged();

                if (paths != null && paths.size() > 0 && paths.contains(imagePath)) {
                    paths.remove(imagePath);
                }

                if (cameraPaths != null && cameraPaths.size() > 0 && cameraPaths.contains(imagePath)) {
                    cameraPaths.remove(imagePath);
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteImage() {

        try {
            //if (adapter != null && adapter.getCount() > 0) {


            if (dataT != null) {
                dataT.clear();
            }

            if (mImageListAdapter != null) {
                mImageListAdapter.mItems.clear();
                mImageListAdapter.notifyDataSetChanged();
            }

            if (adapter != null) {
                adapter.dataT.clear();
                adapter.notifyDataSetChanged();
            }


//                if (paths != null && paths.size() > 0 && paths.contains(imagePath)) {
//                    paths.remove(imagePath);
//                }
//                if (dataT != null) {
//                    if (dataT.containsKey(imagePath)) {
//                        dataT.remove(imagePath);
//                    }
//                }
//
//                for (CustomGallery img : mImageListAdapter.mItems) {
//                    if (img.sdcardPath.equals(imagePath)) {
//                        mImageListAdapter.mItems.remove(img);
//                        mImageListAdapter.notifyDataSetChanged();
//                    }
//                }
//
//                for (CustomGallery img : adapter.dataT) {
//                    if (img.sdcardPath.equals(imagePath)) {
//                        adapter.dataT.remove(img);
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//
//
//                if (paths != null && paths.size() > 0 && paths.contains(imagePath)) {
//                    paths.remove(imagePath);
//                }
            //    }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCamera(boolean isPermission) {
        String[] permissionSet = {MSupportConstants.CAMERA};//{MSupportConstants.WRITE_EXTERNAL_STORAGE, MSupportConstants.CAMERA};
        if (isPermission) {
            try {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    userPhotoUri = MediaUtility.createImageFile(this);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, userPhotoUri);
                    startActivityForResult(takePictureIntent, ACTION_REQUEST_CAMERA);

                }
            } catch (Exception e) {
                showAlertDialog(this, "Device does not support camera.");
            }
        } else {
            boolean isCameraPermissionGranted;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // isCameraPermissionGranted = MSupport.checkMultiplePermission(this, permissionSet, MSupportConstants.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                isCameraPermissionGranted = MSupport.checkMultiplePermission(this, permissionSet, MSupportConstants.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);


            } else
                isCameraPermissionGranted = true;
            if (isCameraPermissionGranted) {
                try {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

     //               if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                        Uri imageFile = MediaUtility.createImageFile(MultipleImagePreviewActivity.this);


                        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            // only for gingerbread and newer versions
                            userPhotoUri = FileProvider.getUriForFile(MultipleImagePreviewActivity.this, "app.com.syarah.provider",
                                    new File(imageFile.getPath()));
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, userPhotoUri);
                            userPhotoUri = imageFile;
                        } else {
                            userPhotoUri = imageFile;
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, userPhotoUri);
                        }

//
//                        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                            // only for gingerbread and newer versions
//                            Uri imageFile = MediaUtility.createImageFile(MultipleImagePreviewActivity.this);
//                            userPhotoUri = FileProvider.getUriForFile(MultipleImagePreviewActivity.this, "app.com.syarah.provider",
//                                    new File(imageFile.getPath()));
//
//                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, userPhotoUri);
//                            userPhotoUri = imageFile;
//
//                        } else {
//
//                            userPhotoUri = MediaUtility.createImageFile(this);
//                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, userPhotoUri);
//                        }

                        startActivityForResult(takePictureIntent, ACTION_REQUEST_CAMERA);

                   // }
                } catch (Exception e) {

                    showAlertDialog(this, "Device does not support camera.");
                }

                /*
                  try {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    Uri imageFile = MediaUtility.createImageFile(CameraPickActivity.this);
                    userPhotoUri = FileProvider.getUriForFile(CameraPickActivity.this, Define.MEDIA_PROVIDER,
                            new File(imageFile.getPath()));

                    if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        // only for gingerbread and newer versions
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, userPhotoUri);
                        userPhotoUri = imageFile;
                    } else {
                        userPhotoUri = imageFile;
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, userPhotoUri);
                    }

                    startActivityForResult(takePictureIntent, ACTION_REQUEST_CAMERA);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlertDialog(CameraPickActivity.this, "Device does not support camera.");
            }
                 */
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MSupportConstants.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {

                boolean isAllGranted = true;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        isAllGranted = false;
                        break;
                    }
                }
                if (isAllGranted) {

                    for (int i = 0; i < grantResults.length; i++) {

                        if (permissions[i].toLowerCase().contains("camera") && isCameraButtonClicked) {
                            isCameraButtonClicked = false;
                            openCamera(false);
                            break;
                        }
                    }

                } else {
                    //Toast.makeText(ImageAlbumListActivity.this, "Storage permission not granted", Toast.LENGTH_SHORT).show();
                    boolean showRationale = true;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                        for (String permission : permissions) {


                            showRationale = shouldShowRequestPermissionRationale(permission);
                            if (!showRationale) {
                                break;
                            }
                        }
                    } else {
                        showRationale = false;
                    }

                    if (!showRationale) {
                        Snackbar snackbar = Snackbar
                                .make(root_layout, "الرجاء تمكين بعض الصلاحيات لكي تعمل هذه الميزة", Snackbar.LENGTH_LONG)
                                .setAction("فتح الإعدادات", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        final Intent i = new Intent();
                                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        i.addCategory(Intent.CATEGORY_DEFAULT);
                                        i.setData(Uri.parse("package:" + MultipleImagePreviewActivity.this.getPackageName()));
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                        MultipleImagePreviewActivity.this.startActivity(i);
                                    }
                                });

// Changing message text color
                        snackbar.setActionTextColor(Color.GREEN);

// Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = sbView.findViewById(R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        snackbar.show();
                        //finish();
                    }

                }
            }
        }
    }


    private class ProcessAllImages extends AsyncTask<Void, Void, Void> {

        private ArrayList<String> stringArrayList;
        private ProgressDialog mProgressDialog;

        ProcessAllImages(ArrayList<String> stringArrayList) {
            this.stringArrayList = stringArrayList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MultipleImagePreviewActivity.this);
            mProgressDialog.setMessage("Processing images ...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < stringArrayList.size(); i++) {
                CustomGallery item = new CustomGallery();

                item.sdcardPath = stringArrayList.get(i);
                item.sdCardUri = Uri.parse(stringArrayList.get(i));

                //     item.sdcardPath = BitmapDecoder.getBitmap(stringArrayList.get(i), MultipleImagePreviewActivity.this);
                item.sdCardUri = (Uri.parse(item.sdcardPath));

                if (!dataT.containsKey(item.sdcardPath))
                    dataT.put(item.sdcardPath, item);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.customNotify(dataT);
            mImageListAdapter.customNotify(dataT);
        }
    }

    private class CustomPagerAdapter extends PagerAdapter {

        LayoutInflater mLayoutInflater;
        ArrayList<CustomGallery> dataT;

        CustomPagerAdapter(HashMap<String, CustomGallery> dataT) {
            this.dataT = new ArrayList<CustomGallery>(dataT.values());
            mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        void customNotify(HashMap<String, CustomGallery> dataHashmap) {
            dataT.clear();
            ArrayList<CustomGallery> dataT2 = new ArrayList<CustomGallery>(dataHashmap.values());
            this.dataT.addAll(dataT2);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dataT.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.image_pager_item, container, false);

//
//            final SimpleDraweeView imageView = itemView.findViewById(R.id.full_screen_image);
            final ImageView imageView = itemView.findViewById(R.id.full_screen_image);

//            imageView.setImageURI(Uri.parse("file://" + dataT.get(position).sdcardPath));


//            final ImageView imageView = itemView.findViewById(R.id.full_screen_image);
//
            Glide.with(MultipleImagePreviewActivity.this)
                    .load(Uri.parse("file://" + dataT.get(position).sdcardPath))
                    .into(imageView);

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
