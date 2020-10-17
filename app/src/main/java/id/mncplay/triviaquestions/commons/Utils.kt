package id.mncplay.triviaquestions.commons

import android.app.Activity
import id.mncplay.triviaquestions.models.DataDetailHistory
import id.mncplay.triviaquestions.models.DataQuestions
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class Utils {

    companion object{

        //endpoint API
        val ENDPOINT = "https://opentdb.com/"
        const val DATA_ENDPOINT = "api.php"

        //endpoint API 2
        val ENDPOINT_POST = "https://phyton06.my.id/project/poyapps/index.php/api/AuthTrivia/"
        const val LOGIN_ENDPOINT = "login"
        const val REGIST_ENDPOINT = "registration"
        const val LEAD_ENDPOINT = "leaderboard"
        const val SCORE_ENDPOINT = "score"

        //fragment
        var mActivity: Activity? = null
        val LOGIN = "login"
        val REGISTER = "register"
        val DASHBOARD = "dashboard"
        val CATEGORY = "category"
        val PLAY = "play"
        val COMPLETE = "complete"
        val LEADERBOARD = "leaderboard"
        val ABOUT = "about"
        val PROFILE = "profile"
        val HISTORY = "history"
        val PREVIEW = "preview"

        //data category
        var id_category = 0
        var page = 0
        var true_answer = ""
        var opt = ""
        var name_category = ""
        var dataQuestions: MutableList<DataQuestions> = mutableListOf()

        //data play game
        var id_player = ""
        var count_correct = 0
        var count_wrong = 0
        var game_mode = ""
        var username = ""
        var name_player = ""
        var score = 0
        var isLogin = false


        //preview
        var dataPrevQuestions: MutableList<DataDetailHistory> = mutableListOf()
        var id_history = ""


        //retrofit
        fun buildClient(): OkHttpClient.Builder {
            val clientBuilder = OkHttpClient.Builder()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            clientBuilder.addInterceptor(loggingInterceptor)
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)

            return clientBuilder
        }

    }

}