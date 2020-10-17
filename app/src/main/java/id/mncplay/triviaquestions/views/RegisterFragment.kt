package id.mncplay.triviaquestions.views

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import id.mncplay.triviaquestions.R
import id.mncplay.triviaquestions.commons.LoadingAlert
import id.mncplay.triviaquestions.commons.RxBaseFragment
import id.mncplay.triviaquestions.commons.RxBus
import id.mncplay.triviaquestions.commons.Utils
import id.mncplay.triviaquestions.models.RegisModel
import id.mncplay.triviaquestions.services.Service
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_warning.view.*
import kotlinx.android.synthetic.main.fragment_register.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RegisterFragment : RxBaseFragment() {

    private var loading: Dialog? = null
    private val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                dialog.dismiss()
                RxBus.get().send(Utils.LOGIN)
            }

            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = LoadingAlert.regisDialog(this.context!!, this.activity!!)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onResume() {
        super.onResume()
        btnSignUp.setOnClickListener {
            onFocus()
        }
    }

    private fun onFocus(){
        when {
            TextUtils.isEmpty(edtUsername.text.toString()) -> {
                edtUsername.error = "username cannot be empty"
                edtUsername.requestFocus()
                return
            }
            TextUtils.isEmpty(edtName.text.toString()) -> {
                edtName.error = "name cannot be empty"
                edtName.requestFocus()
                return
            }
            TextUtils.isEmpty(edtPassword.text.toString()) -> {
                edtPassword.error = "Password cannot be empty"
                edtPassword.requestFocus()
                return
            }
            else -> {
                loading?.show()
                uploadData(""+edtUsername.text.toString(), ""+edtName.text.toString(), ""+edtPassword.text.toString())
            }
        }
    }

    private fun uploadData(username:String, name:String, password:String){
        val data = RegisModel(name, username, password)
        subscriptions.add(providRegistService().register(data)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    user ->
                loading?.dismiss()
                if (user.status) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage(user.message).setPositiveButton("OK", dialogClickListener).setCancelable(false).show()

                } else {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage(user.message).setNegativeButton("OK", dialogClickListener).setCancelable(false).show()
                }
            }, {
                    err ->
                loading?.dismiss()
                if (err.localizedMessage.contains("resolve host")) {
                    val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_no_internet, null)
                    val mBuilder = AlertDialog.Builder(context)
                        .setView(mDialogView)

                    val  mAlertDialog = mBuilder.setCancelable(false).show()

                    mDialogView.bt_close.setOnClickListener {
                        mAlertDialog.dismiss()
                    }

                } else {

                    val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_warning, null)
                    val mBuilder = AlertDialog.Builder(context)
                        .setView(mDialogView)

                    val  mAlertDialog = mBuilder.setCancelable(false).show()

                    mDialogView.bt_close.setOnClickListener {
                        mAlertDialog.dismiss()
                    }

                    mDialogView.title.setText("REGISTER FAILED! ")

                    mDialogView.content.setText(err.localizedMessage)

                }
            })
        )
    }

    private fun providRegistService(): Service {
        val clientBuilder: OkHttpClient.Builder = Utils.buildClient()

        val retrofit = Retrofit.Builder()
            .baseUrl(Utils.ENDPOINT_POST)
            .client(clientBuilder
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
        return retrofit.create(Service::class.java)
    }

}