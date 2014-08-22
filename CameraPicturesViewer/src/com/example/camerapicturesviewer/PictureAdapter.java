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
import com.squareup.picasso.Picasso;

public class PictureAdapter extends BaseAdapter {
	public static final String ADAPTER_TAG = PictureAdapter.class
			.getSimpleName();
	private String[] pictures;
	private Context context;
	private File DCIMDirectory;
	private List<String> downloadedPictures;
	private ImageView pictureView;
	private ProgressBar loadingImageProgressBar;

	public PictureAdapter(Context c) {
		DCIMDirectory = new File(Environment.getExternalStorageDirectory(),
				"DCIM/Camera/");
		pictures = DCIMDirectory.list();
		context = c;
		if (pictures == null) {
			Toast.makeText(context, context.getString(R.string.empty_dir),
					Toast.LENGTH_LONG).show();
		}
		downloadedPictures = new ArrayList<String>();

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
		Rect rectangle = new Rect();
		((Activity) context).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(rectangle);
		int contentViewTop = ((Activity) context).getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int statusBar = contentViewTop - rectangle.top;

		if (convertView == null) {
			Display display = ((Activity) context).getWindowManager()
					.getDefaultDisplay();
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
			pictureView.setLayoutParams(new TwoWayGridView.LayoutParams(
					pictureWidth, pictureHeight));
			pictureView.setScaleType(ImageView.ScaleType.CENTER);
			pictureView.setPadding(0, 0, 0, 0);
		} else {
			pictureView = (ImageView) convertView;
		}

		String pictureDir = DCIMDirectory.getAbsolutePath() + "/"
				+ pictures[position];
		Drawable pictureDrawable = null;
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
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
		if (pictureDir.substring(pictureDir.lastIndexOf("."),
				pictureDir.lastIndexOf(".") + 4).equals(".jpg")) {
			Log.d(ADAPTER_TAG, "try to decode file " + pictureDir);

			ExifInterface exif = null;
			try {
				exif = new ExifInterface(pictureDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				Picasso.with(context).load(new File(pictureDir))
						.resize(pictureWidth, pictureHeight).rotate(90)
						.centerCrop().into(pictureView);
				Log.d(ADAPTER_TAG, "orientation 90");
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				Picasso.with(context).load(new File(pictureDir))
						.resize(pictureWidth, pictureHeight).rotate(180)
						.centerCrop().into(pictureView);

				Log.d(ADAPTER_TAG, "orientation 180");
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				Picasso.with(context).load(new File(pictureDir))
						.resize(pictureWidth, pictureHeight).rotate(270)
						.centerCrop().into(pictureView);

				Log.d(ADAPTER_TAG, "orientation 270");
				break;
			case ExifInterface.ORIENTATION_NORMAL:
				Picasso.with(context).load(new File(pictureDir))
						.resize(pictureWidth, pictureHeight).rotate(0)
						.centerCrop().into(pictureView);

				break;
			case ExifInterface.ORIENTATION_UNDEFINED:
				Picasso.with(context).load(new File(pictureDir))
						.resize(pictureWidth, pictureHeight).rotate(0)
						.centerCrop().into(pictureView);
				break;

			}

		}

		return pictureView;
	}

}
