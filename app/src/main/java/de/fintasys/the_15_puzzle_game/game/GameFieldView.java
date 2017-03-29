package de.fintasys.the_15_puzzle_game.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import de.fintasys.the_15_puzzle_game.interfaces.IGameLogic;


public class GameFieldView extends View {

    private IGameLogic mGameLogic;
    private Bitmap mOriginalImage;
    private Bitmap[] mBitmapTiles;
    private int mViewWidth, mViewHeight;
    private boolean mTouchable = true;
    private Paint paint = new Paint();

    public GameFieldView(Context context) {
        super(context);
    }

    public GameFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameFieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GameFieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Initiates the GameField
     * @param gameLogic
     */
    public void init(IGameLogic gameLogic) {
        mGameLogic = gameLogic;
        mBitmapTiles = new Bitmap[gameLogic.getNumTiles() + 1];
    }


    /**
     * Handles user touch interaction with the view
     * @param event
     * @return boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mTouchable) {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                handleAction((int) event.getX(), (int) event.getY());
        }
        return true;
    }

    /**
     * Receive new mixed up tiles
     * Redraw the view afterwards
     */
    public void shuffle() {
        mGameLogic.shuffle();
        invalidate();
    }

    /**
     * Receives X,Y Coordination from touch action
     * GameLogic calculates new position of tiles
     * Redraw the view afterwards
     * @param x
     * @param y
     */
    private void handleAction(int x, int y) {
        mGameLogic.onAction(x, y);
        invalidate();
    }

    /**
     * Resize and crop the original Bitmap
     */
    private void prepareOriginalImage() {
        resizeOriginalBitmap();
        cropOriginalBitmap();
    }

    /**
     * Create Bitmap tiles out of the original Bitmap
     */
    private void createBitmapTiles() {
        int count = 0;
        for (int i = 0; i < mGameLogic.getSides(); i++) {
            for(int j = 0; j < mGameLogic.getSides(); j++) {
                mBitmapTiles[count] = Bitmap.createBitmap(mOriginalImage, j * mGameLogic.getTileSize(), i * mGameLogic.getTileSize(), mGameLogic.getTileSize(), mGameLogic.getTileSize());
                count++;
            }
        }
    }

    /**
     * Sort the Bitmap tiles in order of the puzzle tiles
     * received from the logic
     */
    private void arrangePuzzle() {
        Bitmap[] newBitmapTiles = new Bitmap[mBitmapTiles.length];
        int[] tiles = mGameLogic.getTiles();
        for(int i = 0; i < tiles.length; i++) {
            if(tiles[i] != 0)
                newBitmapTiles[i] = mBitmapTiles[tiles[i]-1];
            else
                newBitmapTiles[i] = null;
        }
        mBitmapTiles = newBitmapTiles;
    }

    /**
     * Resize original Bitmap to fit the ratio better
     */
    private void resizeOriginalBitmap() {
        int resizeX, resizeY;
        // Check if Bitmap size is bigger than view size
        if(mOriginalImage.getWidth() >= mViewWidth && mOriginalImage.getHeight() >= mViewHeight) {
            if (mOriginalImage.getWidth() == mViewWidth ||  mOriginalImage.getHeight() == mViewHeight) {
                return;
            }
            else if (mOriginalImage.getHeight() < mOriginalImage.getWidth()) {
                float factor = mOriginalImage.getHeight() / (float) mViewHeight;
                resizeX = (int) (mOriginalImage.getWidth() / factor);
                resizeY = mViewHeight;
            }
            else {
                float factor = mOriginalImage.getWidth() / (float) mViewWidth;
                resizeX = mViewWidth;
                resizeY = (int) (mOriginalImage.getHeight() / factor);
            }
        }else{
            if(mOriginalImage.getWidth() > mOriginalImage.getHeight()) {
                float factor = (float)mViewHeight / mOriginalImage.getHeight();
                resizeX = (int)(mOriginalImage.getWidth() * factor);
                resizeY = mViewHeight;
            }
            else {
                float factor = (float)mViewWidth / mOriginalImage.getWidth();
                resizeX = mViewWidth;
                resizeY = (int)(mOriginalImage.getHeight() * factor);
            }
        }

        mOriginalImage = Bitmap.createScaledBitmap(mOriginalImage, resizeX, resizeY, false);
    }

    /**
     * Crop Bitmap to get Bitmap with same height and width
     */
    private void cropOriginalBitmap() {
        if (mOriginalImage.getWidth() >= mOriginalImage.getHeight()){

            mOriginalImage = Bitmap.createBitmap(
                    mOriginalImage,
                    mOriginalImage.getWidth() / 2 - mOriginalImage.getHeight() / 2,
                    0,
                    mOriginalImage.getHeight(),
                    mOriginalImage.getHeight()
            );

        }else{

            mOriginalImage = Bitmap.createBitmap(
                    mOriginalImage,
                    0,
                    mOriginalImage.getHeight() / 2 - mOriginalImage.getWidth() / 2,
                    mOriginalImage.getWidth(),
                    mOriginalImage.getWidth()
            );
        }
    }

    /**
     * Do preparations before drawing (view width/height, prepare Bitmap)
     */
    private void refresh() {
        if(mViewHeight > 0 && mViewWidth > 0 && mOriginalImage != null) {
            mGameLogic.setSize(mViewHeight > mViewWidth ? mViewHeight : mViewWidth);
            prepareOriginalImage();
            createBitmapTiles();
            arrangePuzzle();
        }
    }

    /**
     * Draws the Bitmap tiles
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(255, 255, 255));
        canvas.drawRect(0,  0, mGameLogic.getTileSize() * mGameLogic.getSides(), mGameLogic.getTileSize() * mGameLogic.getSides(), paint);
        int row = -1;
        for (int i = 0; i < mBitmapTiles.length; i++) {

            if(i % mGameLogic.getSides() == 0)
                row++;

            if(mBitmapTiles[i] != null)
                canvas.drawBitmap(mBitmapTiles[i], mGameLogic.getTileSize() * (i % mGameLogic.getSides()), mGameLogic.getTileSize() * row, paint);
        }
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        mViewWidth = xNew;
        mViewHeight = yNew;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        int desireWidthHeight;
        if(height < width)
            desireWidthHeight = height;
        else
            desireWidthHeight = width;

        int wDesired = getPaddingLeft() + getPaddingRight() +
                Math.max(desireWidthHeight, getSuggestedMinimumWidth());
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int w = 0;
        switch(wSpecMode){
            case MeasureSpec.EXACTLY:
                w = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                w = Math.min(wDesired, MeasureSpec.getSize(widthMeasureSpec));
                break;
            case MeasureSpec.UNSPECIFIED:
                w = wDesired;
                break;
        }

        int hDesired = getPaddingTop() + getPaddingBottom() +
                Math.max(desireWidthHeight, getSuggestedMinimumHeight());
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int h = 0;
        switch(hSpecMode){
            case MeasureSpec.EXACTLY:
                h = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                h = Math.min(hDesired, MeasureSpec.getSize(heightMeasureSpec));
                break;
            case MeasureSpec.UNSPECIFIED:
                h = hDesired;
                break;
        }

        setMeasuredDimension(w, h);
    }

    @Override
    public void invalidate() {
        refresh();
        super.invalidate();
    }

    /**
     * Clean up memory when Bitmaps are not needed anymore
     */
    public void destroy() {
        super.onDetachedFromWindow();
        if (mOriginalImage != null && !mOriginalImage.isRecycled()) {
            mOriginalImage.recycle();
            mOriginalImage = null;
        }
        for(Bitmap b : mBitmapTiles) {
            if(b != null && !b.isRecycled()) {
                b.recycle();
            }
        }
    }

    public IGameLogic getGameLogic() {
        return mGameLogic;
    }

    public void setImage(Bitmap image) {
        mOriginalImage = image;
    }

    public Bitmap getImage() {
        return mOriginalImage;
    }

    public boolean isTouchable() {
        return mTouchable;
    }

    public void setTouchable(boolean touchable) {
        this.mTouchable = touchable;
    }
}
