package de.fintasys.the_15_puzzle_game.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.fintasys.the_15_puzzle_game.dialogs.WinDialog;
import de.fintasys.the_15_puzzle_game.game.GameFieldView;
import de.fintasys.the_15_puzzle_game.game.GameLogic;
import de.fintasys.the_15_puzzle_game.interfaces.IGameLogic;
import de.fintasys.the_15_puzzle_game.interfaces.IGameView;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;


public class GameActivityPresenter {

    private final static String TAG = "GameActivityPresenter";

    private IGameView mView;
    private Timer mTimer;
    private long mCurrentTime = 0;
    private Disposable mTimeObs;
    private int[] mStartingTiles;
    private String mUrl;

    private GameFieldView mGameFieldView;
    private IGameLogic mGameLogic;

    public GameActivityPresenter(IGameView view, GameFieldView gameFieldView) {
        this.mView = view;
        this.mGameFieldView = gameFieldView;
        this.mStartingTiles = new int[16];

        mGameLogic = new GameLogic();
        gameFieldView.init(mGameLogic);
    }

    /**
     * Starts the Timer for the Game
     * Ticks every second
     */
    public void startTimer() {
        if(mTimer != null)
            mTimer.cancel();
        else
            mTimer = new Timer();

        Observable<String> ob = new Observable<String>() {
            @Override
            protected void subscribeActual(final Observer<? super String> observer) {
                mTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        // Check if Game finished
                        if(mGameLogic.isSolved())
                            gameSolved();

                        mCurrentTime += 1000;
                        String timeDisplay = convertTime(mCurrentTime);
                        observer.onNext(timeDisplay);
                    }
                }, 1000, 1000);
            }
        };

        mTimeObs = ob
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.updateTime(s);
                    }
                });
    }

    /**
     * Converts milliseconds into a readable time string
     * @param milli
     * @return readable time 00:00:01
     */
    private String convertTime(long milli) {
        int seconds = (int) (milli / 1000) % 60 ;
        int minutes = (int) ((milli / (1000*60)) % 60);
        int hours   = (int) ((milli / (1000*60*60)) % 24);

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d",hours,minutes,seconds);
    }

    /**
     * Stops the timer when puzzle is solved
     * and makes view untouchable until reset
     */
    private void gameSolved() {
        mTimeObs.dispose();
        mGameFieldView.setTouchable(false);
        new WinDialog(mView.getContext()).show();
    }

    /**
     * Stops the timer
     */
    public void stopTimer() {
        mTimeObs.dispose();
    }

    /**
     * Resets the current Game
     */
    public void resetGame() {
        mGameLogic.setTiles(mStartingTiles.clone());
        mGameFieldView.invalidate();
        mGameFieldView.setTouchable(true);
        mCurrentTime = 0;
        mView.updateTime(convertTime(mCurrentTime));
    }

    /**
     * Restores a previous game state
     * For example because of orientation change
     * @param tiles
     */
    public void restoreGame(final int[] tiles) {
        loadImage().subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                mGameLogic.setTiles(tiles);
                mGameFieldView.invalidate();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }

    /**
     * Starts a new game by mixing up the image
     */
    public void newGame() {
        loadImage().subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                // Shuffle the photo
                mGameFieldView.shuffle();
                // Save beginning Tiles to be able to reset later
                mStartingTiles = mGameLogic.getTiles().clone();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }

    /**
     * Loads the image from the URL variable
     * Separate ways for internet or local files
     * @return observable
     */
    private Completable loadImage() {
        return new Completable() {
            @Override
            protected void subscribeActual(CompletableObserver s) {
                if(mUrl != null) {

                    // Local
                    if(!mUrl.contains("http")) {
                        Bitmap bitmap = BitmapFactory.decodeFile(mUrl);
                        mGameFieldView.setImage(bitmap);
                        // Shuffle the photo
                        mGameFieldView.shuffle();
                        // Save beginning Tiles to be able to reset later
                        mStartingTiles = mGameLogic.getTiles().clone();
                    }
                    // Web like Instagram
                    else
                        Picasso
                                .with(mView.getContext())
                                .load(mUrl)
                                .into(createTarget(s));
                }
            }
        };
    }

    /**
     * Receives the Bitmap after being loaded from Picasso
     * and completes the Observable
     * @param s
     * @return Target for Picasso
     */
    private Target createTarget(final CompletableObserver s) {
        return new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mGameFieldView.setImage(bitmap);
                s.onComplete();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                s.onError(new Throwable("Error loading Bitmap"));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public IGameLogic getGameLogic() {
        return mGameLogic;
    }

    public long getCurrentTime() {
        return mCurrentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.mCurrentTime = currentTime;
        this.mView.updateTime(convertTime(currentTime));
    }
}
