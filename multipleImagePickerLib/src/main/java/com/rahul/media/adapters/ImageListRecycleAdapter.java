package com.rahul.media.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rahul.media.R;
import com.rahul.media.model.CustomGallery;

import java.util.ArrayList;
import java.util.HashMap;

public class ImageListRecycleAdapter extends RecyclerView.Adapter<ImageListRecycleAdapter.VerticalItemHolder> {

    private final Context mContext;
    public ArrayList<CustomGallery> mItems;

    public int selectedThumb = 0;

    private AdapterView.OnItemClickListener mOnItemClickListener;

    public ImageListRecycleAdapter(Context context, HashMap<String, CustomGallery> imagesUri) {
        mContext = context;
        this.mItems = new ArrayList<>(imagesUri.values());
    }

    public void removeItem(int position) {
        if (position >= mItems.size()) return;
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void customNotify(HashMap<String, CustomGallery> dataT) {
        mItems.clear();
        ArrayList<CustomGallery> dataT2 = new ArrayList<CustomGallery>(dataT.values());
        this.mItems.addAll(dataT2);
        notifyDataSetChanged();
    }

    @Override
    public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View root = inflater.inflate(R.layout.image_strip_view, container, false);

        return new VerticalItemHolder(root, this);
    }

    @Override
    public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
        CustomGallery item = mItems.get(position);
        itemHolder.setImage(item.sdcardPath);
        if (position == selectedThumb) {
            itemHolder.imageView.setSelected(true);
        } else {
            itemHolder.imageView.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(VerticalItemHolder itemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    class VerticalItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private ImageListRecycleAdapter mAdapter;

        VerticalItemHolder(View itemView, ImageListRecycleAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);

            mAdapter = adapter;
            imageView = itemView.findViewById(R.id.strip_image);
        }

        @Override
        public void onClick(View v) {
            mAdapter.onItemHolderClick(this);
        }

        public void setImage(final String url) {
            //imageView.setImageURI(Uri.parse("file://" + url));
            Glide.with(mContext).load(Uri.parse("file://" + url)).into(imageView);

        }
    }

}