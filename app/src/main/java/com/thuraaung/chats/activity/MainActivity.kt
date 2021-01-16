package com.thuraaung.chats.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.thuraaung.chats.utils.Constants.APP_USERS
import com.thuraaung.chats.R
import com.thuraaung.chats.databinding.ActivityMainBinding
import com.thuraaung.chats.frag.ChatListFragment
import com.thuraaung.chats.frag.UserListFragment
import com.thuraaung.chats.model.AppUser

class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private lateinit var binding: ActivityMainBinding

    companion object {
        private val TITLES = listOf("Chats","Users")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.messaging.isAutoInitEnabled = true // enable the token auto initialization
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)

        binding.pager.adapter = MyPagerAdapter(this)
        TabLayoutMediator(binding.layoutToolBar.tabLayout, binding.pager) { tab, position ->
            tab.text = TITLES[position]
        }.attach()

        db.collection(APP_USERS)
            .document(auth.currentUser!!.uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val user = value!!.toObject(AppUser::class.java)!!
                binding.layoutToolBar.imgProfile.load(user.photoUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_baseline_account_circle_24)
                    transformations(CircleCropTransformation())
                }
            }
    }
}

class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragmentList = listOf(
        ChatListFragment(),
        UserListFragment())

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}