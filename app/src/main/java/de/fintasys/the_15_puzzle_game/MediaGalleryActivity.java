package de.fintasys.the_15_puzzle_game;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import de.fintasys.the_15_puzzle_game.adapter.MediaGalleryAdapter;
import de.fintasys.the_15_puzzle_game.dialogs.ErrorDialog;
import de.fintasys.the_15_puzzle_game.interfaces.IImageModel;
import de.fintasys.the_15_puzzle_game.interfaces.IMediaGalleryView;
import de.fintasys.the_15_puzzle_game.model.InstagramModel;
import de.fintasys.the_15_puzzle_game.presenter.MediaGalleryActivityPresenter;

public class MediaGalleryActivity extends AppCompatActivity implements IMediaGalleryView {

    private final String TAG = getClass().getSimpleName();

    private MediaGalleryActivityPresenter mediaGalleryAcitvityPresenter;
    private MediaGalleryAdapter adapter;
    private List<String> media;
    private final int GRID_SIZE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_gallery);
        ButterKnife.bind(this);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        IImageModel instagramModel = new InstagramModel(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mediaGalleryAcitvityPresenter = new MediaGalleryActivityPresenter(this, instagramModel);

        media = new ArrayList<>();

        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, GRID_SIZE));
        adapter = new MediaGalleryAdapter(this, media, mediaClickListener, (int)(metrics.widthPixels / (float)GRID_SIZE));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaGalleryAcitvityPresenter.init();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showError(String message) {
        new ErrorDialog(this, message).show();
    }

    @Override
    public void updateMedia(List<String> newMedia) {
        media.clear();
        media.addAll(newMedia);
        adapter.notifyDataSetChanged();
    }

    private View.OnClickListener mediaClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int)v.getTag();
            Intent i = new Intent();
            i.putExtra("url", media.get(position));
            setResult(RESULT_OK, i);
            finish();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
