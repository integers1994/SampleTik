package com.photex.tiktok.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.photex.tiktok.R
import com.photex.tiktok.models.MediaInfo
import com.photex.tiktok.utils.CommonUtils
import com.photex.tiktok.utils.Constants
import kotlinx.android.synthetic.main.activity_video_preview_exo.*
import java.io.File


class VideoPreviewExoActivity : AppCompatActivity() {

    lateinit var videoUri: Uri
    lateinit var mediaInfo: MediaInfo

    companion object {

        fun intent(mActivity: Activity, filePath: String, mediaInfo: MediaInfo) {
            val intent = Intent(mActivity, VideoPreviewExoActivity::class.java)
            intent.putExtra(Constants.EXTRA_VIDEO_FILE_URI, filePath)
            intent.putExtra(Constants.EXTRA_VIDEO_MEDIA_INFO, mediaInfo)
            mActivity.startActivity(intent)
            mActivity.overridePendingTransition(R.anim.enter, R.anim.exit)
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonUtils.transparentStatusBar(getActivity())
        setContentView(R.layout.activity_video_preview_exo)

        if (intent != null && intent.hasExtra(Constants.EXTRA_VIDEO_FILE_URI) &&
                intent.hasExtra(Constants.EXTRA_VIDEO_MEDIA_INFO)) {

            val stringUri = intent.getStringExtra(Constants.EXTRA_VIDEO_FILE_URI)
            videoUri = Uri.fromFile(File(stringUri))
            mediaInfo = intent?.extras?.get(Constants.EXTRA_VIDEO_MEDIA_INFO) as MediaInfo

        } else {
            finish()
        }


        video_view.setVideoURI(videoUri)

/*
        playable!!.playerView = playerView
*/

    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.img_done -> {
                startActivityForResult(Intent(getActivity(), VideoUploadSecondActivity::class.java)
                        .putExtra(Constants.EXTRA_URI, videoUri)
                        .putExtra(Constants.EXTRA_VIDEO_MEDIA_INFO, mediaInfo),

                        Constants.UPLOAD_VIDEO_REQUEST)
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }
            R.id.img_back -> {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        video_view.start()

    }

    fun getActivity(): Activity {
        return this
    }


    // Dynamically add buttons to control the Playable instance.

    override fun onPause() {
        super.onPause()
        video_view.start()

        /*  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
              mediaPlayer.releasePlayer()
          }*/
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}


// A extension function for our ViewGroup.
fun ViewGroup.addCustomButton(inflater: LayoutInflater, name: String,
                              action: (View) -> Unit) {
    (inflater.inflate(R.layout.widget_debug_button, this, false) as Button)
            .apply {
                this.text = name
                this.setOnClickListener { action.invoke(it) }
            }
            .run { this@addCustomButton.addView(this) }
}
