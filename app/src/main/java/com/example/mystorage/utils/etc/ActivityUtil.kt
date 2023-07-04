package com.example.mystorage.utils.etc

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.example.mystorage.utils.etc.Constants.TAG

object ActivityUtil {
    fun goToNextActivity(activity: Activity, nextActivity: Activity) {
        val intent = Intent(activity, nextActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }
}