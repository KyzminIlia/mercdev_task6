package com.example.camerapicturesviewer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class PictureViewerActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle retainInstance) {
        super.onCreate(retainInstance);

        if (getViewerFragment() == null) {
            PictureViewerFragment photoFragment = new PictureViewerFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, photoFragment, PictureViewerFragment.FRAGMENT_TAG).commit();
        }
    }

    public PictureViewerFragment getViewerFragment() {
        return (PictureViewerFragment) getSupportFragmentManager()
                .findFragmentByTag(PictureViewerFragment.FRAGMENT_TAG);
    }
}
