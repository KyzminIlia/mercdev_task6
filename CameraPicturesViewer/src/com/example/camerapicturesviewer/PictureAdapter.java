package com.example.camerapicturesviewer;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jess.ui.TwoWayGridView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PictureAdapter extends BaseAdapter {
    public static final String ADAPTER_TAG = PictureAdapter.class.getSimpleName();

    private String[] fileList;
    private List<String> pictures;

    private Context context;

    private int pictureWidth = 0;
    private int pictureHeight = 0;

    public PictureAdapter(Context c) {

        fileList = getDCIMDirectory().list();
        context = c;
        checkForImages();
    }

    public void checkForImages() {

        pictures = new ArrayList<String>();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].substring(fileList[i].lastIndexOf("."), fileList[i].lastIndexOf(".") + 4).equals(".jpg")) {
                pictures.add(fileList[i]);

            }
        }

    }

    public File getDCIMDirectory() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath(), "/Camera");
    }

    @Override
    public int getCount() {
        if (pictures != null)
            return pictures.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int index) {
        return getDCIMDirectory().getPath() + "/" + pictures.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView pictureView = (ImageView) convertView;
        String pictureDir = getDCIMDirectory().getAbsolutePath() + "/" + pictures.get(position);
        int screenOrientation = context.getResources().getConfiguration().orientation;
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        switch (screenOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                pictureWidth = (int) (display.getWidth() / 3.5);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                pictureWidth = (int) (display.getWidth() / 2.5);
                break;
        }
        pictureHeight = pictureWidth;
        if (pictureView == null) {
            pictureView = new ImageView(context);
        }
        Log.d(ADAPTER_TAG, "try to decode file " + pictureDir);
        pictureView.setImageBitmap(null);
        Picasso.with(context).load(new File(pictureDir)).noFade().resize(pictureWidth, pictureHeight).centerCrop()
                .into(pictureView);

        return pictureView;
    }
}
