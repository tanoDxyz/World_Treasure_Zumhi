package com.geocache.zumhi.worldtreasure.tanveer

import android.content.Context
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.geocache.zumhi.worldtreasure.R

class ProgressDialog(context: Context, text: String, cancelable:Boolean) {
    private lateinit var pd: MaterialDialog
    init {
        pd = MaterialDialog(context).apply {
            cornerRadius(16f)
            customView(R.layout.progress_dialog)
            getCustomView().findViewById<TextView>(R.id.textView).setText(text)
            cancelable(cancelable)
        }
    }


    fun show() {
        try {
            pd.show()
        }catch (ex:Exception) {}
    }

    fun dismiss() {
        try {
            pd.dismiss()
        }catch (ex:Exception) {

        }
    }



    var text:String = ""
    set(value) {
        pd.getCustomView().findViewById<TextView>(R.id.textView).setText(value)
    }
}