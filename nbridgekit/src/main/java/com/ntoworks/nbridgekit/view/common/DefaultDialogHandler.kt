package nbridgekit.view.common

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.webkit.JsResult
import android.webkit.WebView


class DefaultDialogHandler(val context: Context) : DialogHandler {
    var mAlertDialog: AlertDialog? = null

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        if (mAlertDialog == null) {
            mAlertDialog = AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.ok, object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        result!!.confirm()
                        mAlertDialog?.dismiss()
                        mAlertDialog = null
                    }
                })
                .setCancelable(false)
                .create()
        }
        mAlertDialog?.show()
        return true
    }

    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        if (mAlertDialog == null) {
            mAlertDialog = AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.ok, object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        result!!.confirm()
                        mAlertDialog?.dismiss()
                        mAlertDialog = null
                    }
                })
                .setNegativeButton(R.string.cancel, object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        result!!.cancel()
                        mAlertDialog?.dismiss()
                        mAlertDialog = null
                    }
                })
                .setCancelable(false)
                .create()
        }
        mAlertDialog?.show()
        return true
    }
}