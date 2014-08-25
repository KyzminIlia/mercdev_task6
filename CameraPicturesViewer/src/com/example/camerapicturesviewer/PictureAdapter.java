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
    private File DCIMDirectory;
    private int pictureWidth = 0;
    private int pictureHeight = 0;

    public void checkForImages() {

        pictures = new ArrayList<String>();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].substring(fileList[i].lastIndexOf("."), fileList[i].lastIndexOf(".") + 4).equals(".jpg")) {
                pictures.add(fileList[i]);

            }
        }

    }

    public PictureAdapter(Context c) {
        DCIMDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        DCIMDirectory = new File(DCIMDirectory.getPath(), "/Camera");

        fileList = DCIMDirectory.list();
        context = c;
        if (fileList == null) {
            Toast.makeText(context, context.getString(R.string.empty_dir), Toast.LENGTH_LONG).show();
        }
        checkForImages();
        if (pictures.get(0) == null) {
            Toast.makeText(context, context.getString(R.string.empty_dir), Toast.LENGTH_LONG).show();
        }

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
        return DCIMDirectory.getPath() + "/" + pictures.get(index);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView pictureView;
        String pictureDir;
        pictureView = (ImageView) convertView;
        pictureDir = DCIMDirectory.getAbsolutePath() + "/" + pictures.get(position);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        int screenOrientation = context.getResources().getConfiguration().orientation;

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
