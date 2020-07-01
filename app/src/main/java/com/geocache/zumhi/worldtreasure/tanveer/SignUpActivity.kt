package com.geocache.zumhi.worldtreasure.tanveer

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.geocache.zumhi.worldtreasure.R
import kotlinx.android.synthetic.main.signupinterface.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException


class SignUpActivity : AppCompatActivity() {

    private lateinit var invalidEmail: String
    private lateinit var weakPassword: String
    private lateinit var nameAlphaNumericOnly: String
    private lateinit var nameShouldBeFiveCharsLong: String
    private var isOldPassEyeClosed: Boolean = false
    private lateinit var dotString: String
    private lateinit var progressbarDialog: ProgressDialog
    private val repo = Repository()
    private val mainScope: CoroutineScope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signupinterface)
        init()
    }


    override fun onDestroy() {
        mainScope.cancel()
        super.onDestroy()
    }

    private fun init() {
        progressbarDialog = ProgressDialog(this, getString(R.string.loadingPleaseWait), false)
        invalidEmail = getString(R.string.invalidEmail)
        weakPassword = getString(R.string.weak_password)
        dotString = getString(R.string.dot)
        nameShouldBeFiveCharsLong = getString(R.string.nameShouldBeFiveCharsLong)
        nameAlphaNumericOnly = getString(R.string.nameAlphaNumericOnly)
        handlePasswordEditText()
        etemailsignup.setInputType(
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )
        etusernamesignup.setInputType(
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        )
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

    private fun showSignupErrorDialog() {
        try {
            MaterialDialog(this).show {
                title(R.string.signUP)
                message(R.string.signupErrorOccured)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun onBackPressed0(view: View) {
        finish()
    }

    fun onSignUpButtonClicked(view: View) {
        val email = etemailsignup.text.toString()
        val userName = validateAndGetUserName()
        val password = validateAndGetPassword()


        if (email.isNullOrBlank()) {
            etemailsignup.setError(invalidEmail)
            return
        }


        if (userName.isNullOrBlank() || password.isNullOrBlank()) {
            return
        }


        progressbarDialog.show()


        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType: MediaType? = "application/json".toMediaTypeOrNull()


        val json = JSONObject().put("username", userName).put("email", email).put("password", password)


        val body: RequestBody =
            RequestBody.create(mediaType, json.toString())
        val request: Request = Request.Builder()
            .url("http://35.189.94.151/zumhicacheapi/api/v1/auth/register")
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mainScope.launch {
                    progressbarDialog.dismiss()
                    showSignupErrorDialog()
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
                    repo.saveUserData(UserData(userName, email))
                    mainScope.launch {
                        showDialogWithMessage(R.string.signUP, R.string.signup_Successful) {
                            finishAffinity()
                            startActivity<MainActivity>(false)
                        }
                    }
                } else {
                    mainScope.launch {
                        if(message.isNullOrBlank()) {
                            showDialogWithMessage(R.string.signUP,R.string.signupErrorOccured){}
                        }else {
                            showDialogWithMessage(getString(R.string.signUP),message) {}
                        }
                    }
                }


            }
        })
    }

    private fun validateAndGetPassword(): String? {
        val toString = etpassword.text.toString()
        return if (toString.isBlank() || toString.length < 6) {
            etpassword.setError(weakPassword)
            null
        } else {
            toString
        }
    }


    private fun validateAndGetUserName(): String? {
        var editTextInput: String? = etusernamesignup.getText().toString()
        // strategy
        // at least 5 chars
        // and max 23
        // all alphabets and underscores.
        val len = editTextInput!!.length
        var error = false
        var errorString: String? = ""
        if (len < 5) {
            error = true
            errorString = nameShouldBeFiveCharsLong
        } else if (!allAlphabetsWithUnderScores(editTextInput)) {
            error = true
            errorString = nameAlphaNumericOnly
        }
        if (error) {
            etusernamesignup.setError(errorString)
            editTextInput = null
        }
        return editTextInput
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun handlePasswordEditText() {
        etpassword.isLongClickable = (false)
        etpassword.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2
            var result = false
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= etpassword.getRight() - etpassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {
                    isOldPassEyeClosed = !isOldPassEyeClosed
                    etpassword.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0,
                        if (isOldPassEyeClosed) R.drawable.ic_invisble__ else R.drawable.ic_visible__, 0
                    )
                    if (isOldPassEyeClosed) {
                        //salted
                        etpassword.setInputType(
                            InputType.TYPE_CLASS_TEXT or
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD
                        )
                        etpassword.setSelection(etpassword.getText().length);
                    } else {
                        //orignal
                        etpassword.setInputType(
                            InputType.TYPE_CLASS_TEXT or
                                    InputType.TYPE_TEXT_VARIATION_NORMAL
                        )
                        etpassword.setSelection(etpassword.getText().length);
                    }
                    result = true
                } else {
                    result = false
                }

            } else {
                result = false
            }
            result
        }
    }
}
