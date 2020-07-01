package com.geocache.zumhi.worldtreasure

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.registrationinterfaceactivity.*


class Registration_Interface_Activity : AppCompatActivity() {
    var mSignInClient:GoogleSignInClient?= null
    val RC_GOOGLE = 0
    var callbackManager:CallbackManager?= null
    var sharedPreferences:SharedPreferences?=null
    var loggedIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrationinterfaceactivity)
//        AssHole.a(image_back)
//        val bitmap = BlurDrawable(rootview,100)
//        image_back.setBackgroundDrawable(bitmap)
        sharedPreferences = getSharedPreferences("userprefs",0)
        loggedIn = sharedPreferences!!.getBoolean("isloggedin",false)

        val accessToken = AccessToken.getCurrentAccessToken()
        val isUnExpired = accessToken != null && !accessToken.isExpired

        if (loggedIn){
            goToHome()
        }
        btn_facebook.setOnClickListener{
            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().logInWithReadPermissions(this@Registration_Interface_Activity,
                mutableListOf("email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        sharedPreferences!!.edit().putBoolean("isloggedin",true).apply()
                        goToHome()

                    }
                    override fun onCancel() {
                        Toast.makeText(this@Registration_Interface_Activity,"Cancelled",Toast.LENGTH_SHORT).show()
                    }
                    override fun onError(exception: FacebookException) {
                        Toast.makeText(this@Registration_Interface_Activity,"Something Went Wrong",Toast.LENGTH_SHORT).show()
                    }
                })
        }
        btnGoogle.setOnClickListener {
//            progressregistration.visibility = View.VISIBLE
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                    .requestProfile()
                    .build()
            mSignInClient = GoogleSignIn.getClient(this, gso)
            signIn()
//            progressregistration.visibility = View.GONE
        }
    }


    private fun signIn() {
        val signInIntent: Intent = mSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE){
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)
            updateUI(account,0)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google_Fucked", "signInResult:failed code=" + e.statusCode)
            updateUI(null,e.statusCode)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?,code:Int) {
       if (code == GoogleSignInStatusCodes.NETWORK_ERROR){
            Toast.makeText(this,"Please check your internet and try again",Toast.LENGTH_SHORT).show()
           return
        } else if (code == GoogleSignInStatusCodes.SUCCESS) {
           val intent = Intent(this@Registration_Interface_Activity,ChooseUserNameActivity::class.java)
           intent.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
           intent.putExtra("EMAIL",account!!.email)
           intent.putExtra("NAME",account.displayName)
           sharedPreferences!!.edit().putBoolean("isloggedin",true).apply()
           startActivity(intent)
        } else  if (account== null){
            Toast.makeText(this,"Something Went Wrong",Toast.LENGTH_SHORT).show()
            return
        }
    }
    private fun goToHome(){
        val intent = Intent(this@Registration_Interface_Activity,ChooseUserNameActivity::class.java)
        intent.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
