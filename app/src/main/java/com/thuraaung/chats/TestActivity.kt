package com.thuraaung.chats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.thuraaung.chats.Constants.APP_USERS
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity : AppCompatActivity() {

    @Inject
    lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        db.document("$APP_USERS/VYw3dhVcpJSuoTFqXPJUC7PCAdl2")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this@TestActivity,"Read user failed",Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                Toast.makeText(this@TestActivity,"Read user success",Toast.LENGTH_SHORT).show()

            }
    }
}