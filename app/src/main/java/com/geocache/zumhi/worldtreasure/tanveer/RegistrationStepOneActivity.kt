package com.geocache.zumhi.worldtreasure.tanveer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.geocache.zumhi.worldtreasure.ChooseUserNameActivity
import com.geocache.zumhi.worldtreasure.R
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_registration_step_one.*
import org.json.JSONException

import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL


class RegistrationStepOneActivity : AppCompatActivity() {
    var mSignInClient: GoogleSignInClient?= null
    val RC_GOOGLE = 0
    var callbackManager: CallbackManager?= null
    var sharedPreferences: SharedPreferences?=null
    var loggedIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_one)
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (callbackManager!= null){
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE){
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun init() {

        // background blur drawable
        AssHole.measureView(image_back) { width,height->
            val bitmap = BlurDrawable(rootview,100,width,height)
            image_back.background = (bitmap)
        }



        btn_signup.setOnClickListener {
            startActivity<SignUpActivity>(finish = false)
        }
        btn_login.setOnClickListener {
            startActivity<LoginActivity>(finish = false)
        }
        btnGoogle.setOnClickListener {
            signInWithGoogle()
        }
        btn_facebook.setOnClickListener {
            signInWithFacebook()
        }
        btn_apple.setOnClickListener {
            startActivity<AppleLoginHandlingActivity>(finish = false)
        }

    }

    private fun signInWithGoogle() {
        progressregistration.visibility = View.VISIBLE
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .requestProfile()
                .build()
        mSignInClient = GoogleSignIn.getClient(this, gso)
        signIn()
        progressregistration.visibility = View.GONE
    }
    private fun signIn() {
        val signInIntent: Intent = mSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE)
    }
    private fun signInWithFacebook() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this@RegistrationStepOneActivity,
            mutableListOf("email"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    val request = GraphRequest.newMeRequest(
                        loginResult!!.accessToken
                    ) { `object`, response ->
                        Log.i("LoginActivity", response.toString())
                        // Get facebook data from login
                        val bFacebookData: Bundle = getFacebookData(`object`)!!
                        val email = bFacebookData.getString("email")!!
                        val fbname = bFacebookData.getString("first_name")!! + " " + bFacebookData.getString("last_name")!!
                        goToHome(email,fbname)
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location") // Parámetros que pedimos a facebook
                    request.parameters = parameters
                    request.executeAsync()
                }
                override fun onCancel() {
                    Toast.makeText(this@RegistrationStepOneActivity,"Cancelled", Toast.LENGTH_SHORT).show()
                }
                override fun onError(exception: FacebookException) {
                    Toast.makeText(this@RegistrationStepOneActivity,"Something Went Wrong", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getFacebookData(`object`: JSONObject): Bundle? {
        try {
            val bundle = Bundle()
            val id = `object`.getString("id")
            try {
                val profile_pic = URL("https://graph.facebook.com/$id/picture?width=200&height=150")
                Log.i("profile_pic", profile_pic.toString() + "")
                bundle.putString("profile_pic", profile_pic.toString())
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return null
            }
            bundle.putString("idFacebook", id)
            if (`object`.has("first_name")) bundle.putString("first_name", `object`.getString("first_name"))
            if (`object`.has("last_name")) bundle.putString("last_name", `object`.getString("last_name"))
            if (`object`.has("email")) bundle.putString("email", `object`.getString("email"))
            if (`object`.has("gender")) bundle.putString("gender", `object`.getString("gender"))
            if (`object`.has("birthday")) bundle.putString("birthday", `object`.getString("birthday"))
            if (`object`.has("location")) bundle.putString("location", `object`.getJSONObject("location").getString("name"))
            return bundle
        } catch (e: JSONException) {
            Log.d("Profile_Masla", "Error parsing JSON")
        }
        return null
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
            val intent = Intent(this@RegistrationStepOneActivity, ChooseUserNameActivity::class.java)
            intent.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("EMAIL",account!!.email)
            intent.putExtra("NAME",account.displayName)
            startActivity(intent)
        } else  if (account== null){
            Toast.makeText(this,"Something Went Wrong",Toast.LENGTH_SHORT).show()
            Log.e("Code:",code.toString())
            return
        }
    }
    private fun goToHome(email: String, name: String) {
        val intent = Intent(this@RegistrationStepOneActivity,ChooseUserNameActivity::class.java)
        intent.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("EMAIL",email)
        intent.putExtra("NAME",name)
        startActivity(intent)
    }
}
