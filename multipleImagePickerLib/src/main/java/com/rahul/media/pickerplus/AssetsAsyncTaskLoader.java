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
import android.util.Log;


/**
 * The {@link AssetsAsyncTaskLoader} class is an AsyncTaskLoader subclass that
 * loads photos that can be found on the device.
 */
public class AssetsAsyncTaskLoader extends
        AbstractSingleDataInstanceAsyncTaskLoader<Cursor> {

    public static final String TAG = AssetsAsyncTaskLoader.class
            .getSimpleName();
    String id = "";

    public AssetsAsyncTaskLoader(Context context, String id) {
        super(context);
        this.id = id;
    }

    @Override
    public Cursor loadInBackground() {
        Log.i("AssetsAsyncTaskLoader", "id:" + id);
        if (id.equals("0")) {
            return MediaDAO.getAllMediaPhotos(getContext());

        }
        return MediaDAO.getAllMediaPhotosFromID(getContext(), id);

    }

    @Override
    public void deliverResult(Cursor data) {
        super.deliverResult(data);
    }

}
