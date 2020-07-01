package com.geocache.zumhi.worldtreasure

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.geocache.zumhi.worldtreasure.tanveer.*
import kotlinx.android.synthetic.main.chooseyourname.*
import kotlinx.android.synthetic.main.signupinterface.*
import org.koin.android.ext.android.inject

class ChooseUserNameActivity : AppCompatActivity() {

    private val repo:Repository by inject()
    private lateinit var nameAlphaNumericOnly: String
    private lateinit var nameShouldBeFiveCharsLong: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chooseyourname)
        init()
    }



    private fun init() {
        nameShouldBeFiveCharsLong = getString(R.string.nameShouldBeFiveCharsLong)
        nameAlphaNumericOnly = getString(R.string.nameAlphaNumericOnly)


    }


    private fun validateAndGetUserName(): String? {
        var editTextInput: String? = etusernamechoosing.getText().toString()
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
            etusernamechoosing.setError(errorString)
            editTextInput = null
        }
        return editTextInput
    }

    fun onSignUpButtonClicked(view: View) {
        val email = intent.getStringExtra("EMAIL")
        val name = validateAndGetUserName()
        if(name.isNullOrBlank()) {
            return
        }
        repo.saveUserData(UserData(name,email))
        startActivity<MainActivity>(true)
    }
}

