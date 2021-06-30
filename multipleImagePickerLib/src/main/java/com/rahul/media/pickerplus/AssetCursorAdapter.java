/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2013 Chute
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.rahul.media.pickerplus;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rahul.media.R;
import com.rahul.media.imagemodule.ImageGalleryPickerActivityPlus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import com.facebook.drawee.view.SimpleDraweeView;


public class AssetCursorAdapter extends CursorAdapter implements OnScrollListener, AssetSelectListener {

    public static final String TAG = AssetCursorAdapter.class.getSimpleName();

    private static LayoutInflater inflater = null;
    //	public ImageLoader loader;
    private final int dataIndex;
    public Map<Integer, String> tick;
    public boolean showSelectedOnly = false;
    public ArrayList<String> checkedPathsList = new ArrayList<String>();
    Context ctx;
    int availableCount = 1;
    private boolean shouldLoadImages = true;

    @SuppressWarnings("deprecation")
    public AssetCursorAdapter(Context context, Cursor c, boolean showSelectedOnly, int availableCount, ArrayList<String> paths) {
        super(context, c);
        this.ctx = context;
        this.availableCount = availableCount;
        this.showSelectedOnly = showSelectedOnly;
        if (paths != null) {
            this.checkedPathsList = paths;
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //loader = ImageLoader.getLoader(context);
        dataIndex = c.getColumnIndex(MediaStore.Images.Media.DATA);
        tick = new HashMap<Integer, String>();
        ((ImageGalleryPickerActivityPlus) ctx).setAssetSelectListener(this);

    }

    public static String convertNumbersToEnglish(String str) {
        String answer = str;
        answer = answer.replace("١", "1");
        answer = answer.replace("٢", "2");
        answer = answer.replace("٣", "3");
        answer = answer.replace("٤", "4");
        answer = answer.replace("٥", "5");
        answer = answer.replace("٦", "6");
        answer = answer.replace("٧", "7");
        answer = answer.replace("٨", "8");
        answer = answer.replace("٩", "9");
        answer = answer.replace("٠", "0");
        return answer;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();
        String path;

        path = cursor.getString(dataIndex); //convertNumbersToEnglish(cursor.getString(dataIndex));
        //holder.imageViewThumb.setTag(path);
        holder.imageViewTick.setTag(cursor.getPosition());
        if (shouldLoadImages) {
            Log.i("images", "path:" + path);
            //holder.imageViewThumb.setImageURI(Uri.fromFile(new File(path)));
            Glide.with(context).load(Uri.fromFile(new File(path))).into(holder.imageViewThumb);
            //loader.displayImage(Uri.fromFile(new File(path)).toString(), holder.imageViewThumb, null);
        }
        if (!showSelectedOnly) {
            if (tick.containsKey(cursor.getPosition())) {
                holder.imageViewTick.setVisibility(View.VISIBLE);
                view.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
            } else {

                holder.imageViewTick.setVisibility(View.GONE);
                view.setBackgroundColor(context.getResources().getColor(R.color.gray_light));
            }
        } else {
            holder.imageViewTick.setVisibility(View.VISIBLE);
        }


        if (checkedPathsList != null && checkedPathsList.size() > 0) {
            if (checkedPathsList.contains(path)) {

                holder.imageViewTick.setVisibility(View.VISIBLE);
                setTick(cursor.getPosition());
            }
        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder;
        View vi = inflater.inflate(R.layout.gc_adapter_assets, null);
        holder = new ViewHolder();
        holder.imageViewThumb = vi.findViewById(R.id.gcImageViewThumb);
        holder.imageViewTick = vi.findViewById(R.id.gcImageViewTick);
        vi.setTag(holder);
        return vi;
    }

    @Override
    public String getItem(int position) {
        final Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return cursor.getString(dataIndex);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // Do nothing

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_FLING:
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                shouldLoadImages = false;
                break;
            case OnScrollListener.SCROLL_STATE_IDLE:
                shouldLoadImages = true;
                notifyDataSetChanged();
                break;
        }
    }

    public ArrayList<String> getSelectedFilePaths() {
        final ArrayList<String> photos = new ArrayList<String>();
        final Iterator<String> iterator = tick.values().iterator();
        while (iterator.hasNext()) {
            photos.add(iterator.next());
        }
        return photos;
    }

    public boolean hasSelectedItems() {
        return tick.size() > 0;
    }

    public int getSelectedItemsCount() {
        return checkedPathsList != null ? checkedPathsList.size() : tick.size();
    }

    public void setTick(int position) {
        if (showSelectedOnly) {
            checkedPathsList.add(tick.get(position));
        } else if (getCount() > position) {

            if (!tick.containsValue(getItem(position)))
                tick.put(position, getItem(position));
            if (!checkedPathsList.contains(getItem(position)))
                checkedPathsList.add(getItem(position));
        }
    }

    public void toggleTick(int position, View view) {

        if (showSelectedOnly) {
            String lpath = getItem(position);
            checkedPathsList.remove(lpath);
            for (Map.Entry<Integer, String> entry : tick.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(lpath)) {
                    tick.remove(entry.getKey());
                    break;
                }
            }
        } else if (getCount() > position) {
            if (tick.containsKey(position)) {
                String path = tick.get(position);
                checkedPathsList.remove(path);
//                if(paths != null && paths.size() > 0 && paths.contains(path)) {
//                    paths.remove(path);
//                }
                tick.remove(position);

            } else {
                if (getSelectedItemsCount() >= availableCount) {
                    Toast.makeText(ctx, ctx.getString(R.string.image_scount_limit) + " ," + availableCount, Toast.LENGTH_LONG).show();
                    return;
                }
                tick.put(position, getItem(position));
                checkedPathsList.add(getItem(position));
            }
            ImageView thick = view.findViewById(R.id.gcImageViewTick);
            if (tick.containsKey(position)) {
                Log.i("thick", "View.VISIBLE");
                thick.setVisibility(View.VISIBLE);
            } else {
                Log.i("thick", "View.GONE");
                thick.setVisibility(View.GONE);

            }
            //this.notifyDataSetChanged();
        }
    }

    @Override
    public ArrayList<Integer> getSelectedItemPositions() {
        final ArrayList<Integer> positions = new ArrayList<Integer>();
        final Iterator<Integer> iterator = tick.keySet().iterator();
        while (iterator.hasNext()) {
            positions.add(iterator.next());
        }
        return positions;
    }

    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();

    }

    public static class ViewHolder {
        public ImageView imageViewThumb;
        public ImageView imageViewTick;
    }

}
