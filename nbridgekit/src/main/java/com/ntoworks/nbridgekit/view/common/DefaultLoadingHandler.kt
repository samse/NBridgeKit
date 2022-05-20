package nbridgekit.view.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.ntoworks.nbridgekit.R

class DefaultLoadingHandler(context: Context) :
    Dialog(context, R.style.HarfTransparentDialog), LoadingHandler {

    private var progressDialog: DefaultLoadingHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.default_loading)

        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    override fun showLoading(context: Context, msg: String) {
        try {
            hideLoading()
            progressDialog = DefaultLoadingHandler(context)
            progressDialog?.findViewById<TextView>(R.id.message)?.text = msg
            progressDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun hideLoading() {
        progressDialog?.let {
            try {
                it.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        progressDialog = null
    }

}