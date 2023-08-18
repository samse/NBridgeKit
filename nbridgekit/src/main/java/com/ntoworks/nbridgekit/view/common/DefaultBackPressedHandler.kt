package com.ntoworks.nbridgekit.view.common

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.ntoworks.nbridgekit.R
import com.ntoworks.nbridgekit.view.BaseActivity
import kotlin.system.exitProcess

class DefaultBackPressedHandler(val context: Context): BackPressedHandler {
    var mAlertDialog: AlertDialog? = null

    override fun onBackPressed(): Boolean {
        if (context as BaseActivity != null) {
            if ((context as BaseActivity).webWindow.webView.canGoBack()) {
                (context as BaseActivity).webWindow.webView.goBack()
            } else {
                if (mAlertDialog == null) {
                    mAlertDialog = AlertDialog.Builder(context)
                        .setMessage(R.string.warning_exit_app)
                        .setPositiveButton(
                            R.string.confirm,
                            object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    (context as Activity).finishAffinity()
                                    exitProcess(0)
                                }
                            })
                        .setCancelable(true)
                        .create()
                }
                mAlertDialog?.show()
            }
            return true
        }
        return false
    }
}