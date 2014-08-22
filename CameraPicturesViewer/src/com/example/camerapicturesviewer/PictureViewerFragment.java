package com.example.camerapicturesviewer;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

public class PictureViewerFragment extends Fragment implements TwoWayAdapterView.OnItemClickListener {
    public static final String FRAGMENT_TAG = PictureViewerFragment.class.getSimpleName();
    private TwoWayGridView pictureGridView;
    private ProgressBar imageLoadingProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_picture_viewer, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        pictureGridView = (TwoWayGridView) view.findViewById(R.id.pictures_grid);
        imageLoadingProgressBar = (ProgressBar) view.findViewById(R.id.image_loading_progress_bar);

        pictureGridView.setAdapter(new PictureAdapter(getActivity(), imageLoadingProgressBar));
        pictureGridView.setOnItemClickListener(this);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int orientation = display.getOrientation();
        int pictureWidth = 0;
        int pictureHeight = 0;
        Rect rectangle = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int contentViewTop = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int statusBar = contentViewTop - rectangle.top;
        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                pictureWidth = (int) (display.getWidth() / 3.5);
                pictureGridView.setNumRows(2);
                pictureHeight = (display.getHeight() - statusBar) / 2;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                pictureWidth = (int) (display.getWidth() / 2.5);
                pictureGridView.setNumRows(4);
                pictureHeight = (display.getHeight() - statusBar) / 4;
                break;
        }

        pictureGridView.setColumnWidth(pictureWidth);
        pictureGridView.setRowHeight(pictureHeight);
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
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
