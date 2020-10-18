package id.mncplay.triviaquestions.views

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import id.mncplay.triviaquestions.R
import id.mncplay.triviaquestions.adapters.HistoryAdapter
import id.mncplay.triviaquestions.commons.LoadingAlert
import id.mncplay.triviaquestions.commons.RxBaseFragment
import id.mncplay.triviaquestions.commons.RxBus
import id.mncplay.triviaquestions.commons.Utils
import id.mncplay.triviaquestions.database.DbHelper
import id.mncplay.triviaquestions.models.DataDetailHistory
import id.mncplay.triviaquestions.models.DataHistory
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : RxBaseFragment() {

    private var toolbar: Toolbar? = null
    private var loading: Dialog? = null
    private var items: MutableList<DataHistory> = mutableListOf()
    private lateinit var DbHelper : DbHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DbHelper = DbHelper(this.requireContext())

        initData()

        history_list.layoutManager = LinearLayoutManager(this.context)
        history_list.adapter = HistoryAdapter(this.context!!, items){
            Utils.id_history = it.id
            Utils.name_category = it.category
            Utils.game_mode = it.mode
            Utils.dataPrevQuestions = DbHelper.readDetailHistory(Utils.id_history)
            RxBus.get().send(Utils.PREVIEW)
        }
    }
    
     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view = inflater.inflate(R.layout.fragment_history, container, false)
         toolbar = view.findViewById(R.id.toolbar) as Toolbar
         loading = LoadingAlert.progressDialog(this.context!!, this.activity!!)

         initToolbar()

         return view
    }

    private fun initData(){
        items.clear()
        items = DbHelper.readAllHistoryByName(Utils.username)
    }

    private fun initDataDetail(idHistory: String): MutableList<DataDetailHistory>{
        return DbHelper.readDetailHistory(idHistory)
    }

    private fun initToolbar() {
        toolbar?.setNavigationIcon(R.drawable.ic_back)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.title = "HISTORY GAME"
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            RxBus.get().send(Utils.PROFILE)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        RxBus.get().send(Utils.PROFILE)
        return super.onOptionsItemSelected(item)
    }
}