package com.example.camerapicturesviewer;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class PictureViewerFragment extends Fragment implements OnItemClickListener {
    public static final String FRAGMENT_TAG = PictureViewerFragment.class.getSimpleName();
    private GridView pictureGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_picture_viewer, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        pictureGridView = (GridView) view.findViewById(R.id.picture_grid_view);
        pictureGridView.setAdapter(new PictureAdapter(getActivity()));
        pictureGridView.setOnItemClickListener(this);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int orientation = display.getOrientation();
        int pictureWidth = 0;
        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                pictureWidth = (int) (display.getWidth() / 3.5);
                pictureGridView.setNumColumns((int) (pictureGridView.getAdapter().getCount() / 2));
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                pictureWidth = (int) (display.getWidth() / 2.5);
                pictureGridView.setNumColumns((int) (pictureGridView.getAdapter().getCount() / 4));
                break;
        }
        pictureGridView.setHorizontalScrollBarEnabled(true);
        pictureGridView.setVerticalScrollBarEnabled(false);
        pictureGridView.setColumnWidth(pictureWidth);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse("file://" + ((File) (pictureGridView.getAdapter().getItem(position))).getPath()), "image/*");
        startActivity(intent);

    }
}
