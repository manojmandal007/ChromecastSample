/*
 * Project     : Swing
 * Filename    : CommonVideoPlayerFragment.java
 * Author      : manoj on 5/2/16 7:40 PM
 * Comments    :
 * Copyright   : Copyright © 2015, GolfingIndian.com
 *  	           Written under contract by Robosoft Technologies Pvt. Ltd.
 * History     : NA
 */

package com.robo.sample.chromecastapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.robo.sample.chromecastapp.cast.ExpandedControlsActivity;

/**
 * Created by manoj on 5/2/16.
 */
public class CommonVideoPlayerFragment extends BaseFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, TextureVideoView.OrientationListener, SensorEventListener, MediaPlayer.OnBufferingUpdateListener, Constant.BundleKeys {

    private ScreenListener mScreenListener;
    private TextureVideoView mTextureVideoView;
    private VideoControllerView mVideoController;
    private ProgressBar mProgressBar;
    private AudioManager mAudioManager;
    private boolean mResumeVideo;
    private TextView mVideoNameView;
    private int mCurrentPos, mOrientation = 1;
    private SensorManager mSensorManager;
    private String mUrl, mVideoName;

    private CastContext mCastContext;
    private CastSession mCastSession;
    private SessionManagerListener<CastSession> mSessionManagerListener;
    private PlaybackState mPlaybackState;
    private PlaybackLocation mLocation;

    /**
     * indicates whether we are doing a local or a remote playback
     */
    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }

    /**
     * List of various states that we can be in
     */
    public enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }

    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionEnding(CastSession session) {
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            }

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;
                if (mPlaybackState == PlaybackState.PLAYING) {
                    mTextureVideoView.pause();
                    loadRemoteMedia(mTextureVideoView.getCurrentPosition(), true);
                    return;
                } else {
                    mPlaybackState = PlaybackState.IDLE;
                    updatePlaybackLocation(PlaybackLocation.REMOTE);
                }
                //  updatePlayButton(mPlaybackState);
                getActivity().invalidateOptionsMenu();
            }

            private void onApplicationDisconnected() {
                updatePlaybackLocation(PlaybackLocation.LOCAL);
                mPlaybackState = PlaybackState.IDLE;
                mLocation = PlaybackLocation.LOCAL;
                //updatePlayButton(mPlaybackState);
                getActivity().invalidateOptionsMenu();
            }
        };
    }

    private void togglePlayback() {
        //  stopControllersTimer();
        switch (mPlaybackState) {
            case PAUSED:
                switch (mLocation) {
                    case LOCAL:
                        mTextureVideoView.start();
                        mPlaybackState = PlaybackState.PLAYING;
                        //startControllersTimer();
                        // restartTrickplayTimer();
                        updatePlaybackLocation(PlaybackLocation.LOCAL);
                        break;
                    case REMOTE:
                        getActivity().finish();
                        break;
                    default:
                        break;
                }
                break;

            case PLAYING:
                mPlaybackState = PlaybackState.PAUSED;
                mTextureVideoView.pause();
                break;

            case IDLE:
                switch (mLocation) {
                    case LOCAL:
                        mTextureVideoView.setVideoURI(Uri.parse(mUrl));
                        mTextureVideoView.seekTo(0);
                        mTextureVideoView.start();
                        mPlaybackState = PlaybackState.PLAYING;
                        //restartTrickplayTimer();
                        updatePlaybackLocation(PlaybackLocation.LOCAL);
                        break;
                    case REMOTE:
                        if (mCastSession != null && mCastSession.isConnected()) {
                            loadRemoteMedia(mTextureVideoView.getCurrentPosition(), true);
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        // updatePlayButton(mPlaybackState);
    }

    private void updatePlaybackLocation(PlaybackLocation location) {
        mLocation = location;
/*        if (location == PlaybackLocation.LOCAL) {
            if (mPlaybackState == PlaybackState.PLAYING
                    || mPlaybackState == PlaybackState.BUFFERING) {
                setCoverArtStatus(null);
                startControllersTimer();
            } else {
                stopControllersTimer();
                setCoverArtStatus(mSelectedMedia.getImage(0));
            }
        } else {
            stopControllersTimer();
            setCoverArtStatus(mSelectedMedia.getImage(0));
            updateControllersVisibility(false);
        }*/
    }

    private void loadRemoteMedia(int position, boolean autoPlay) {
        if (mCastSession == null) {
            return;
        }
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }
        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                Intent intent = new Intent(getActivity(), ExpandedControlsActivity.class);
                startActivity(intent);
                remoteMediaClient.removeListener(this);
            }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }
        });
        remoteMediaClient.load(buildMediaInfo(), autoPlay, position);
    }

    private MediaInfo buildMediaInfo() {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

        //movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, mSelectedMedia.getStudio());
        movieMetadata.putString(MediaMetadata.KEY_TITLE, mVideoName);
        //movieMetadata.addImage(new WebImage(Uri.parse(mSelectedMedia.getImage(0))));
        // movieMetadata.addImage(new WebImage(Uri.parse(mSelectedMedia.getImage(1))));
        return new MediaInfo.Builder(mUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/mp4")
                .setMetadata(movieMetadata)
                .setStreamDuration(567 * 1000)
                .build();
    }

    public interface ScreenListener {
        void onFullScreen();

        void onExitFullScreen();
    }

    public static CommonVideoPlayerFragment getInstance(String videoName, String videoUrl, boolean shouldStartPlayback) {
        CommonVideoPlayerFragment videoPlayerFragment = new CommonVideoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(VIDEO_URL, videoUrl);
        bundle.putString(VIDEO_NAME, videoName);
        bundle.putBoolean(SHOULD_START_PLAYBACK, shouldStartPlayback);
        videoPlayerFragment.setArguments(bundle);
        return videoPlayerFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            mScreenListener = (ScreenListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getLocalClassName()
                    + " must implement ScreenListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_video_player, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mTextureVideoView = (TextureVideoView) view.findViewById(R.id.texture_video_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mVideoNameView = (TextView) view.findViewById(R.id.video_name);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        extractArguments();
        initListener();

        setupCastListener();
        mCastContext = CastContext.getSharedInstance(getActivity());
        mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(getActivity(), savedInstanceState);
        mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
        boolean shouldStartPlayback = getArguments().getBoolean(SHOULD_START_PLAYBACK);
        int startPosition = getArguments().getInt("startPosition", 0);
        if (shouldStartPlayback) {
            // this will be the case only if we are coming from the
            // CastControllerActivity by disconnecting from a device
            mPlaybackState = PlaybackState.PLAYING;
            updatePlaybackLocation(PlaybackLocation.LOCAL);
            //  updatePlayButton(mPlaybackState);
            if (startPosition > 0) {
                mTextureVideoView.seekTo(startPosition);
            }

            mTextureVideoView.start();
            //  startControllersTimer();
        } else {
            // we should load the video but pause it
            // and show the album art.
            if (mCastSession != null && mCastSession.isConnected()) {
                updatePlaybackLocation(PlaybackLocation.REMOTE);
            } else {
                updatePlaybackLocation(PlaybackLocation.LOCAL);
            }
            mPlaybackState = PlaybackState.IDLE;
            //  updatePlayButton(mPlaybackState);
        }


        mVideoNameView.setText(mVideoName);
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mVideoController = new VideoControllerView(getActivity());
        mTextureVideoView.setMediaController(mVideoController);
        mTextureVideoView.setVideoURI(Uri.parse(mUrl));

        togglePlayback();
    }

    private void initListener() {
        mTextureVideoView.setOnCompletionListener(this);
        mTextureVideoView.setOnErrorListener(this);
        mTextureVideoView.setOnPreparedListener(this);
        mTextureVideoView.setFullScreenListener(this);
        mTextureVideoView.setOnBufferListener(this);
    }

    private void extractArguments() {
        mUrl = getArguments().getString(VIDEO_URL);
        mVideoName = getArguments().getString(VIDEO_NAME);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        if (mCastSession != null && mCastSession.isConnected()) {
            updatePlaybackLocation(PlaybackLocation.REMOTE);
        } else {
            updatePlaybackLocation(PlaybackLocation.LOCAL);
        }

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        if (mResumeVideo) {
            if (!TextUtils.isEmpty(mUrl)) {
                mTextureVideoView.setVideoURI(Uri.parse(mUrl));
                int audioInFocus = mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                if (audioInFocus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mTextureVideoView.start();
                    mTextureVideoView.seekTo(mCurrentPos);
                }
                mResumeVideo = false;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mCastContext.getSessionManager().removeSessionManagerListener(
                mSessionManagerListener, CastSession.class);

        if (mVideoController != null)
            mVideoController.hide();

        if (mTextureVideoView.isPlaying()) {
            mTextureVideoView.pause();
            mPlaybackState = PlaybackState.PAUSED;
            mCurrentPos = mTextureVideoView.getCurrentPosition();
            mResumeVideo = true;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        int audioInFocus = mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (audioInFocus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mTextureVideoView.start();
            mTextureVideoView.seekTo(mCurrentPos);
            mProgressBar.setVisibility(View.GONE);
            mVideoController.show();
        }
    }

    @Override
    public void setOrientation(int orientation) {
        if (mVideoController != null)
            mVideoController.hide();
        Utils.setOrientation(getActivity(), orientation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getActivity() == null)
            return;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mVideoController.updateFullScreen();
            mScreenListener.onFullScreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mVideoController.updateFullScreen();
            mScreenListener.onExitFullScreen();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values[1] < 2.5 && sensorEvent.values[1] > -6.5) {
            if (mOrientation != 1) {
                setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            mOrientation = 1;
        } else {
            if (mOrientation != 0) {
                setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
            mOrientation = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int bufferPercent) {
        Log.v("onBufferingUpdate", "" + bufferPercent);
        mTextureVideoView.setCurrentBuffering(bufferPercent);
    }

    @Override
    public void onDestroyView() {
        if (mVideoController != null) {
            mVideoController.hide();
        }
        mSensorManager.unregisterListener(this);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mScreenListener.onExitFullScreen();
        if (mTextureVideoView != null) {
            mTextureVideoView.stopPlayback();
            mTextureVideoView = null;
        }
        super.onDetach();
    }
}
