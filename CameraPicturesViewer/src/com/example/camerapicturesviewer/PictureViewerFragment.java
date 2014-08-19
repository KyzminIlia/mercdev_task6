package com.example.camerapicturesviewer;

import java.io.File;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PictureViewerFragment extends Fragment implements TwoWayAdapterView.OnItemClickListener {
    public static final String FRAGMENT_TAG = PictureViewerFragment.class.getSimpleName();
    private PictureAdapter pictureAdapter;
    private TwoWayGridView pictureGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_picture_viewer, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        pictureGridView = (TwoWayGridView) view.findViewById(R.id.pictures_grid);
        pictureGridView.setAdapter(new PictureAdapter(getActivity()));
        pictureGridView.setOnItemClickListener(this);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int orientation = display.getOrientation();
        int pictureWidth = 0;
        int count = 0;
        int filesCount = 0;
        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                pictureWidth = (int) (display.getWidth() / 3.5);
                filesCount = pictureGridView.getAdapter().getCount();
                count = (int) Math.ceil(((double) pictureGridView.getAdapter().getCount() / 2));
                pictureGridView.setNumRows(2);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                pictureWidth = (int) (display.getWidth() / 2.5);
                filesCount = pictureGridView.getAdapter().getCount();
                count = (int) Math.ceil(((double) pictureGridView.getAdapter().getCount() / 4));
                pictureGridView.setNumRows(4);
                break;
        }

        pictureGridView.setColumnWidth(pictureWidth);
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        pictureAdapter = new PictureAdapter(getActivity());
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
