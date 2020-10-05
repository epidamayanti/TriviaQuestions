package id.mncplay.triviaquestions.commons

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import id.mncplay.triviaquestions.R

class LoadingAlert {

    companion object {
        fun progressDialog(context:Context, activity:Activity):Dialog{
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context)
                .inflate(R.layout.loading_alert, activity.findViewById<ViewGroup>(R.id.custom_toast_layout))
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(
                    ColorDrawable(Color.TRANSPARENT))
            return dialog
        }
        fun regisDialog(context: Context, activity: Activity): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(
                R.layout.register_alert,
                activity.findViewById<ViewGroup>(R.id.custom_toast_layout)
            )
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            return dialog
        }
        fun scoreDialog(context: Context, activity: Activity): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(
                R.layout.score_alert,
                activity.findViewById<ViewGroup>(R.id.custom_toast_layout)
            )
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            return dialog
        }

        fun loginDialog(context: Context, activity: Activity): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.login_alert, activity.findViewById<ViewGroup>(R.id.custom_toast_layout))
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            return dialog
        }

    }
}
