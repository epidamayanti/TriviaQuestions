package id.mncplay.triviaquestions.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import id.mncplay.triviaquestions.R
import id.mncplay.triviaquestions.adapters.LeaderboardAdapter
import id.mncplay.triviaquestions.commons.*
import id.mncplay.triviaquestions.database.DbHelper
import id.mncplay.triviaquestions.models.DataUser
import id.mncplay.triviaquestions.services.Service
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_warning.view.*
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LeaderboardFragment : RxBaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var sharedPrefManager: SharedPrefManager? = null
    private var toolbar: Toolbar? = null
    private var loading: Dialog? = null
    private var items: MutableList<DataUser> = mutableListOf()
    private var rank = 0
    lateinit var DbHelper : DbHelper
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lead_list.layoutManager = LinearLayoutManager(this.context)
        mSwipeRefreshLayout?.setOnRefreshListener(this)

        if(DbHelper.readAllUsers().isEmpty())
            initData()
        else{
            items.clear()
            items = DbHelper.readAllUsers()
            lead_list.adapter = LeaderboardAdapter(this.context!!, items) {}
        }


    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        sharedPrefManager = SharedPrefManager(this.requireContext())
        toolbar = view.findViewById(R.id.toolbar) as Toolbar
        DbHelper = DbHelper(this.requireContext())
        loading = LoadingAlert.progressDialog(this.requireContext(), this.requireActivity())
        mSwipeRefreshLayout = view.findViewById(R.id.refresh);



        initToolbar()

        return view
    }

    private fun initToolbar() {
        toolbar?.setNavigationIcon(R.drawable.ic_back)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.title = "Leaderboard"
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
                if (resp.status) {
                    DbHelper.deleteAllUser()

                    items = resp.data
                    lead_list.adapter = LeaderboardAdapter(this.context!!, items) {}
                    for (i in items.indices) {
                        if (items[i].username == Utils.username) {
                            rank = i + 1
                            tvRankPlayer.text = "" + rank
                            Utils.score = items[i].score.toInt()
                        }
                        DbHelper.insertUser(
                            DataUser(
                                items[i].id,
                                items[i].username,
                                items[i].name,
                                items[i].score
                            )
                        )
                    }
                    Log.d("DB", "" + DbHelper.readAllUsers())

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
                if(sharedPrefManager?.spLogin!!){
                    tvName.text = Utils.name_player
                    tvScore.text = ""+Utils.score
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

                    mDialogView.title.setText("SCORE DATA DOESN'T UPDATE! ")

                    mDialogView.content.setText(err.localizedMessage)

                }

            }
        )

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

    override fun onRefresh() {
        mSwipeRefreshLayout?.setRefreshing(false)
        initData()
    }


}