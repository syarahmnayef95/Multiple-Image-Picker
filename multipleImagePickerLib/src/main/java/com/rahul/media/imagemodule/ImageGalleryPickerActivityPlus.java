package com.rahul.media.imagemodule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.msupport.MSupport;
import com.msupport.MSupportConstants;
import com.rahul.media.R;
import com.rahul.media.model.Album;
import com.rahul.media.model.Define;
import com.rahul.media.pickerplus.AssetCursorAdapter;
import com.rahul.media.pickerplus.AssetSelectListener;
import com.rahul.media.pickerplus.AssetsAsyncTaskLoader;
import com.rahul.media.pickerplus.CursorFilesListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ahmed on 8/13/2016.
 */
public class ImageGalleryPickerActivityPlus extends AppCompatActivity {


    public AssetCursorAdapter cursorAssetAdapter;
    int imageSize = 24 * 1024 * 1024;
    private AssetSelectListener assetSelectListener;
    private Album a;
    private int pickCount;
    private GridView gridView;
    private View emptyView;
    private ArrayList<Integer> selectedItemsPositions;
    private CursorFilesListener cursorListener;

    private TextView backTitle;
    private ArrayList<String> paths;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(this, ImagePipelineConfig.newBuilder(this).setDownsampleEnabled(true).build());
        a = (Album) getIntent().getSerializableExtra("album");
        pickCount = getIntent().getIntExtra("pickCount", 1);


        if (getIntent().getExtras().containsKey("paths")) {
            paths = getIntent().getStringArrayListExtra("paths");
        }

        setContentView(R.layout.gc_fragment_assets);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setTitle(a.bucketname);

        backTitle = findViewById(R.id.backTitle);
        backTitle.setText(a.bucketname);
        backTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        gridView = findViewById(R.id.gcGridViewAssets);
        emptyView = findViewById(R.id.gc_empty_view_layout);
        gridView.setEmptyView(emptyView);

        gridView.setNumColumns(getResources().getInteger(R.integer.grid_columns_assets));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isStoragePermissionGiven = MSupport.checkPermissionWithRationale(ImageGalleryPickerActivityPlus.this,
                    null, MSupportConstants.READ_EXTERNAL_STORAGE, MSupportConstants.REQUEST_STORAGE_READ_WRITE);
            if (isStoragePermissionGiven)
                startLoadImages();
        } else
            startLoadImages();


    }

    private void startLoadImages() {
        getSupportLoaderManager().initLoader(1, null, new AssetsLoaderCallback(false));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_ok) {
            if (cursorAssetAdapter == null) return true;
            ArrayList<String> path = new ArrayList<>();
            path.addAll(cursorAssetAdapter.checkedPathsList);
            if (path.isEmpty()) {
                Snackbar.make(gridView, Html.fromHtml("<font color=\"#ffffff\">" + getString(R.string.msg_no_slected) + "</font>"), Snackbar.LENGTH_SHORT).show();

            } else {
                Intent i = new Intent();
                i.putStringArrayListExtra(Define.INTENT_PATH, path);
                setResult(RESULT_OK, i);
                finish();
            }
            return true;
        } else if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void setAssetSelectListener(AssetSelectListener adapterListener) {
        this.assetSelectListener = adapterListener;
    }

    private final class AssetsLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        boolean showOnlySelected;

        public AssetsLoaderCallback(boolean showOnlySelected) {
            super();
            this.showOnlySelected = showOnlySelected;

        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            return new AssetsAsyncTaskLoader(getApplicationContext(), a.bucketid + "");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor == null) {
                return;
            }

            if (cursorAssetAdapter != null) {
                HashMap<Integer, String> oldtick = new HashMap<Integer, String>();
                oldtick.putAll(cursorAssetAdapter.tick);

                if (showOnlySelected) {
                    MatrixCursor lselectedImagesCursor = new MatrixCursor(new String[]{"_id", "_data"});
                    for (String str : cursorAssetAdapter.checkedPathsList)
                        lselectedImagesCursor.addRow(new String[]{"1", str});

                    cursorAssetAdapter = new AssetCursorAdapter(ImageGalleryPickerActivityPlus.this, lselectedImagesCursor, true, pickCount, paths);
                    cursorAssetAdapter.tick.putAll(oldtick);
                } else {
                    cursorAssetAdapter = new AssetCursorAdapter(ImageGalleryPickerActivityPlus.this, cursor, false, pickCount, paths);

                }
                gridView.setAdapter(cursorAssetAdapter);
            } else {
                cursorAssetAdapter = new AssetCursorAdapter(ImageGalleryPickerActivityPlus.this, cursor, false, pickCount, paths);
                gridView.setAdapter(cursorAssetAdapter);
            }
            if (cursorAssetAdapter.getCount() == 0) {
                emptyView.setVisibility(View.GONE);
            }

            if (selectedItemsPositions != null) {
                for (int position : selectedItemsPositions) {
                    cursorAssetAdapter.setTick(position);
                }
            }

            try {
                backTitle.setText("حدد" + "(" + cursorAssetAdapter.checkedPathsList.size() + ")");

            } catch (Exception e) {
                e.printStackTrace();
            }
            gridView.setOnItemClickListener(new OnMultiSelectGridItemClickListener());
            // NotificationUtil.showPhotosAdapterToast(getActivity().getApplicationContext(),
            // cursorAssetAdapter.getCount());

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // TODO Auto-generated method stub

        }

    }

    /*
     * CURSOR ADAPTER CLICK LISTENERS
     */
    private final class OnMultiSelectGridItemClickListener implements AdapterView.OnItemClickListener {

        @SuppressLint("NewApi")
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {


            String imgPath = (String) parent.getAdapter().getItem(position);
            if (!TextUtils.isEmpty(imgPath)) {
                File f = new File(imgPath);
                if (f.length() > imageSize) {
                    Toast.makeText(view.getContext(), getString(R.string.image_size_limit), Toast.LENGTH_LONG).show();
                    return;
                }

                if (f.length() <= 0) {
                    Toast.makeText(view.getContext(), "الصورة غير صالحة", Toast.LENGTH_LONG).show();
                    return;
                }


            }
            cursorAssetAdapter.toggleTick(position, view);
//            getSupportActionBar().setTitle(a.bucketname+"("+cursorAssetAdapter.checkedPathsList.size()+")");
            //backTitle.setText(a.bucketname+"("+cursorAssetAdapter.checkedPathsList.size()+")");
            backTitle.setText("حدد" + "(" + cursorAssetAdapter.checkedPathsList.size() + ")");

        }
    }

    private final class CancelClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            finish();
        }

    }

    private final class OkClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!cursorAssetAdapter.getSelectedFilePaths().isEmpty()) {
                cursorListener.onDeliverCursorAssets(cursorAssetAdapter.getSelectedFilePaths());
            }
        }
    }
}
