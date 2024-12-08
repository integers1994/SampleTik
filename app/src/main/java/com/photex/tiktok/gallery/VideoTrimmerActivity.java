package com.photex.tiktok.gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deep.videotrimmer.DeepVideoTrimmer;
import com.deep.videotrimmer.interfaces.OnTrimVideoListener;
import com.deep.videotrimmer.view.RangeSeekBarView;
import com.photex.tiktok.R;
import com.photex.tiktok.activities.VideoPreviewExoActivity;
import com.photex.tiktok.models.MediaInfo;
import com.photex.tiktok.models.VideoInfo;
import com.photex.tiktok.utils.CommonUtils;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.RealPathUtil;

import java.util.ArrayList;

public class VideoTrimmerActivity extends AppCompatActivity implements OnTrimVideoListener {


    private DeepVideoTrimmer mVideoTrimmer;
    private ProgressBar progressBarInternal;
    TextView textSize, tvCroppingMessage;
    RangeSeekBarView timeLineBar;
    long maxDuration = 60;

    String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtils.transparentStatusBar(getActivity());
        setContentView(R.layout.activity_video_trimer);

        Intent extraIntent = getIntent();


        if (extraIntent != null) {
            path = extraIntent.getStringExtra(Constants.EXTRA_VIDEO_PATH);
        }

        initUI();
    }

    private void initUI() {
        mVideoTrimmer = findViewById(R.id.timeLine);
        timeLineBar = findViewById(R.id.timeLineBar);
        textSize = findViewById(R.id.textSize);
        progressBarInternal = findViewById(R.id.progressBarInternal);
        tvCroppingMessage = findViewById(R.id.tvCroppingMessage);

        if (mVideoTrimmer != null && path != null) {
            mVideoTrimmer.setMaxDuration(60);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setVideoURI(Uri.parse(path));
        } else {
            CommonUtils.showToast(getActivity(),
                    getString(R.string.msg_unable_to_extract_video));
            finish();
        }
    }

    @Override
    public void getResult(Uri uri) {


        runOnUiThread(() -> setProgressVisibility(View.GONE));

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(getActivity(), uri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);

        retriever.release();
        ArrayList<VideoInfo> videosList = new ArrayList<>();

        /*Prepare Video info */
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setDuration((int) timeInMillisec);
        videoInfo.setPath(uri.getPath());
        videosList.add(videoInfo);


        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setInputVideos(videosList);

        String realPathInUri = uri.getPath()/*RealPathUtil.getPathFromUri(getActivity(), uri)*/;
        if (realPathInUri != null && !realPathInUri.isEmpty()) {
            VideoPreviewExoActivity.Companion.intent(getActivity(),
                    realPathInUri, mediaInfo);
            finish();
        } else {

            runOnUiThread(() -> CommonUtils.showToast(getActivity(),
                    "Uri is null"));
        }


    }

    @Override
    public void onFailure() {
        setProgressVisibility(View.GONE);
        CommonUtils.showToast(getActivity(), "Something goes wrong");
    }

    @Override
    public void saveAction() {
        setProgressVisibility(View.VISIBLE);
    }

    @Override
    public void cancelAction() {
        mVideoTrimmer.destroy();
        setProgressVisibility(View.GONE);
        finish();
    }

    private void setProgressVisibility(int visibility) {
        progressBarInternal.setVisibility(visibility);
        tvCroppingMessage.setVisibility(visibility);
    }

    private Activity getActivity() {
        return VideoTrimmerActivity.this;
    }

    @Override
    protected void onDestroy() {

        //mVideoTrimmer.destroy();
        super.onDestroy();
    }
}
