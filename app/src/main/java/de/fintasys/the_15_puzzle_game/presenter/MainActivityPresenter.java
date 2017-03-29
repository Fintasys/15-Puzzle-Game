package de.fintasys.the_15_puzzle_game.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;


import java.util.ArrayList;

import de.fintasys.the_15_puzzle_game.interfaces.IMainView;


public class MainActivityPresenter {

    private final String TAG = "MainActivityPresenter";

    private IMainView mView;

    public MainActivityPresenter(IMainView view) {
        this.mView = view;
    }

    /**
     * Takes a URI and return the real path of the file
     * @param uri
     * @return real path
     */
    public String getRealPath(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        CursorLoader cursorLoader = new CursorLoader(mView.getContext(), uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Check for Permission
     */
    public void checkForPermissions() {
        // Permission Request API 23
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ArrayList<String> permissions = new ArrayList<>();

            boolean granted = true;
            if (ContextCompat.checkSelfPermission(mView.getContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                permissions.add(Manifest.permission.INTERNET);
            }
            if (ContextCompat.checkSelfPermission(mView.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if(granted == false) {
                ActivityCompat.requestPermissions((Activity) mView.getContext(), permissions.toArray(new String[0]), 0);
            }
        }
    }

}
