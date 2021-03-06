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
import android.util.DisplayMetrics;
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
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

public class PictureAdapter extends BaseAdapter {
    public static final String ADAPTER_TAG = PictureAdapter.class.getSimpleName();

    private String[] fileList;
    private List<String> pictures;

    private Context context;

    private int pictureSize = 0;
    private int pictureHeight = 0;

    public PictureAdapter(Context c) {

        fileList = getDCIMDirectory().list();
        context = c;
        if (fileList != null)
            checkForImages();
    }

    public void checkForImages() {

        pictures = new ArrayList<String>();
        for (String file : fileList) {
            if (file.substring(file.lastIndexOf("."), file.lastIndexOf(".") + 4).equals(".jpg")) {
                pictures.add(file);

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
        switch (screenOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                pictureSize = (int) ((getScreenHeight() - getStatusBarHeight()) / 2);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                pictureSize = (int) ((getScreenHeight() - getStatusBarHeight()) / 4);
                break;
        }
        if (pictureView == null) {
            pictureView = new ImageView(context);
        }
        Log.d(ADAPTER_TAG, "try to decode file " + pictureDir);
        pictureView.setImageBitmap(null);
        Picasso.with(context).load(new File(pictureDir)).noFade().resize(pictureSize, pictureSize).centerCrop()
                .placeholder(R.drawable.gray_background_loaded).into(pictureView);

        return pictureView;
    }

    private int getStatusBarHeight() {
        Rect rectgle = new Rect();
        Window window = ((Activity) context).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        int StatusBarHeight = rectgle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int statusBar = contentViewTop + StatusBarHeight;
        return statusBar;
    }

    private int getScreenHeight() {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        return screenHeight;
    }
}
