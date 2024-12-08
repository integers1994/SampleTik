package com.photex.tiktok.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.photex.tiktok.activities.MyAudiosActivity;

public class AudioPlayerHelper {
    private Context context;
    private String TAG;

    private static SimpleExoPlayer exoPlayer;
    private static SimpleExoPlayer streamingExoPlayer;

    private DefaultRenderersFactory renderersFactory;
    private DefaultBandwidthMeter bandwidthMeter;
    private AdaptiveTrackSelection.Factory trackSelectionFactory;
    private DefaultTrackSelector trackSelector;
    private DefaultLoadControl loadControl;
    private DefaultDataSourceFactory dataSourceFactory;
    private DefaultExtractorsFactory extractorsFactory;
    private Handler mainHandler;
    private ExtractorMediaSource mediaSource;

    public AudioPlayerHelper(Context context) {
        this.context = context;
        TAG = context.getClass().getName();
    }

    public SimpleExoPlayer getInstance() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
            exoPlayer.addListener(eventListener);
        }
        return exoPlayer;
    }

    public SimpleExoPlayer getStreamingInstance() {
        if (streamingExoPlayer == null) {
            renderersFactory = new DefaultRenderersFactory(context);
            trackSelector = new DefaultTrackSelector();
            loadControl = new DefaultLoadControl();

            streamingExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory,
                    trackSelector);
        }
        return streamingExoPlayer;
    }

    private ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {
/*
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            Log.i(TAG, "onTimelineChanged");
        }
*/

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.i(TAG, "onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.i(TAG, "onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.i(TAG, "onPlayerStateChanged: playWhenReady = " + String.valueOf(playWhenReady)
                    + " playbackState = " + playbackState);
            switch (playbackState) {
                case ExoPlayer.STATE_ENDED:
                    Log.i(TAG, "Playback ended!");
                    //Stop playback and return to start position
                    exoPlayer.setPlayWhenReady(true);
                    exoPlayer.seekTo(0);
                    break;
                case ExoPlayer.STATE_READY:
                    Log.i(TAG, "ExoPlayer ready! pos: " + exoPlayer.getCurrentPosition());
                    exoPlayer.setPlayWhenReady(true);
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    Log.i(TAG, "Playback buffering!");
                    break;
                case ExoPlayer.STATE_IDLE:
                    Log.i(TAG, "ExoPlayer idle!");
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.i(TAG, "onPlaybackError: " + error.getMessage());
        }

        @Override
        public void onPositionDiscontinuity() {

        }

/*
        @Override
        public void onPositionDiscontinuity() {
            Log.i(TAG, "onPositionDiscontinuity");
        }
*/

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            Log.i(TAG, "onPlaybackParametersChanged");

        }
    };

}
