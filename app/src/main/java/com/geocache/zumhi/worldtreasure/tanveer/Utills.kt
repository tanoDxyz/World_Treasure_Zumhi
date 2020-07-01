package com.geocache.zumhi.worldtreasure.tanveer

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator

fun goFullScreen(activity: Activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

 fun allAlphabetsWithUnderScores(input: String): Boolean {
    for (i in 0 until input.length) {
        val ch = input[i]
        if (!(ch.toInt() >= 65 && ch.toInt() <= 90 || ch.toInt() >= 97 && ch.toInt() <= 122 || ch == '_' || ch == ' ')) {
            return false
        }
    }
    return true
}


inline fun <reified T>Activity.startActivity(finish:Boolean = true) {
        Intent(this,T::class.java).apply {
            if(finish) {
                finish()
            }
            startActivity(this)

        }
    }

    fun View.startFadeInAnimation(callback:()->Unit) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator() //add this
        fadeIn.duration = 3000
        fadeIn.startOffset = 10


        val animation = AnimationSet(false) //change to false
        animation.addAnimation(fadeIn)
        animation.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                callback()
            }
        })
        this.setAnimation(animation)
    }