package id.mncplay.triviaquestions.views

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import id.mncplay.triviaquestions.R
import id.mncplay.triviaquestions.commons.RxBaseFragment
import id.mncplay.triviaquestions.commons.RxBus
import id.mncplay.triviaquestions.commons.Utils
import id.mncplay.triviaquestions.database.DbHelper
import kotlinx.android.synthetic.main.fragment_complete.*

class CompleteFragment : RxBaseFragment() {

    private var toolbar: Toolbar? = null
    private lateinit var DbHelper : DbHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DbHelper = DbHelper(this.requireContext())
        initToolbar()
        initData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_complete, container, false)
        toolbar = view.findViewById(R.id.toolbar) as Toolbar

        return view
    }

    @SuppressLint("ResourceAsColor")
    override fun onResume() {
        super.onResume()
        tvPlayNext.setOnClickListener {
            RxBus.get().send(Utils.CATEGORY)
        }
        tvLeaderBoard.setOnClickListener {
            RxBus.get().send(Utils.LEADERBOARD)
        }
        tvHome.setOnClickListener {
            RxBus.get().send(Utils.DASHBOARD)
        }
        tvPreview.setOnClickListener{
            Utils.dataPrevQuestions = DbHelper.readDetailHistory(Utils.id_history)
            RxBus.get().send(Utils.PREVIEW)
        }
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.title = "Result"
    }

    @SuppressLint("SetTextI18n")
    private fun initData(){
        countScore()

        if(Utils.name_player != "")
            user.text = Utils.name_player
        else
            user.text = "GUEST"

        right.text = ""+Utils.count_correct
        wrong.text = ""+Utils.count_wrong

        val result = (Utils.count_correct * 100) / 10
        persen.text = "$result%"

        Utils.count_correct = 0
        Utils.count_wrong = 0
        Utils.status = ""
        Utils.page = 0
    }

    @SuppressLint("SetTextI18n")
    private fun countScore(){
        when (Utils.game_mode) {
            "easy" -> tvTtlScore.text = ""+(Utils.count_correct*1)
            "medium" -> tvTtlScore.text = ""+(Utils.count_correct*2)
            "hard" -> tvTtlScore.text = ""+(Utils.count_correct*5)
        }
    }

}