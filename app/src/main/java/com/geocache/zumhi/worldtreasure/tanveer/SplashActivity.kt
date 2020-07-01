package com.geocache.zumhi.worldtreasure.tanveer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.*
import com.geocache.zumhi.worldtreasure.R
import kotlinx.android.synthetic.main.activity_splash.*
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

    private val repository:Repository by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goFullScreen(this)
        setContentView(R.layout.activity_splash)
        init()
    }


    private fun init() {
        logoImv.startFadeInAnimation {
            val userData = repository.getUserData()
            println("bakori: $userData")
            if(userData == null) {
                startActivity<RegistrationStepOneActivity>(finish = true)
            }else {
                startActivity<MainActivity>(finish = true)
            }
        }
    }
}
