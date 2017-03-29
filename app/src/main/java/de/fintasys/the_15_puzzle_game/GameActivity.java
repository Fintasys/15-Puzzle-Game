package de.fintasys.the_15_puzzle_game;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.fintasys.the_15_puzzle_game.game.GameFieldView;
import de.fintasys.the_15_puzzle_game.interfaces.IGameView;
import de.fintasys.the_15_puzzle_game.presenter.GameActivityPresenter;


public class GameActivity extends AppCompatActivity implements IGameView {

    private final String TAG = getClass().getSimpleName();

    private GameActivityPresenter gameActivityPresenter;

    @BindView(R.id.gameField)
    GameFieldView gameFieldView;

    @BindView(R.id.tv_timer)
    TextView tvTimer;

    @BindView(R.id.btn_reset)
    Button btnReset;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putIntArray("tiles", gameActivityPresenter.getGameLogic().getTiles());
        outState.putString("url", gameActivityPresenter.getUrl());
        outState.putLong("time", gameActivityPresenter.getCurrentTime());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_acitvity);
        ButterKnife.bind(this);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnReset.setOnClickListener(resetListener);

        gameActivityPresenter = new GameActivityPresenter(this, gameFieldView);

        if (savedInstanceState != null) {

            gameActivityPresenter.setUrl(savedInstanceState.getString("url"));
            gameActivityPresenter.restoreGame(savedInstanceState.getIntArray("tiles"));
            gameActivityPresenter.setCurrentTime(savedInstanceState.getLong("time"));

        } else {

            gameActivityPresenter.setUrl(getIntent().getStringExtra("url"));
            gameActivityPresenter.newGame();

        }

        gameActivityPresenter.startTimer();
    }

    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gameActivityPresenter.resetGame();
        }
    };

    @Override
    public void updateTime(String time) {
        tvTimer.setText(time);
    }

    @Override
    public Context getContext() {
        return this;
    }

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

    @Override
    protected void onDestroy() {
        if(gameFieldView != null)
            gameFieldView.destroy();

        gameActivityPresenter.stopTimer();

        super.onDestroy();
    }
}
