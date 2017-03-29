package de.fintasys.the_15_puzzle_game;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.fintasys.the_15_puzzle_game.interfaces.IMainView;
import de.fintasys.the_15_puzzle_game.presenter.MainActivityPresenter;

public class MainActivity extends AppCompatActivity implements IMainView {

    private final String TAG = getClass().getSimpleName();
    private final int READ_MEDIA_REQUEST_CODE = 30;
    private final int READ_LOCAL_REQUEST_CODE = 40;

    private MainActivityPresenter mainActivityPresenter;

    @BindView(R.id.iv_title)
    ImageView ivTitle;

    @BindView(R.id.tv_choose_a_photo)
    TextView tvChooseAPhoto;

    @BindView(R.id.btn_instagram)
    Button btnMedia;

    @BindView(R.id.btn_local)
    Button btnLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainActivityPresenter = new MainActivityPresenter(this);

        btnMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MediaGalleryActivity.class);
                startActivityForResult(i, READ_MEDIA_REQUEST_CODE);
            }
        });

        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load from Content Provider
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, READ_LOCAL_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for Permission (only from api23)
        mainActivityPresenter.checkForPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startOpeningAnimation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == READ_MEDIA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent i = new Intent(this, GameActivity.class);
                i.putExtra("url", data.getStringExtra("url"));
                startActivity(i);
            }
        }
        else if(requestCode == READ_LOCAL_REQUEST_CODE) {
            Uri uri;
            if (data != null) {
                uri = data.getData();

                // Get Real Path
                String url = mainActivityPresenter.getRealPath(uri);
                if(url != null) {
                    Intent i = new Intent(this, GameActivity.class);
                    i.putExtra("url", url);
                    startActivity(i);
                }
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    /**
     *  App Opening Animation
     */
    private void startOpeningAnimation() {
        Animation animSlideInLeft;
        if(getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT)
            animSlideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        else
            animSlideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        animSlideInLeft.setDuration(1000);
        btnMedia.startAnimation(animSlideInLeft);

        Animation animSlideInRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        animSlideInRight.setDuration(1000);
        btnLocal.startAnimation(animSlideInRight);

        Animation animCenterToTop = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        animCenterToTop.setDuration(3000);
        ivTitle.startAnimation(animCenterToTop);
        tvChooseAPhoto.startAnimation(animCenterToTop);
    }

    /**
     * Check Permissions result and disabled Buttons if necessary
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {

                // Check if one permission is not granted
                boolean allgranted = true;
                for(int g : grantResults) {
                    if (g == -1) {
                        allgranted = false;
                        break;
                    }
                }

                if (!allgranted) {

                    // permission denied
                    // Disable the functionality that depends on this permission.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                        btnMedia.setEnabled(false);
                    }

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        btnLocal.setEnabled(false);
                    }

                }

            }
        }
    }
}
