package com.example.camerapicturesviewer;

import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.jess.ui.TwoWayGridView;

public class PictureAdapter extends BaseAdapter {
    public static final String ADAPTER_TAG = PictureAdapter.class.getSimpleName();
    private String[] pictures;
    private Context context;
    File DCIMDirectory;

    public PictureAdapter(Context c) {
        DCIMDirectory = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera/");
        pictures = DCIMDirectory.list();
        context = c;
        if (pictures == null) {
            Toast.makeText(context, context.getString(R.string.empty_dir), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public int getCount() {
        if (pictures != null)
            return pictures.length;
        else
            return 0;
    }

    @Override
    public Object getItem(int index) {
        return DCIMDirectory.getPath() + "/" + pictures[index];
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
            pictureView.setLayoutParams(new TwoWayGridView.LayoutParams(pictureHeight, pictureWidth));
            pictureView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        } else
            pictureView = (ImageView) convertView;
        try {
            if (pictures[position].substring(pictures[position].lastIndexOf("."),
                    pictures[position].lastIndexOf(".") + 4).equals(".jpg")) {
                Bitmap cameraPictureThumbnail = getThumbnail(context.getContentResolver(), DCIMDirectory.getPath()
                        + "/" + pictures[position]);
                Bitmap rotatedThumbnail = null;
                Matrix matrix = new Matrix();
                ExifInterface exif = new ExifInterface(DCIMDirectory.getPath() + "/" + pictures[position]);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        rotatedThumbnail = Bitmap.createBitmap(cameraPictureThumbnail, 0, 0,
                                cameraPictureThumbnail.getWidth(), cameraPictureThumbnail.getHeight(), matrix, true);
                        Log.d(ADAPTER_TAG, "orientation 90");
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        rotatedThumbnail = Bitmap.createBitmap(cameraPictureThumbnail, 0, 0,
                                cameraPictureThumbnail.getWidth(), cameraPictureThumbnail.getHeight(), matrix, true);
                        Log.d(ADAPTER_TAG, "orientation 180");
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        rotatedThumbnail = Bitmap.createBitmap(cameraPictureThumbnail, 0, 0,
                                cameraPictureThumbnail.getWidth(), cameraPictureThumbnail.getHeight(), matrix, true);
                        Log.d(ADAPTER_TAG, "orientation 270");
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                        Log.d(ADAPTER_TAG, "orientation normal");
                        rotatedThumbnail = cameraPictureThumbnail;
                        break;
                    case ExifInterface.ORIENTATION_UNDEFINED:
                        Log.d(ADAPTER_TAG, "orientation undefined");
                        rotatedThumbnail = cameraPictureThumbnail;
                        break;

                }

                pictureView.setImageBitmap(rotatedThumbnail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pictureView;
    }
}
