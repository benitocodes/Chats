package com.thuraaung.chats

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.thuraaung.chats.Constants.USER_REF
import com.thuraaung.chats.model.AppUser

class UserViewModel @ViewModelInject constructor(
    private val db : FirebaseFirestore
) : ViewModel() {

    val userList = MutableLiveData<List<AppUser>>()

    init {
        loadUserList()
    }

    private fun loadUserList() {

        db.collection(USER_REF)
            .addSnapshotListener { snapshot , error ->

                if (error != null) {
                    Log.d(UserViewModel::class.simpleName,"Load user list failed")
                    return@addSnapshotListener
                }

                userList.postValue(snapshot?.let {

                    val tempUsers = mutableListOf<AppUser>()
                    for (data in snapshot.iterator()) {
                        val user = data.toObject(AppUser::class.java)
                        tempUsers.add(user)
                    }

                    tempUsers

                } ?: listOf())

            }

    }

}