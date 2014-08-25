package com.example.camerapicturesviewer;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class PictureViewerActivity extends FragmentActivity implements TwoWayAdapterView.OnItemClickListener {
    public static final String ACTIVITY_TAG = PictureViewerActivity.class.getSimpleName();
    private TwoWayGridView pictureGridView;

    private int getStatusBarHeight() {
        Rect rectgle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        int StatusBarHeight = rectgle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int statusBar = contentViewTop + StatusBarHeight;
        return statusBar;
    }

    @Override
    protected void onCreate(Bundle retainInstance) {
        super.onCreate(retainInstance);

        setContentView(R.layout.a_picture_viewer);
        pictureGridView = (TwoWayGridView) findViewById(R.id.pictures_grid);
        pictureGridView.setAdapter(new PictureAdapter(this));
        pictureGridView.setOnItemClickListener(this);
        pictureGridView.setEmptyView(findViewById(android.R.id.empty));
        Display display = getWindowManager().getDefaultDisplay();
        int orientation = getResources().getConfiguration().orientation;
        int pictureWidth = 0;
        int pictureHeight = 0;
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                pictureWidth = (int) (display.getWidth() / 3.5);
                pictureGridView.setNumRows(2);
                pictureHeight = (display.getHeight() - getStatusBarHeight()) / 2;
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                pictureWidth = (int) (display.getWidth() / 2.5);
                pictureGridView.setNumRows(4);
                pictureHeight = (display.getHeight() - getStatusBarHeight()) / 4;
                break;
        }

        pictureGridView.setColumnWidth(pictureWidth);
        pictureGridView.setRowHeight(pictureHeight);

    }

    @Override
    public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + ((String) (pictureGridView.getAdapter().getItem(position)))),
                "image/*");
        startActivity(intent);

    }

}
