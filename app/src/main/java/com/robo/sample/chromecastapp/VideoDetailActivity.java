package com.robo.sample.chromecastapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.gms.cast.framework.CastButtonFactory;

import static com.robo.sample.chromecastapp.Constant.BundleKeys.SHOULD_START_PLAYBACK;
import static com.robo.sample.chromecastapp.Constant.BundleKeys.VIDEO_NAME;
import static com.robo.sample.chromecastapp.Constant.BundleKeys.VIDEO_URL;

/**
 * Created by manoj on 21/2/17.
 */

public class VideoDetailActivity extends AppCompatActivity implements CommonVideoPlayerFragment.ScreenListener {

    private MenuItem mediaRouteMenuItem;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        setupActionBar();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            FragmentHelper.replaceFragment(this, CommonVideoPlayerFragment.getInstance(bundle.getString(VIDEO_NAME), bundle.getString(VIDEO_URL),bundle.getBoolean(SHOULD_START_PLAYBACK)), R.id.video_container);
        }
    }
    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.browse, menu);
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
                R.id.media_route_menu_item);
        return true;
    }

    @Override
    public void onFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    @Override
    public void onExitFullScreen() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().show();
    }
}
