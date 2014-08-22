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

public class PictureAdapter extends BaseAdapter {
    public static final String ADAPTER_TAG = PictureAdapter.class.getSimpleName();
    private String[] pictures;
    private Context context;
    private File DCIMDirectory;
    private List<String> downloadedPictures;
    private ImageView pictureView;
    private ProgressBar loadingImageProgressBar;

    public PictureAdapter(Context c, ProgressBar progressBar) {
        DCIMDirectory = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera/");
        pictures = DCIMDirectory.list();
        context = c;
        if (pictures == null) {
            Toast.makeText(context, context.getString(R.string.empty_dir), Toast.LENGTH_LONG).show();
        }
        downloadedPictures = new ArrayList<String>();
        loadingImageProgressBar = progressBar;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pictureWidth = 0;
        int pictureHeight = 0;
        ViewHolder holder = new ViewHolder();
        LoadImageAsyncTask loadImage = new LoadImageAsyncTask();
        Rect rectangle = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int contentViewTop = ((Activity) context).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int statusBar = contentViewTop - rectangle.top;

        if (convertView == null) {
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            int screenOrientation = display.getOrientation();

            switch (screenOrientation) {
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    pictureWidth = (int) (display.getWidth() / 3.5);

                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    pictureWidth = (int) (display.getWidth() / 2.5);

                    break;
            }
            pictureHeight = pictureWidth;
            pictureView = new ImageView(context);
            pictureView.setLayoutParams(new TwoWayGridView.LayoutParams(pictureWidth, pictureHeight));
            pictureView.setScaleType(ImageView.ScaleType.CENTER);
            pictureView.setPadding(0, 0, 0, 0);
            holder.picture = pictureView;
            pictureView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        pictureHeight = pictureWidth;
        String pictureDir = DCIMDirectory.getAbsolutePath() + "/" + pictures[position];

        loadImage.execute(pictureDir);
        try {

            holder.picture.setImageDrawable(loadImage.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return pictureView;
    }

    class LoadImageAsyncTask extends AsyncTask<String, Void, Drawable> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pictureView.setImageResource(R.drawable.gray_background_loaded);
            loadingImageProgressBar.setVisibility(ProgressBar.VISIBLE);

        }

        @Override
        protected Drawable doInBackground(String... params) {
            int pictureWidth = 0;
            int pictureHeight = 0;
            Drawable pictureDrawable = null;
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            int screenOrientation = display.getOrientation();

            switch (screenOrientation) {
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    pictureWidth = (int) (display.getWidth() / 3.5);

                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    pictureWidth = (int) (display.getWidth() / 2.5);

                    break;
            }
            pictureHeight = pictureWidth;
            if (params[0].substring(params[0].lastIndexOf("."), params[0].lastIndexOf(".") + 4).equals(".jpg")) {
                Options options = new BitmapFactory.Options();
                downloadedPictures.add(params[0]);
                options.inScaled = true;
                options.inDither = true;
                Log.d(ADAPTER_TAG, "try to decode file " + params[0]);
                WeakReference<Bitmap> scaledPicture = new WeakReference<Bitmap>(Bitmap.createScaledBitmap(
                        BitmapFactory.decodeFile(params[0], options), pictureWidth, pictureHeight, false));
                Bitmap rotatedPicture = null;
                Matrix matrix = new Matrix();
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(params[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        rotatedPicture = Bitmap.createBitmap(scaledPicture.get(), 0, 0, scaledPicture.get().getWidth(),
                                scaledPicture.get().getHeight(), matrix, true);
                        Log.d(ADAPTER_TAG, "orientation 90");
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        rotatedPicture = Bitmap.createBitmap(scaledPicture.get(), 0, 0, scaledPicture.get().getWidth(),
                                scaledPicture.get().getHeight(), matrix, true);
                        Log.d(ADAPTER_TAG, "orientation 180");
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        rotatedPicture = Bitmap.createBitmap(scaledPicture.get(), 0, 0, scaledPicture.get().getWidth(),
                                scaledPicture.get().getHeight(), matrix, true);
                        Log.d(ADAPTER_TAG, "orientation 270");
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                        Log.d(ADAPTER_TAG, "orientation normal");
                        rotatedPicture = scaledPicture.get();
                        break;
                    case ExifInterface.ORIENTATION_UNDEFINED:
                        Log.d(ADAPTER_TAG, "orientation undefined");
                        rotatedPicture = scaledPicture.get();
                        break;

                }

                pictureDrawable = new BitmapDrawable(context.getResources(), rotatedPicture);

            }
            return pictureDrawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            pictureView.setImageDrawable(result);
            loadingImageProgressBar.setVisibility(ProgressBar.INVISIBLE);
            super.onPostExecute(result);
        }

    }

    class ViewHolder {
        ImageView picture;
    }

}
