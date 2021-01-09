package com.thuraaung.chats

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.thuraaung.chats.Constants.TOKEN
import com.thuraaung.chats.activity.ChatActivity
import com.thuraaung.chats.model.Token
import kotlin.random.Random

class ChatMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage : RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val uid = remoteMessage.data["uid"]
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("uid",uid)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val notificationID = Random.nextInt()
        val pendingIntent = PendingIntent.getActivity(this,0,intent,FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this,"Channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID,notification)

    }

    private fun createNotificationChannel(notificationManager : NotificationManager) {

        val channelName = "channelName"
        val channel = NotificationChannel("Channel",channelName, IMPORTANCE_HIGH).apply {
            description = "My notificaiton channel"
            enableLights(false)
            lightColor = Color.GREEN
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun sendRegistrationToServer(token : String) {

        val auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore

        db.collection(TOKEN)
            .document(auth.currentUid())
            .set(Token(token))
            .addOnSuccessListener {
                //No implementation
            }
            .addOnFailureListener {
                //No implementation
            }
    }

}