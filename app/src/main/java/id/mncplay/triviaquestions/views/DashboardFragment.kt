package id.mncplay.triviaquestions.views

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.dialog_warning.view.*
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.lytPlay
import kotlinx.android.synthetic.main.fragment_login_popup.view.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class DashboardFragment : RxBaseFragment() {

    private var sharedPrefManager: SharedPrefManager? = null
    private var loading: Dialog? = null
    private var loadQuest: Dialog? = null
    @SuppressLint("ResourceAsColor")
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
        loadQuest = LoadingAlert.progressDialog(this.context!!, this.activity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    @SuppressLint("ResourceAsColor")
    override fun onResume() {
        super.onResume()

        lytPlay.setOnClickListener {
            lytPlay.setBackgroundResource(R.drawable.button_bg_rec_press)
            RxBus.get().send(Utils.CATEGORY)
        }

        playRandom.setOnClickListener {
            initDataQuestion()
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
                    //imgProfile.setBackgroundColor(R.color.white)
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
            //imgLeaderboard.setBackgroundColor(R.color.light_gray)
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

    private fun initDataQuestion(){
        loadQuest?.show()

        subscriptions.add(provideService()
            .getDataRandom(10,"multiple")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                loadQuest?.dismiss()
                if (resp.response_code == 0) {
                    Utils.dataQuestions = resp.results
                    Utils.game_mode = resp.results[0].difficulty
                    Utils.name_category = resp.results[0].category
                    Utils.status = "random"
                    RxBus.get().send(Utils.PLAY)
                } else {
                    val builder = AlertDialog.Builder(this.context!!)
                    builder.setMessage("DATA TIDAK TERSEDIA")
                        .setPositiveButton(
                            "OK"
                        ) { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    dialog.dismiss()
                                }
                            }
                        }.setCancelable(false).show()
                }
            }) { err ->
                loadQuest?.dismiss()
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

                    mDialogView.title.setText("FAILED TO GET DATA CATEGORY! ")

                    mDialogView.content.setText(err.localizedMessage)

                }
            }
        )
    }

    private fun provideService(): Service {
        val clientBuilder: OkHttpClient.Builder = Utils.buildClient()
        val retrofit = Retrofit.Builder()
            .baseUrl(Utils.ENDPOINT)
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