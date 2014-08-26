package com.example.camerapicturesviewer;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;

public class PictureViewerActivity extends FragmentActivity implements TwoWayAdapterView.OnItemClickListener {

    public static final String ACTIVITY_TAG = PictureViewerActivity.class.getSimpleName();

    private TwoWayGridView pictureGridView;

    @Override
    protected void onCreate(Bundle retainInstance) {
        super.onCreate(retainInstance);

        setContentView(R.layout.a_picture_viewer);
        pictureGridView = (TwoWayGridView) findViewById(R.id.pictures_grid);
        pictureGridView.setAdapter(new PictureAdapter(this));
        pictureGridView.setOnItemClickListener(this);
        pictureGridView.setEmptyView(findViewById(android.R.id.empty));
        int orientation = getResources().getConfiguration().orientation;
        int cellSize = 0;
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                pictureGridView.setNumRows(2);
                cellSize = (getScreenHeight() - getStatusBarHeight()) / 2;
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                pictureGridView.setNumRows(4);
                cellSize = (getScreenHeight() - getStatusBarHeight()) / 4;
                break;
        }
    }

    @Override
    public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + pictureGridView.getAdapter().getItem(position)), "image/*");
        startActivity(intent);

    }

    private int getStatusBarHeight() {
        Rect rectgle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        int StatusBarHeight = rectgle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int statusBar = contentViewTop + StatusBarHeight;
        return statusBar;
    }

    private int getScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        return screenHeight;
    }

}
