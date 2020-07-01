package com.geocache.zumhi.worldtreasure.tanveer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.geocache.zumhi.worldtreasure.R
import kotlinx.android.synthetic.main.logininterface.*
import kotlinx.android.synthetic.main.signupinterface.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var progressbarDialog:ProgressDialog
    private val mainScope = MainScope()
    private val repo:Repository by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logininterface)
        init()
    }

    override fun onDestroy() {
        mainScope.cancel()
        super.onDestroy()
    }
    private fun init() {
        progressbarDialog = ProgressDialog(this,getString(R.string.loadingPleaseWait),false)
        ivbacksignin.setOnClickListener {
            finish()
        }
    }
    fun onLoginButtonClicked(view: View) {

        val userName = etusernamesignin.text.toString()
        val password = etpasswordsigin.text.toString()



        progressbarDialog.show()


        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType: MediaType? = "application/json".toMediaTypeOrNull()


        val json = JSONObject().put("username", userName).put("password", password)


        val body: RequestBody =
            RequestBody.create(mediaType, json.toString())
        val request: Request = Request.Builder()
            .url("http://35.189.94.151/zumhicacheapi/api/v1/auth/login")
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mainScope.launch {
                    progressbarDialog.dismiss()
                    showLoginFailedDialog()
                }
                // error occured
            }

            override fun onResponse(call: Call, response: Response) {
                mainScope.launch {
                    progressbarDialog.dismiss()
                }

                val message: String? = try {
                    JSONObject(response?.body?.string()).getJSONObject("error").getString("message")
                } catch (ex: Exception) {
                    null
                }


                if (response.isSuccessful) {
                    repo.saveUserData(UserData(userName, ""))
                    mainScope.launch {
                        showDialogWithMessage(R.string.login, R.string.login_successful) {
                            finishAffinity()
                            startActivity<MainActivity>(false)
                        }
                    }
                } else {
                    mainScope.launch {
                        if(message.isNullOrBlank()) {
                            showDialogWithMessage(R.string.login,R.string.loginErrorOccured){}
                        }else {
                            showDialogWithMessage(getString(R.string.login),message) {}
                        }
                    }
                }


            }
        })
    }

    private fun showLoginFailedDialog() {
        try {
            MaterialDialog(this).show {
                title(R.string.login)
                message(R.string.loginErrorOccured)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    private fun showDialogWithMessage(title: Int, msg: Int, callback: () -> Unit) {
        try {
            MaterialDialog(this).show {
                title(title)
                message(msg)
            }.positiveButton {
                it?.dismiss()
                callback()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    private fun showDialogWithMessage(title: String, msg: String, callback: () -> Unit) {
        try {
            MaterialDialog(this).show {
                title(text = title)
                message(text = msg)
            }.positiveButton {
                it?.dismiss()
                callback()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
