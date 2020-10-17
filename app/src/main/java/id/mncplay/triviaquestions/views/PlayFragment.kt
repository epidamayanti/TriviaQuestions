package id.mncplay.triviaquestions.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import id.mncplay.triviaquestions.R
import id.mncplay.triviaquestions.commons.LoadingAlert
import id.mncplay.triviaquestions.commons.RxBaseFragment
import id.mncplay.triviaquestions.commons.RxBus
import id.mncplay.triviaquestions.commons.Utils
import id.mncplay.triviaquestions.database.DbHelper
import id.mncplay.triviaquestions.models.DataDetailHistory
import id.mncplay.triviaquestions.models.DataHistory
import id.mncplay.triviaquestions.models.ScoreModel
import id.mncplay.triviaquestions.services.Service
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_warning.view.*
import kotlinx.android.synthetic.main.fragment_play.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule


@Suppress("DEPRECATION")
class PlayFragment : RxBaseFragment() {

    private var toolbar: Toolbar? = null
    private var loading: Dialog? = null
    var score = 0
    private val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                dialog.dismiss()
            }

            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()

            }
        }
    }
        //private lateinit var DbHelper : DbHelper


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_play, container, false)
        toolbar = view.findViewById(R.id.toolbar) as Toolbar
        loading = LoadingAlert.scoreDialog(this.context!!, this.activity!!)
        //DbHelper = DbHelper(this.requireContext())

        initToolbar()
        // Inflate the layout for this fragment
        return view
    }

    override fun onResume() {
        super.onResume()
        onClickOpt()
    }

    private fun initToolbar() {
        toolbar?.setNavigationIcon(R.drawable.ic_back)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.title = "Play"
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            if(Utils.page == 0)
                RxBus.get().send(Utils.CATEGORY)
            else{
                val builder = AlertDialog.Builder(this.context)
                builder.setMessage("Please Complete The Question")
                    .setNegativeButton("OK", dialogClickListener)
                    .setCancelable(false).show()
            }        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        RxBus.get().send(Utils.CATEGORY)
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun initData(){
        tvCategory.text = ""+Utils.name_category+" - "+Utils.game_mode
        tvIndex.text = ""+(Utils.page+1)

        val params = progress_true.layoutParams
        params.height = Utils.count_correct * 24
        progress_true.layoutParams = params

        val paramsWrong = progress_wrong.layoutParams
        paramsWrong.height = Utils.count_wrong * 24
        progress_wrong.layoutParams = paramsWrong

        val answerOpt: MutableList<String> = mutableListOf()
        txtQuestion.text = Html.fromHtml(Utils.dataQuestions[Utils.page].question)

        answerOpt.add(Utils.dataQuestions[Utils.page].correct_answer)
        Utils.true_answer = Utils.dataQuestions[Utils.page].correct_answer
        answerOpt.addAll(Utils.dataQuestions[Utils.page].incorrect_answers)
        randomOpt(answerOpt)

        txtTrueQuestion.text = ""+Utils.count_correct
        txtFalseQuestion.text = ""+Utils.count_wrong
    }


    private fun onClickOpt(){
        Log.d("page", "" + Utils.page)
        var idHistory = ""

        a_layout.setOnClickListener {
            checkAnswer("a")

        }
        b_layout.setOnClickListener {
            checkAnswer("b")

        }
        c_layout.setOnClickListener {
            checkAnswer("c")

        }
        d_layout.setOnClickListener {
            checkAnswer("d")

        }

        Utils.id_history = idHistory
    }

    private fun checkAnswer(opt: String){
        when (Utils.true_answer) {
            btnOpt1.text.toString() -> {
                a_layout.setBackgroundResource(R.drawable.right_gradient)
                when {
                    opt == "a" -> {
                        Utils.count_correct++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    opt == "b" -> {
                        b_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    opt == "c" -> {
                        c_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    opt == "d" -> {
                        d_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                }
            }
            btnOpt2.text.toString() -> {
                b_layout.setBackgroundResource(R.drawable.right_gradient)
                when (opt) {
                    "a" -> {
                        a_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    "b" -> {
                        Utils.count_correct++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    "c" -> {
                        c_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    "d" -> {
                        d_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                }
            }
            btnOpt3.text.toString() -> {
                c_layout.setBackgroundResource(R.drawable.right_gradient)
                when (opt) {
                    "b" -> {
                        b_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    "c" -> {
                        Utils.count_correct++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    "a" -> {
                        a_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    "d" -> {
                        d_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                }
            }
            btnOpt4.text.toString() -> {
                d_layout.setBackgroundResource(R.drawable.right_gradient)
                when (opt) {
                    "b" -> {
                        b_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    "c" -> {
                        b_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    "a" -> {
                        a_layout.setBackgroundResource(R.drawable.wrong_gradient)
                        Utils.count_wrong++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                    "d" -> {
                        Utils.count_correct++
                        Utils.page++
                        changeQuestion(Utils.page)
                    }
                }
            }
        }

    }

    private fun randomOpt(answerOpt: MutableList<String>){
        val randA = getRandomNumber(answerOpt.size - 1)
        btnOpt1.text = Html.fromHtml(answerOpt[randA])
        answerOpt.removeAt(randA)

        val randB = getRandomNumber(answerOpt.size - 1)
        btnOpt2.text = Html.fromHtml(answerOpt[randB])
        answerOpt.removeAt(randB)

        val randC = getRandomNumber(answerOpt.size - 1)
        btnOpt3.text = Html.fromHtml(answerOpt[randC])
        answerOpt.removeAt(randC)

        val randD = getRandomNumber(answerOpt.size - 1)
        btnOpt4.text = Html.fromHtml(answerOpt[randD])
        answerOpt.removeAt(randD)
    }

    private fun changeQuestion(page: Int){
        if(page == 10){
            loading?.show()
            if(Utils.isLogin){
                when (Utils.game_mode) {
                    "easy" -> Utils.score = Utils.score+(Utils.count_correct*1)
                    "medium" -> Utils.score = Utils.score+(Utils.count_correct*2)
                    "hard" -> Utils.score = Utils.score+(Utils.count_correct*5)
                }
                updateData(Utils.id_player, Utils.username, ""+Utils.score)
            }
            else{
                loading?.dismiss()
                Timer("Waiting..", false).schedule(100) {
                    RxBus.get().send(Utils.COMPLETE)
                }
            }
        }
        else{
            Timer("Waiting..", false).schedule(100) {
                RxBus.get().send(Utils.PLAY)
            }
        }
    }

    private fun getRandomNumber(max: Int): Int {
        return (Math.random() * (max - 0) + 0).toInt()
    }

    private fun updateData(id:String,username:String, score:String){
        val data = ScoreModel(id, username, score)
        subscriptions.add(providUpdateService().scoreUpdate(data)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    user ->
                loading?.dismiss()
                if (user.status) {
                    RxBus.get().send(Utils.COMPLETE)
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

                    mDialogView.content.setText("SORRY YOUR SCORE DOESN'T UPDATE")


                } else {

                    val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_warning, null)
                    val mBuilder = AlertDialog.Builder(context)
                        .setView(mDialogView)

                    val  mAlertDialog = mBuilder.setCancelable(false).show()

                    mDialogView.bt_close.setOnClickListener {
                        mAlertDialog.dismiss()
                    }

                    mDialogView.title.setText("UPDATE SCORE FAILED! ")

                    mDialogView.content.setText(err.localizedMessage)

                }
            })
        )
    }



    private fun providUpdateService(): Service {
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

    @SuppressLint("SimpleDateFormat")
    private fun getTime():String{
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        return sdf.format(Date())
    }

}