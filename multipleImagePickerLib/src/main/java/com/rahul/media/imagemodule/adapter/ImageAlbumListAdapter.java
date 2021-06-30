package com.rahul.media.imagemodule.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rahul.media.R;
import com.rahul.media.imagemodule.ImageGalleryPickerActivityPlus;
import com.rahul.media.model.Album;
import com.rahul.media.model.Define;

import java.util.ArrayList;
import java.util.List;


public class ImageAlbumListAdapter
        extends RecyclerView.Adapter<ImageAlbumListAdapter.ViewHolder> {

    private final int pickCount;
    private List<Album> albumList;
    private List<String> thumbList = new ArrayList<String>();
    private ArrayList<String> paths = new ArrayList<String>();
    private Context context;

    public ImageAlbumListAdapter(List<Album> albumList, int pickCount, ArrayList<String> paths,Context context) {
        this.albumList = albumList;
        this.pickCount = pickCount;
        this.paths = paths;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_item, parent, false);
        return new ViewHolder(view);
    }

    public void setThumbList(List<String> thumbList) {
        this.thumbList = thumbList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String thumbPath = "";
        if (thumbList != null && thumbList.size() > position)
            thumbPath = thumbList.get(position);

        loadImage(thumbPath, holder.imgAlbum);

        holder.areaAlbum.setTag(albumList.get(position));
        Album a = (Album) holder.areaAlbum.getTag();
        holder.albumNameTv.setText(albumList.get(position).bucketname);

        holder.albumCountTv.setText(Html.fromHtml("<b><font color='#03A9F4'>" + a.counter + "</font></b>" + "<font " +
                "color='#FFFFFF'> Media </font>"));

        holder.areaAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Album a = (Album) v.getTag();
                Intent i = new Intent(holder.areaAlbum.getContext(), ImageGalleryPickerActivityPlus.class);
                i.putExtra("album", a);
                i.putExtra("album_title", albumList.get(position).bucketname);
                i.putExtra("pickCount", pickCount);
                if (paths != null) {
                    i.putExtra("paths", paths);
                }
                ((Activity) holder.areaAlbum.getContext()).startActivityForResult(i, Define.ENTER_ALBUM_REQUEST_CODE);
            }
        });
    }

    private void loadImage(String thumbPath, final ImageView imgAlbum) {
        Glide.with(context).load(Uri.parse("file://" + thumbPath)).into(imgAlbum);
//        imgAlbum.setImageURI(Uri.parse("file://" + thumbPath));
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgAlbum;
        private TextView albumNameTv;
        private TextView albumCountTv;
        private LinearLayout areaAlbum;


        public ViewHolder(View view) {
            super(view);
            imgAlbum = view.findViewById(R.id.img_album);
            albumNameTv = view.findViewById(R.id.album_name);
            albumCountTv = view.findViewById(R.id.album_photos_count);
            areaAlbum = view.findViewById(R.id.area_album);
        }
    }


}


