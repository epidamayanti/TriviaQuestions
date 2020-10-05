package id.mncplay.triviaquestions.views

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import id.mncplay.triviaquestions.R
import id.mncplay.triviaquestions.R.array.category_id
import id.mncplay.triviaquestions.R.array.category_name
import id.mncplay.triviaquestions.adapters.CategoryAdapter
import id.mncplay.triviaquestions.commons.LoadingAlert
import id.mncplay.triviaquestions.commons.RxBaseFragment
import id.mncplay.triviaquestions.commons.RxBus
import id.mncplay.triviaquestions.commons.Utils
import id.mncplay.triviaquestions.models.DataCategory
import id.mncplay.triviaquestions.services.Service
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_mode_popup.view.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class CategoryFragment : RxBaseFragment() {

    private var toolbar: Toolbar? = null
    private var loading: Dialog? = null
    private var items: MutableList<DataCategory> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        category_list.layoutManager = LinearLayoutManager(this.context)
        category_list.adapter = CategoryAdapter(this.context!!, items){
            Utils.id_category = it.id
            Utils.name_category = it.name
            val mDialogView = LayoutInflater.from(context).inflate(
                R.layout.fragment_mode_popup,
                null
            )
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)

            val  mAlertDialog = mBuilder.setCancelable(false).show()

            mDialogView.imgClose.setOnClickListener {
                mAlertDialog.dismiss()
            }

            mDialogView.tvEasy.setOnClickListener {
                Utils.game_mode = "easy"
                initDataQuestion()
                mAlertDialog.dismiss()
            }

            mDialogView.tvMedium.setOnClickListener {
                Utils.game_mode = "medium"
                initDataQuestion()
                mAlertDialog.dismiss()
            }

            mDialogView.tvHard.setOnClickListener {
                Utils.game_mode = "hard"
                initDataQuestion()
                mAlertDialog.dismiss()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        toolbar = view.findViewById(R.id.toolbar) as Toolbar
        loading = LoadingAlert.progressDialog(this.context!!, this.activity!!)

        initToolbar()

        return view
    }

    private fun initData(){
        val id = resources.getIntArray(category_id)
        val name = resources.getStringArray(category_name)
        items.clear()
        for (i in name.indices) {
            items.add(
                DataCategory(
                    id[i],
                    name[i]
                )
            )
        }
    }

    private fun initToolbar() {
        toolbar?.setNavigationIcon(R.drawable.ic_back)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.title = "Select Category"
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            RxBus.get().send(Utils.DASHBOARD)
        }
    }

    private fun initDataQuestion(){
        loading?.show()

        subscriptions.add(provideService()
            .getData(10, Utils.id_category, Utils.game_mode, "multiple")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                loading?.dismiss()
                if (resp.response_code == 0) {
                    Utils.dataQuestions = resp.results
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

    }


    private fun provideService(): Service {
        val clientBuilder: OkHttpClient.Builder = Utils.buildClient()
        val retrofit = Retrofit.Builder()
            .baseUrl(Utils.ENDPOINT)
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