package com.example.camerapicturesviewer;

import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PictureAdapter extends BaseAdapter {
    private File[] pictures;
    private Context context;

    public PictureAdapter(Context c) {
        File DCIMDirectory = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera/");
        pictures = DCIMDirectory.listFiles();
        context = c;
    }

    @Override
    public int getCount() {
        return pictures.length;
    }

    @Override
    public Object getItem(int index) {
        return pictures[index];
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?",
                new String[] { path }, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }

        ca.close();
        return null;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView pictureView;
        if (convertView == null) {
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            int orientation = display.getOrientation();
            int pictureWidth = 0;
            int pictureHeight = 0;
            switch (orientation) {
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    pictureWidth = (int) (display.getWidth() / 3.5);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    pictureWidth = (int) (display.getWidth() / 2.5);
                    break;
            }
            pictureHeight = pictureWidth;
            pictureView = new ImageView(context);
            pictureView.setLayoutParams(new GridView.LayoutParams(pictureHeight, pictureWidth));
            pictureView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            pictureView.setPadding(0, 0, 0, 0);

        } else
            pictureView = (ImageView) convertView;
        try {
            pictureView
                    .setImageBitmap(getThumbnail(context.getContentResolver(), pictures[position].getAbsolutePath()));
        } catch (Exception e) {

            e.printStackTrace();
        }
        return pictureView;
    }
}
