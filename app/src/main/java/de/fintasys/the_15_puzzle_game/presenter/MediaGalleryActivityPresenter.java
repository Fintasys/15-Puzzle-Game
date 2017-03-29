package de.fintasys.the_15_puzzle_game.presenter;

import android.util.Log;

import java.util.List;

import de.fintasys.the_15_puzzle_game.R;
import de.fintasys.the_15_puzzle_game.interfaces.IImageModel;
import de.fintasys.the_15_puzzle_game.interfaces.IMediaGalleryView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class MediaGalleryActivityPresenter {

    private final String TAG = "MediaGalleryPresenter";

    private IMediaGalleryView mView;
    private IImageModel mModel;

    public MediaGalleryActivityPresenter(IMediaGalleryView view, IImageModel model) {
        this.mView = view;
        this.mModel = model;
    }

    /**
     * This method initiates the Model
     * If the login was successful, it tries to get the users images
     */
    public void init() {
        mModel.init()
                .subscribeOn(AndroidSchedulers.mainThread())
                .singleOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String value) {
                        Log.d(TAG, "Login successful");
                        getMedia();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Show error
                        mView.showError(mView.getContext().getResources().getString(R.string.error_login_failed));
                    }
                });
    }

    /**
     * After Login was successful
     * this Method requests the users images from the Model.
     * If successful it will return a List of image urls
     */
    private void getMedia() {
        mModel.getUserImages()
                .subscribeOn(Schedulers.io())
                .singleOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<String>>() {
                    @Override
                    public void onSuccess(List<String> value) {
                        mView.updateMedia(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(mView.getContext().getResources().getString(R.string.error_get_media_failed));
                    }
                });
    }
}
