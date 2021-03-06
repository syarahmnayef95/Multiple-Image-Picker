package com.rahul.media.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.rahul.media.R;
import com.rahul.media.model.CustomGallery;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rahul on 6/5/2016.
 */

public class ImagePreviewAdapter extends PagerAdapter {

    private final Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<CustomGallery> dataT;

    public ImagePreviewAdapter(Context mContext, HashMap<String, CustomGallery> dataT) {
        this.mContext = mContext;
        this.dataT = new ArrayList<>(dataT.values());
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void customNotify(HashMap<String, CustomGallery> dataHashmap) {
        dataT.clear();
        ArrayList<CustomGallery> dataT2 = new ArrayList<>(dataHashmap.values());
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

        final ImageView imageView = itemView.findViewById(R.id.full_screen_image);
        //imageView.setImageURI(Uri.parse("file://" + dataT.get(position).sdcardPath));
        Glide.with(mContext).load(Uri.parse("file://" + dataT.get(position).sdcardPath)).into(imageView);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
