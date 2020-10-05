package id.mncplay.triviaquestions.commons

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(context: Context) {

    internal var sp: SharedPreferences
    internal var spEditor: SharedPreferences.Editor

    val spUsername: String?
        get() = sp.getString(USERNAME, "")

    val spIdPlayer: String?
        get() = sp.getString(ID_PLAYER, "")

    val spName: String?
        get() = sp.getString(NAME, "")

    val spScore: Int?
        get() = sp.getInt(SCORE, 0)

    val spLogin: Boolean?
        get() = sp.getBoolean(LOGIN, false)


    init {
        sp = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        spEditor = sp.edit()
    }

    fun saveSPString(keySP: String, value: String) {
        spEditor.putString(keySP, value)
        spEditor.commit()
    }

    fun saveSPInt(keySP: String, value: Int) {
        spEditor.putInt(keySP, value)
        spEditor.commit()
    }

    fun saveSPBoolean(keySP: String, value: Boolean) {
        spEditor.putBoolean(keySP, value)
        spEditor.commit()
    }


    companion object {
        val APP = "App"
        val USERNAME = "Username"
        val NAME = "Name"
        val SCORE = "Score"
        val LOGIN = "Login"
        val ID_PLAYER = "id"

    }
}
