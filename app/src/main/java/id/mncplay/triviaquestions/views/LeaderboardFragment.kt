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
import androidx.recyclerview.widget.LinearLayoutManager
import id.mncplay.triviaquestions.R
import id.mncplay.triviaquestions.adapters.LeaderboardAdapter
import id.mncplay.triviaquestions.commons.*
import id.mncplay.triviaquestions.models.DataUser
import id.mncplay.triviaquestions.services.Service
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import kotlinx.android.synthetic.main.fragment_leaderboard.tvName
import kotlinx.android.synthetic.main.fragment_leaderboard.tvScore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LeaderboardFragment : RxBaseFragment() {

    private var sharedPrefManager: SharedPrefManager? = null
    private var toolbar: Toolbar? = null
    private var loading: Dialog? = null
    private var items: MutableList<DataUser> = mutableListOf()
    private var rank = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lead_list.layoutManager = LinearLayoutManager(this.context)
        initData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        sharedPrefManager = SharedPrefManager(this.requireContext())
        toolbar = view.findViewById(R.id.toolbar) as Toolbar
        loading = LoadingAlert.progressDialog(this.context!!, this.activity!!)

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
                if(resp.status){
                    items = resp.data
                    lead_list.adapter = LeaderboardAdapter(this.context!!, items){}
                    for(i in items.indices){
                        if(items[i].username == Utils.username){
                            rank = i+1
                            tvRankPlayer.text = ""+rank
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
            }) { error ->
                loading?.dismiss()
                val builder = AlertDialog.Builder(this.context!!)
                builder.setMessage("ERROR TO GET DATA BECAUSE : \n " + error.localizedMessage)
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
        )
        if(sharedPrefManager?.spLogin!!){
            tvName.text = Utils.username
            tvScore.text = ""+Utils.score
        }

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