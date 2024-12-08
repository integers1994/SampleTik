package com.photex.tiktok.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.photex.tiktok.R
import com.photex.tiktok.models.MediaInfo
import com.photex.tiktok.models.VideoMakerPostInfo
import com.photex.tiktok.services.UploadVideoSecondService
import com.photex.tiktok.setting.SettingManager
import com.photex.tiktok.utils.*
import kotlinx.android.synthetic.main.activity_video_upload_second.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class VideoUploadSecondActivity : AppCompatActivity() {

    val TAG = VideoUploadSecondActivity::class.java.simpleName
    private lateinit var mediaInfo: MediaInfo
    private var thumbnailBitmap: Bitmap? = null
    private var videoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_upload_second)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent != null && intent.hasExtra(Constants.EXTRA_URI)) {
            videoUri = intent?.extras?.get(Constants.EXTRA_URI) as Uri?
            mediaInfo = intent?.extras?.get(Constants.EXTRA_VIDEO_MEDIA_INFO) as MediaInfo
        } else {
            finish()
        }

        videoUri?.let {
            /*   thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(RealPathUtil.getPathFromUri
               (getActivity(), it), MediaStore.Images.Thumbnails.MINI_KIND)*/
            iv_video_thumbnail.setImageBitmap(thumbnailBitmap)
            loading_indicator.show()
            Glide.with(getActivity())
                    .asBitmap()
                    .load(RealPathUtil.getPathFromUri(getActivity(),videoUri))
                    .apply(RequestOptions()
                            .placeholder(R.color.blue)

                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
/*
                          CommonUtils.showToast(getActivity(),"Some thing goes wrong")

*/
                            thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(mediaInfo.inputVideos[0].path,
                                    MediaStore.Images.Thumbnails.MINI_KIND)
                            iv_video_thumbnail.setImageBitmap(thumbnailBitmap)

                            return true
                        }

                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            if(resource != null ){
                                loading_indicator.hide()

                                thumbnailBitmap= resource
                                iv_video_thumbnail.setImageBitmap(thumbnailBitmap)
                            }
                            return true
                        }

                    })
                    .into(iv_video_thumbnail)
        }

    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.btn_upload_post -> {
                hideKeyBoard()
                if (mediaInfo.audioInfo != null)
                    mediaInfo.getAudioInfo().setCatId("5c1a339547743113e20d8bdb")
                uploadDialog(getActivity())
            }

        }
    }

    fun uploadDialog(context: Context) {

        val alertupload = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
        val message = "Do you want to Upload this Video ?" + "It might takes some time to process"

        alertupload.setTitle(R.string.app_name)
        alertupload.setIcon(R.mipmap.ic_launcher)
        alertupload.setCancelable(true)
        alertupload.setMessage(message)

        alertupload.setPositiveButton(getString(R.string.yes)) { arg0, arg1 ->
            if (Util.isNetworkAvailable(getActivity())) {
                startVideoUploading()
            } else {
                Util.internetNotAvailableDialouge(getActivity())
            }
        }
        alertupload.setNegativeButton(getString(R.string.no))
        { dialog, which -> dialog.dismiss() }
        alertupload.show()

    }

    private fun startVideoUploading() {
        val postInfo = getPostInfo()

        // upload video in background
        if (!CommonUtils.isMyServiceRunning(getActivity(), UploadVideoSecondService::class.java)) {
            val serviceIntent = Intent(getActivity(),
                    UploadVideoSecondService::class.java)
            UploadVideoSecondService.IS_SERVICE_RUNNING = true
            serviceIntent.putExtra(Constants.EXTRA_POST_INFO, postInfo)
            serviceIntent.putExtra(Constants.EXRTA_MEDIA_INFO, mediaInfo)
            serviceIntent.putExtra(Constants.EXTRA_URI, videoUri)
            serviceIntent.action = Constants.START_UPLOAD_SERVICE

            startService(serviceIntent)
            Log.w(TAG, "service started")
            setResult(Activity.RESULT_OK)
            finish()

        } else {
            Log.w(TAG, "service already running")
            CommonUtils.showToast(applicationContext,
                    "Last Video is still uploading...\nPlease try again later!")
        }
    }

    fun createThumb(bm: Bitmap): File {
        var bm = bm
        bm = Bitmap.createScaledBitmap(bm, 640, 480, false)

        var thumbFile: File = FileUtils.getTempImageDirectory(getActivity())
        var ostream1: FileOutputStream? = null
        try {
            ostream1 = FileOutputStream(thumbFile)
            bm.compress(Bitmap.CompressFormat.JPEG, 35, ostream1)
        } catch (e: FileNotFoundException) {

            e.printStackTrace()
        }

        try {
            ostream1!!.flush()
            ostream1.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return thumbFile
    }

    private fun getPostInfo(): VideoMakerPostInfo {
        val videoCaption = et_caption.getText().toString().trim({ it <= ' ' })
        val postInfo = VideoMakerPostInfo()

        var thumbnailHeight = "512"
        var thumbnailWidth = "512"
        thumbnailBitmap?.let {


            thumbnailWidth = it.getWidth().toString()
            thumbnailHeight = it.getHeight().toString()


            val fullName = SettingManager.getUserFullName(this)
            val userName = SettingManager.getUserName(this)
            val email = SettingManager.getUserEmail(this)
            val id = SettingManager.getUserId(this)

            val folderName = SettingManager.getUserFolderName(this)
            var userImage = ""
            var spliteEmail: Array<String>? = null
            try {
                spliteEmail = email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                userImage = folderName + "/" + spliteEmail[0] + ".jpeg"
            } catch (e: Exception) {
                userImage = SettingManager.getUserPictureURL(this)
            }


            postInfo.userId = id
            postInfo.fullName = fullName
            postInfo.userName = userName
            postInfo.location = "NA"
            postInfo.tags = "NA"
            postInfo.userDisplayPicture = userImage

            postInfo.postImageUrl = folderName
            postInfo.postVideoUrl = folderName
            postInfo.caption = videoCaption
            postInfo.height = thumbnailHeight
            postInfo.width = thumbnailWidth
            postInfo.videoFilePath = null
            postInfo.videoDuration = mediaInfo.duration.toString()
            postInfo.localPath = ""
            postInfo.thumbFilePath = createThumb(it)
        }
        return postInfo
    }


    fun getActivity(): Activity {
        return this
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et_caption.getWindowToken(), 0)
    }


}
