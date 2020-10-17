package id.mncplay.triviaquestions.views

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
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
import kotlinx.android.synthetic.main.fragment_preview.*
import kotlinx.android.synthetic.main.fragment_preview.a_layout
import kotlinx.android.synthetic.main.fragment_preview.tvCategory
import kotlinx.android.synthetic.main.fragment_preview.tvIndex
import kotlinx.android.synthetic.main.fragment_preview.txtQuestion
import java.util.*
import kotlin.concurrent.schedule


class PreviewFragment : RxBaseFragment() {

    private var toolbar: Toolbar? = null
    private var loading: Dialog? = null
    private lateinit var DbHelper : DbHelper

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
    private var trueAnswer =""
    private var answer =""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_preview, container, false)
        toolbar = view.findViewById(R.id.toolbar) as Toolbar
        loading = LoadingAlert.scoreDialog(this.context!!, this.activity!!)
        DbHelper = DbHelper(this.requireContext())


        initToolbar()
        // Inflate the layout for this fragment
        return view
    }

    private fun initToolbar() {
        toolbar?.setNavigationIcon(R.drawable.ic_back)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.title = "Preview"
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            RxBus.get().send(Utils.DASHBOARD)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        RxBus.get().send(Utils.HISTORY)
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()

        initData()

        preview_next.setOnClickListener {
            Utils.page++
            changeQuestion(Utils.page)
        }

        preview_prev.setOnClickListener {
            Utils.page++
            changeQuestion(Utils.page)
        }

        finish.setOnClickListener {
            RxBus.get().send(Utils.HISTORY)
        }

    }
    @SuppressLint("SetTextI18n")
    private fun initData(){

        tvCategory.text = ""+Utils.name_category+" - "+Utils.game_mode
        tvIndex.text = ""+(Utils.page+1)

        txtQuestion.text = Html.fromHtml(Utils.dataPrevQuestions[Utils.page].question)
        trueAnswer = Utils.dataPrevQuestions[Utils.page].trueAnswer
        answer = Utils.dataPrevQuestions[Utils.page].answer

        btnOpt1.text = Utils.dataPrevQuestions[Utils.page].optionA
        btnOpt2.text = Utils.dataPrevQuestions[Utils.page].optionB
        btnOpt3.text = Utils.dataPrevQuestions[Utils.page].optionC
        btnOpt4.text = Utils.dataPrevQuestions[Utils.page].optionD

        checkAnswer(trueAnswer, answer)

    }

    private fun checkAnswer(trueAns: String, ans:String){
        if(trueAns == ans){
            when (trueAns) {
                btnOpt1.text  -> a_layout.setBackgroundResource(R.drawable.right_gradient)
                btnOpt2.text  -> b_layout.setBackgroundResource(R.drawable.right_gradient)
                btnOpt3.text  -> c_layout.setBackgroundResource(R.drawable.right_gradient)
                btnOpt4.text  -> d_layout.setBackgroundResource(R.drawable.right_gradient)
            }
        }
        else {
            when (trueAns) {
                btnOpt1.text  -> a_layout.setBackgroundResource(R.drawable.right_gradient)
                btnOpt2.text  -> b_layout.setBackgroundResource(R.drawable.right_gradient)
                btnOpt3.text  -> c_layout.setBackgroundResource(R.drawable.right_gradient)
                btnOpt4.text  -> d_layout.setBackgroundResource(R.drawable.right_gradient)
            }
            when (ans) {
                btnOpt1.text  -> a_layout.setBackgroundResource(R.drawable.wrong_gradient)
                btnOpt2.text  -> b_layout.setBackgroundResource(R.drawable.wrong_gradient)
                btnOpt3.text  -> c_layout.setBackgroundResource(R.drawable.wrong_gradient)
                btnOpt4.text  -> d_layout.setBackgroundResource(R.drawable.wrong_gradient)
            }
        }
    }

    private fun changeQuestion(page: Int){
        if(page == 10){
            preview_next.visibility = View.INVISIBLE
            finish.visibility = View.VISIBLE
            preview_prev.visibility = View.VISIBLE

        }
        else if(page == 0){
            preview_prev.visibility = View.GONE
            finish.visibility = View.GONE

        }
        else{
            finish.visibility = View.GONE
            preview_prev.visibility = View.VISIBLE
            Timer("Waiting..", false).schedule(100) {
                RxBus.get().send(Utils.PREVIEW)
            }
        }
    }


}