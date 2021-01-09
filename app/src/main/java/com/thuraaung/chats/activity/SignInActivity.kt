package com.thuraaung.chats.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.thuraaung.chats.Constants.APP_USERS
import com.thuraaung.chats.R
import com.thuraaung.chats.model.AppUser
import java.util.*

class SignInActivity : AppCompatActivity() {

    private val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestIdToken(getString(R.string.default_web_client_id))
        .requestIdToken("491519653941-48f809ks3n8qvtv6p0ba72mitgspplio.apps.googleusercontent.com")
        .requestEmail()
        .build()

    private lateinit var googleSignInClient : GoogleSignInClient

    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val btnSignIn = findViewById<SignInButton>(R.id.btn_sign_in)
        btnSignIn.setOnClickListener {
            signIn()
        }
    }

    @Suppress("DEPRECATION")
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 101)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("Google sign in", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUser()
                    startApp()
                } else {
                    Toast.makeText(this,"Sign failed",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUser() {

        val db = Firebase.firestore

        db.collection(APP_USERS)
            .document(auth.currentUser!!.uid)
            .set(AppUser(
                uid = auth.currentUser!!.uid,
                name = auth.currentUser!!.displayName.toString(),
                photoUrl = auth.currentUser!!.photoUrl.toString(),
                email = auth.currentUser!!.email.toString(),
                signInDate = Date(),
                online = true))

    }

    @Suppress("DEPRECATION")
    private fun startApp() {
        Handler().postDelayed({
            val test = Intent(this, MainActivity::class.java)
            startActivity(test)
            finish()
        },500)
    }
}