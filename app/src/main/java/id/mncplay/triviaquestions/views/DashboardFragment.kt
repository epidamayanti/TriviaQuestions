package id.mncplay.triviaquestions.views

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText
import id.mncplay.triviaquestions.R
import id.mncplay.triviaquestions.commons.*
import id.mncplay.triviaquestions.models.LoginModel
import id.mncplay.triviaquestions.services.Service
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_login_popup.view.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class DashboardFragment : RxBaseFragment() {

    private var sharedPrefManager: SharedPrefManager? = null
    private var loading: Dialog? = null
    private val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                sharedPrefManager?.saveSPBoolean(SharedPrefManager.LOGIN, false)
                sharedPrefManager?.saveSPString(SharedPrefManager.USERNAME, "")
                Utils.isLogin = false
                RxBus.get().send(Utils.LOGIN)
            }

            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefManager = SharedPrefManager(this.requireContext())
        loading = LoadingAlert.loginDialog(this.context!!, this.activity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onResume() {
        super.onResume()
        lytPlay.setOnClickListener {
            RxBus.get().send(Utils.CATEGORY)
        }

        imgLogout.setOnClickListener {
            if(!sharedPrefManager?.spLogin!!){
                val mDialogView = LayoutInflater.from(context).inflate(R.layout.fragment_login_popup, null)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)

                val  mAlertDialog = mBuilder.setCancelable(false).show()

                mDialogView.imgClose.setOnClickListener {
                    mAlertDialog.dismiss()
                }

                mDialogView.btnSignUp.setOnClickListener {
                    RxBus.get().send(Utils.REGISTER)
                    mAlertDialog.dismiss()
                }

                mDialogView.btnSignIn.setOnClickListener {
                    onFocus(mDialogView.edtUsername, mDialogView.edtUsername, mAlertDialog)
                }
            }
            else{
                val builder = AlertDialog.Builder(context)
                builder
                    .setMessage("Are You Sure to LOGOUT ?")
                    .setPositiveButton("YES", dialogClickListener)
                    .setNegativeButton("NO", dialogClickListener)
                    .setCancelable(false)
                    .show()
            }
        }

        imgAbout.setOnClickListener {
            RxBus.get().send(Utils.ABOUT)
        }

        imgProfile.setOnClickListener {
            if(!sharedPrefManager?.spLogin!!){
                val mDialogView = LayoutInflater.from(context).inflate(R.layout.fragment_login_popup, null)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)

                val  mAlertDialog = mBuilder.setCancelable(false).show()

                mDialogView.imgClose.setOnClickListener {
                    mAlertDialog.dismiss()
                }

                mDialogView.btnSignUp.setOnClickListener {
                    RxBus.get().send(Utils.REGISTER)
                    mAlertDialog.dismiss()
                }

                mDialogView.btnSignIn.setOnClickListener {
                    onFocus(mDialogView.edtUsername, mDialogView.edtUsername, mAlertDialog)
                }
            }
            else{
                RxBus.get().send(Utils.PROFILE)
            }
        }

        imgLeaderboard.setOnClickListener {
            RxBus.get().send(Utils.LEADERBOARD)
        }
    }

    private fun onFocus(
        username: TextInputEditText,
        pass: TextInputEditText,
        mAlertDialog: AlertDialog
    ) {
        when {
            TextUtils.isEmpty(username.text.toString()) -> {
                username.error = "username cannot be empty"
                username.requestFocus()
                return
            }
            TextUtils.isEmpty(pass.text.toString()) -> {
                pass.error = "Password cannot be empty"
                pass.requestFocus()
                return
            }
            else -> {
                loading?.show()
                validateUser(""+ username.text.toString(),""+pass.text.toString())
                mAlertDialog.dismiss()
            }
        }
    }

    private fun validateUser(username:String, pass:String) {
        val login = LoginModel(username, pass)

        subscriptions.add(provideLoginService().login(login)
            .retry(3)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    user ->
                loading?.dismiss()
                if (user.status) {
                    val builder = AlertDialog.Builder(context)
                    builder
                        .setMessage("Hallo "+user.data.name)
                        .setPositiveButton("OK", dialogClickListener)
                        .setCancelable(false)
                        .show()

                    RxBus.get().send(Utils.DASHBOARD)
                    Utils.name_player = user.data.name
                    sharedPrefManager?.saveSPBoolean(SharedPrefManager.LOGIN, true)
                    sharedPrefManager?.saveSPString(SharedPrefManager.NAME, Utils.name_player)
                    sharedPrefManager?.saveSPString(SharedPrefManager.USERNAME, username)

                } else {
                    val builder = AlertDialog.Builder(context)
                    builder
                        .setMessage(""+user.message)
                        .setPositiveButton("OK", dialogClickListener)
                        .setCancelable(false)
                        .show()

                }
            }, {
                    err ->
                loading?.dismiss()
                val builder = AlertDialog.Builder(context)
                builder
                    .setMessage("Login Failed: "+err.localizedMessage)
                    .setPositiveButton("OK", dialogClickListener)
                    .setCancelable(false)
                    .show()
            }))
    }

    private fun provideLoginService(): Service {
        val clientBuilder: OkHttpClient.Builder = Utils.buildClient()
        val retrofit = Retrofit.Builder()
            .baseUrl(Utils.ENDPOINT_POST)
            .client(clientBuilder
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
        return retrofit.create(Service::class.java)
    }

}