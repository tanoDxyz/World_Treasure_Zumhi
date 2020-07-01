package com.geocache.zumhi.worldtreasure.tanveer

import org.koin.core.KoinComponent
import org.koin.core.inject

class Repository:KoinComponent {
    private val prefs:TinyDB by inject()


    fun isFirstTimeUser():Boolean {

        return try {
            prefs.getBoolean("firstTimeUser")
        }catch (ex:Exception) {
            true
        }
    }
    fun setIsNotFirstTimeUser() {
        try {
            prefs.putBoolean("firstTimeUser",false)
        }catch (ex:Exception) {

        }
    }

    fun saveUserData(data:UserData) {
        try {
            prefs.putObject("userData",data)
        }catch (ex:Exception) {}
    }

    fun getUserData():UserData? {
        return try {
            prefs.getObject("userData",UserData::class.java)
        }catch (ex:Exception) {
            null
        }
    }
}