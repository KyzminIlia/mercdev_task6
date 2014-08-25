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

    @Override
    protected void onCreate(Bundle retainInstance) {
        super.onCreate(retainInstance);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.a_picture_viewer);
        pictureGridView = (TwoWayGridView) findViewById(R.id.pictures_grid);
        pictureGridView.setAdapter(new PictureAdapter(this));
        pictureGridView.setOnItemClickListener(this);
        Display display = getWindowManager().getDefaultDisplay();
        int orientation = getResources().getConfiguration().orientation;
        int pictureWidth = 0;
        int pictureHeight = 0;
        Rect rectangle = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int statusBar = contentViewTop - rectangle.top;

        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                pictureWidth = (int) (display.getWidth() / 3.5);
                pictureGridView.setNumRows(2);
                pictureHeight = (display.getHeight() - statusBar) / 2;
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                pictureWidth = (int) (display.getWidth() / 2.5);
                pictureGridView.setNumRows(4);
                pictureHeight = (display.getHeight() - statusBar) / 4;
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
