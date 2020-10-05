package id.mncplay.triviaquestions.views

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.mncplay.triviaquestions.R
import id.mncplay.triviaquestions.commons.*
import id.mncplay.triviaquestions.models.LoginModel
import id.mncplay.triviaquestions.services.Service
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.btnSignUp
import kotlinx.android.synthetic.main.fragment_login.edtPassword
import kotlinx.android.synthetic.main.fragment_login.edtUsername
import kotlinx.android.synthetic.main.fragment_register.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LoginFragment : RxBaseFragment() {

    private var sharedPrefManager: SharedPrefManager? = null
    private var username = ""
    private var pass = ""
    private var loading: Dialog? = null
    private val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                dialog.dismiss()
            }

            DialogInterface.BUTTON_NEGATIVE -> {
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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onResume() {
        super.onResume()
        edtUsername.setText("")
        edtPassword.setText("")

        guest.setOnClickListener {
            RxBus.get().send(Utils.DASHBOARD)
        }

        btnLogin.setOnClickListener {
            username = edtUsername.text.toString()
            pass = edtPassword.text.toString()
            onFocus()
        }

        btnSignUp.setOnClickListener {
            RxBus.get().send(Utils.REGISTER)
        }

    }

    private fun onFocus(){
        when {
            TextUtils.isEmpty(edtUsername.text.toString()) -> {
                edtUsername.error = "username cannot be empty"
                edtUsername.requestFocus()
                return
            }
            TextUtils.isEmpty(edtPassword.text.toString()) -> {
                edtPassword.error = "Password cannot be empty"
                edtPassword.requestFocus()
                return
            }
            else -> {
                loading?.show()
                validateUser(""+edtUsername.text.toString(),""+edtPassword.text.toString())
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
                    Utils.username = user.data.username
                    Utils.score = user.data.score.toInt()
                    Utils.id_player = user.data.id
                    Utils.isLogin = true
                    sharedPrefManager?.saveSPBoolean(SharedPrefManager.LOGIN, true)
                    sharedPrefManager?.saveSPString(SharedPrefManager.NAME, Utils.name_player)
                    sharedPrefManager?.saveSPString(SharedPrefManager.USERNAME, Utils.username)
                    sharedPrefManager?.saveSPString(SharedPrefManager.ID_PLAYER, Utils.id_player)
                    sharedPrefManager?.saveSPInt(SharedPrefManager.SCORE, Utils.score)

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