package id.mncplay.triviaquestions.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import id.mncplay.triviaquestions.R
import id.mncplay.triviaquestions.commons.*
import id.mncplay.triviaquestions.models.DataUser
import id.mncplay.triviaquestions.services.Service
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_warning.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ProfileFragment : RxBaseFragment() {

    private var sharedPrefManager: SharedPrefManager? = null
    private var toolbar: Toolbar? = null
    private var loading: Dialog? = null
    private var items: MutableList<DataUser> = mutableListOf()
    private var rank = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPrefManager = SharedPrefManager(this.requireContext())
        toolbar = view.findViewById(R.id.toolbar) as Toolbar
        loading = LoadingAlert.progressDialog(this.context!!, this.activity!!)

        initToolbar()
        // Inflate the layout for this fragment
        return view
    }

    private fun initToolbar() {
        toolbar?.setNavigationIcon(R.drawable.ic_back)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.title = "Pofile"
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            RxBus.get().send(Utils.DASHBOARD)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData(){
        loading?.show()

        subscriptions.add(provideService()
            .leaderboard()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                loading?.dismiss()
                if(resp.status){
                    items = resp.data
                    for(i in items.indices){
                        if(items[i].username == Utils.username){
                            rank = i+1
                            tvRank.text = ""+rank
                            break
                        }
                    }
                }
                else {
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

                    mDialogView.title.setText("GET PROFILE FAILED! ")

                    mDialogView.content.setText(err.localizedMessage)

                }

            }
        )
            tvName.text = Utils.username
            tvScore.text = ""+Utils.score

    }

    override fun onResume() {
        super.onResume()

        tvPlayNext.setOnClickListener {
            RxBus.get().send(Utils.CATEGORY)
        }
        tvLeaderBoard.setOnClickListener {
            RxBus.get().send(Utils.LEADERBOARD)
        }
        tvLogout.setOnClickListener {
            sharedPrefManager?.saveSPBoolean(SharedPrefManager.LOGIN, false)
            sharedPrefManager?.saveSPString(SharedPrefManager.USERNAME, "")
            Utils.isLogin = false
            RxBus.get().send(Utils.LOGIN)
        }
        /*tvPreview.setOnClickListener{
            RxBus.get().send(Utils.HISTORY)
        }*/
    }

    private fun provideService(): Service {
        val clientBuilder: OkHttpClient.Builder = Utils.buildClient()
        val retrofit = Retrofit.Builder()
            .baseUrl(Utils.ENDPOINT_POST)
            .client(
                clientBuilder
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
        return retrofit.create(Service::class.java)
    }

}