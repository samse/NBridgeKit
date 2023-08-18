package com.ntoworks.nbridgekit.view.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.WindowManager
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import com.ntoworks.nbridgekit.R
import nbridgekit.view.common.DialogHandler
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

open class BridgeWebChromeClient(val context: Activity, val dialogHandler: DialogHandler?): WebChromeClient() {
    val TYPE_IMAGE = "image/*"
    val INPUT_FILE_REQUEST_CODE = 1

    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mCameraPhotoPath: String? = null

    // 동영상 전체화면을 위한 객체
    private var videoView: View? = null
    private var videoViewCallBack: CustomViewCallback? = null
    private var pendingOrientation = 0

    private var mFullscreenContainer: FrameLayout? = null
    private val coverScreenParams =
        FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

    // 웹뷰 동영상 전체 화면을 위한 클래스
    inner class FullScreenHolder(ctx: Context) : FrameLayout(ctx) {
        init {
            setBackgroundColor(ContextCompat.getColor(ctx, R.color.black))
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            return false
        }
    }

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        if (dialogHandler != null) return dialogHandler.onJsAlert(view, url, message, result)
        else return super.onJsAlert(view, url, message, result)
    }

    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        if (dialogHandler != null) return dialogHandler.onJsConfirm(view, url, message, result)
        else return super.onJsConfirm(view, url, message, result)
    }


    // For Android Version < 3.0
    open fun openFileChooser(uploadMsg: ValueCallback<Uri>?) {
        mUploadMessage = uploadMsg
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = TYPE_IMAGE
        (context as Activity).startActivityForResult(
            intent,
            INPUT_FILE_REQUEST_CODE
        )
    }

    // For 3.0 <= Android Version < 4.1
    open fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String) {
        openFileChooser(uploadMsg, acceptType, "")
    }

    // For 4.1 <= Android Version < 5.0
    open fun openFileChooser(uploadFile: ValueCallback<Uri>, acceptType: String, capture: String) {
        Log.d(javaClass.name, "openFileChooser : $acceptType/$capture")
        mUploadMessage = uploadFile
        imageChooser()
    }

    // For Android Version 5.0+
    // Ref: https://github.com/GoogleChrome/chromium-webview-samples/blob/master/input-file-example/app/src/main/java/inputfilesample/android/chrome/google/com/inputfilesample/MainFragment.java
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>?>, fileChooserParams: FileChooserParams?
    ): Boolean {
//        println("WebViewActivity A>5, OS Version : " + Build.VERSION.SDK_INT + "\t onSFC(WV,VCUB,FCP), n=3")
//        if (mFilePathCallback != null) {
//            mFilePathCallback.onReceiveValue(null)
//        }
//        mFilePathCallback = filePathCallback
        imageChooser()
        return true
    }


    // 동영상 전체화면 전환
    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        videoView?.let {
            callback.onCustomViewHidden()
            return
        }

        pendingOrientation = context.requestedOrientation
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        val decor = context.window.decorView as FrameLayout
        mFullscreenContainer = FullScreenHolder(context)
        mFullscreenContainer?.addView(view, coverScreenParams)
        decor.addView(mFullscreenContainer, coverScreenParams)
        videoView = view
        setFullscreen(true)
        videoViewCallBack = callback
    }

    // 동영상 전체화면 종료
    override fun onHideCustomView() {
        if (videoView == null) {
            return
        }
        setFullscreen(false)
        val decor = context.window.decorView as FrameLayout
        decor.removeView(mFullscreenContainer)
        mFullscreenContainer = null
        videoView = null
        videoViewCallBack?.onCustomViewHidden()
        context.requestedOrientation =
            pendingOrientation
    }

    open fun imageChooser() {
        var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent!!.resolveActivity(context.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Log.e(javaClass.name, "Unable to create Image File", ex)
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.absolutePath
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile)
                )
            } else {
                takePictureIntent = null
            }
        }
        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.type =
            TYPE_IMAGE
        val intentArray: Array<Intent?> = takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        context.startActivityForResult(
            chooserIntent,
            INPUT_FILE_REQUEST_CODE
        )
    }

    /**
     * More info this method can be found at
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun createImageFile(): File? {
        // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

//    open fun getResultUri(data: Intent?): Uri? {
//        var result: Uri? = null
//        if (data == null || TextUtils.isEmpty(data.dataString)) {
//            // If there is not data, then we may have taken a photo
//            if (mCameraPhotoPath != null) {
//                result = Uri.parse(mCameraPhotoPath)
//            }
//        } else {
//            var filePath: String? = ""
//            filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                data.dataString
//            } else {
//                "file:" + RealPathUtil.getRealPath(context, data.data)
//            }
//            result = Uri.parse(filePath)
//        }
//        return result
//    }

    private fun setFullscreen(enabled: Boolean) {
        val window = context.window

        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let {
                    it.hide(WindowInsetsCompat.Type.systemBars())
                    it.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                videoView?.let {
                    it.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                }
            }
        } else {
            videoView?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.setDecorFitsSystemWindows(true)
                    window.insetsController?.show(WindowInsetsCompat.Type.systemBars())
                } else {
                    val winParams = window.attributes
                    val bits = WindowManager.LayoutParams.FLAG_FULLSCREEN
                    winParams.flags = winParams.flags and bits.inv()
                    it.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    window.attributes = winParams
                }
            }
        }
    }

}