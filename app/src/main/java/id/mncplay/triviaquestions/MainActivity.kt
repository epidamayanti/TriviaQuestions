package id.mncplay.triviaquestions

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import id.mncplay.triviaquestions.commons.BaseActivity
import id.mncplay.triviaquestions.commons.RxBus
import id.mncplay.triviaquestions.commons.SharedPrefManager
import id.mncplay.triviaquestions.commons.Utils
import id.mncplay.triviaquestions.database.DbHelper
import id.mncplay.triviaquestions.models.DataCategory
import id.mncplay.triviaquestions.views.*

class MainActivity : BaseActivity() {

    private var sharedPrefManager: SharedPrefManager? = null
    private val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                dialog.dismiss()
                finish()
            }

            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()

            }
        }
    }
    lateinit var DbHelper : DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DbHelper = DbHelper(this)

        sharedPrefManager = SharedPrefManager(this)

        if (savedInstanceState == null){
            manageSubscription()
            Utils.mActivity = this
            if(DbHelper.readAllCategory().isEmpty())
                addDataCategory()

            if(sharedPrefManager?.spLogin!!){
                changeFragment(DashboardFragment(), false, Utils.DASHBOARD)
                Utils.name_player = sharedPrefManager?.spName.toString()
                Utils.username = sharedPrefManager?.spUsername.toString()
                Utils.score = sharedPrefManager?.spScore!!.toInt()
                Utils.id_player = sharedPrefManager?.spIdPlayer.toString()
                Utils.isLogin = true

            }

            else{
                changeFragment(LoginFragment(), false, Utils.LOGIN)
            }
        }
    }

    private fun manageSubscription(){
        subscriptions.add(RxBus.get().toObservable().subscribe({
                event -> manageBus(event)
        },{ Toast.makeText(this, "Timeout! koneksi buruk", Toast.LENGTH_SHORT).show()}))
    }

    private fun manageBus(event:Any) {
        when (event) {
            Utils.LOGIN -> changeFragment(LoginFragment(), false, Utils.LOGIN)
            Utils.DASHBOARD -> changeFragment(DashboardFragment(), false, Utils.DASHBOARD)
            Utils.CATEGORY -> changeFragment(CategoryFragment(), false, Utils.CATEGORY)
            Utils.REGISTER -> changeFragment(RegisterFragment(), false, Utils.REGISTER)
            Utils.PLAY -> changeFragment(PlayFragment(), false, Utils.PLAY)
            Utils.COMPLETE -> changeFragment(CompleteFragment(), false, Utils.COMPLETE)
            Utils.LEADERBOARD -> changeFragment(LeaderboardFragment(), false, Utils.LEADERBOARD)
            Utils.ABOUT -> changeFragment(AboutFragment(), false, Utils.ABOUT)
            Utils.PROFILE -> changeFragment(ProfileFragment(), false, Utils.PROFILE)
            Utils.HISTORY -> changeFragment(HistoryFragment(), false, Utils.HISTORY)
            Utils.PREVIEW -> changeFragment(PreviewFragment(), false, Utils.PREVIEW)
        }
    }

    private fun addDataCategory(){
        DbHelper.insertCategory(DataCategory(9, "General Knowledge"))
        DbHelper.insertCategory(DataCategory(11, "Film"))
        DbHelper.insertCategory(DataCategory(14, "Television"))
        DbHelper.insertCategory(DataCategory(19, "Mathematics"))
        DbHelper.insertCategory(DataCategory(17, "Science and Nature"))
        DbHelper.insertCategory(DataCategory(20, "Mythology"))
        DbHelper.insertCategory(DataCategory(21, "Sport"))
        DbHelper.insertCategory(DataCategory(22, "Geography"))
        DbHelper.insertCategory(DataCategory(23, "History"))
        DbHelper.insertCategory(DataCategory(24, "Politics"))
        DbHelper.insertCategory(DataCategory(25, "Art"))
        DbHelper.insertCategory(DataCategory(26, "Celebrities"))
        DbHelper.insertCategory(DataCategory(27, "Animals"))
        DbHelper.insertCategory(DataCategory(28, "Vehicles"))
        DbHelper.insertCategory(DataCategory(31, "Anime and Manga"))
    }


    override fun onBackPressed() {
        super.onBackPressed()
        when (supportFragmentManager.fragments.last()){
            is DashboardFragment ->{

            }
            is LoginFragment -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Exit ? ")
                    .setPositiveButton("YES", dialogClickListener)
                    .setNegativeButton("NO", dialogClickListener)
                    .setCancelable(false).show()
            }
            is CategoryFragment ->{
                changeFragment(DashboardFragment(), false, Utils.DASHBOARD)
            }
            is PlayFragment -> {
                if(Utils.page == 0)
                    changeFragment(CategoryFragment(), false, Utils.CATEGORY)
                else{
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Please Complete The Question")
                        .setNegativeButton("OK", dialogClickListener)
                        .setCancelable(false).show()
                }
            }
            is LeaderboardFragment -> {
                changeFragment(DashboardFragment(), false, Utils.DASHBOARD)
            }
            is CompleteFragment-> {

            }
        }
    }

}