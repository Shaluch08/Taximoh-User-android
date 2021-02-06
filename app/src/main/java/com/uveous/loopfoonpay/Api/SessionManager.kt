package com.uveous.loopfoonpay.Api

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.uveous.loopfoonpay.R


class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
        const val USER_NAME= "user_name"
        const val USER_PHONENO= "user_number"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun savename(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_NAME, token)
        editor.apply()
    }

     fun savenumber(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_PHONENO, token)
        editor.apply()
    }

   fun saveuserid(userid: Int) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, userid)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun fetchusername(): String? {
        return prefs.getString(USER_NAME, null)
    }

     fun fetchuserno(): String? {
        return prefs.getString(USER_PHONENO, null)
    }

    fun fetchuserid(): Int? {
        return prefs.getInt(USER_ID, 0)
    }

    fun logoutUser() {
        prefs.edit().clear()
        prefs.edit().remove(USER_TOKEN)
        prefs.edit().remove(USER_PHONENO)
        prefs.edit().remove(USER_ID)
        prefs.edit().remove(USER_NAME)
        prefs.edit().commit()
    }

}