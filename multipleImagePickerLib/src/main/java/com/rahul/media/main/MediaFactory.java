package com.rahul.media.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.rahul.media.activity.MultipleImagePreviewActivity;
import com.rahul.media.model.Define;
import com.rahul.media.utils.MediaSingleTon;
import com.rahul.media.utils.MediaUtility;

import java.util.ArrayList;

/**
 * Class to initiate media picker
 * Created by rahul on 24/6/15.
 */
public class MediaFactory {

    public static final int MEDIA_REQUEST_CODE = 222;
    private static MediaFactory mMediaFactory;

    private MediaFactory() {
    }

    public static synchronized MediaFactory create() {
        if (mMediaFactory == null)
            mMediaFactory = new MediaFactory();
        return mMediaFactory;
    }

    /**
     * Method to clear cached images stored in sd card
     *
     * @param context
     */
    public void clearCache(Context context) {
        try {
            MediaUtility.initializeImageLoader(context).delete();
        } catch (Exception ignored) {
        }
    }

    /**
     * Starts the media picking functionality
     *
     * @param mediaBuilder MediaBuilder object
     * @return current instance of MediaFactory
     */

    public MediaFactory start(MediaBuilder mediaBuilder) {
        MediaSingleTon.getInstance();
        Intent intent;
        Bundle bundle = new Bundle();
        {
            bundle.putBoolean("isEnableHidePlate", mediaBuilder.isEnableHidePlate);
            bundle.putBoolean("crop", mediaBuilder.isCrop);
            bundle.putInt("pickCount", mediaBuilder.pickCount);
            bundle.putBoolean("enableCamera", mediaBuilder.enableCamera);
            bundle.putStringArrayList("paths", mediaBuilder.paths);

            if (mediaBuilder.fromGallery) {


                if (mediaBuilder.mFragment != null) {
                    intent = new Intent(mediaBuilder.mFragment.getContext(), MultipleImagePreviewActivity.class);
                    intent.putExtras(bundle);
                    intent.putExtra("IsCamera", false);
                    mediaBuilder.mFragment.startActivityForResult(intent, MEDIA_REQUEST_CODE);

                } else {
                    intent = new Intent(mediaBuilder.mContext, MultipleImagePreviewActivity.class);
                    intent.putExtra("IsCamera", false);
                    intent.putExtras(bundle);
                    ((Activity) mediaBuilder.mContext).startActivityForResult(intent, MEDIA_REQUEST_CODE);

                }

            } else {
                if (mediaBuilder.mFragment != null) {
                    intent = new Intent(mediaBuilder.mFragment.getContext(), MultipleImagePreviewActivity.class);
                    intent.putExtra("IsCamera", true);
                    intent.putExtras(bundle);
                    mediaBuilder.mFragment.startActivityForResult(intent, MEDIA_REQUEST_CODE);

                } else {
                    intent = new Intent(mediaBuilder.mContext, MultipleImagePreviewActivity.class);
                    intent.putExtra("IsCamera", true);
                    intent.putExtras(bundle);
                    ((Activity) mediaBuilder.mContext).startActivityForResult(intent, MEDIA_REQUEST_CODE);

                }

            }
        }

        return mMediaFactory;
    }

    public ArrayList<String> onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<String> all_path = new ArrayList<>();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MEDIA_REQUEST_CODE) {
                all_path = data.getStringArrayListExtra(Define.INTENT_PATH);
            }
        }
        return all_path;
    }

    public static class MediaBuilder {
        boolean isEnableHidePlate = false;
        boolean isCrop = false;
        boolean fromGallery = true;
        private Context mContext;
        private Fragment mFragment;
        private int pickCount = 1;
        private boolean enableCamera = false;
        private ArrayList<String> paths;

        public MediaBuilder(Context mContext) {
            this.mContext = mContext;
        }

        public MediaBuilder(Fragment Fragment) {
            this.mFragment = Fragment;
        }

        /**
         * Sets type of media to be taken from gallery
         *
         * @return current instance of MediaBuilder
         */
        public MediaBuilder enableHidePlate() {
            this.isEnableHidePlate = true;
            return this;
        }

        public MediaBuilder fromGallery() {
            this.fromGallery = true;
            return this;
        }

        /**
         * Sets type of media to be taken from camera
         *
         * @return current instance of MediaBuilder
         */
        public MediaBuilder fromCamera() {
            this.fromGallery = false;
            return this;
        }

        /**
         * Sets the cropping feature enabled or disabled.
         * Works only for camera image
         *
         * @return current instance of MediaBuilder
         */
        public MediaBuilder doCropping() {
            this.isCrop = true;
            return this;
        }

        public MediaBuilder setPickCount(int count) {
            if (count <= 0)
                count = 1;
            this.pickCount = count;
            return this;
        }

        public MediaBuilder setEnableCamera() {
            this.enableCamera = true;
            return this;
        }

        public MediaBuilder setPaths(ArrayList<String> paths) {
            this.paths = paths;
            return this;
        }
    }

}
