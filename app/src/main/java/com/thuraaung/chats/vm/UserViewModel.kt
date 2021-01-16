package com.thuraaung.chats.vm

import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.thuraaung.chats.utils.Constants.APP_USERS
import com.thuraaung.chats.model.AppUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _userList = MutableLiveData<List<AppUser>>()
    val userList : LiveData<List<AppUser>> = _userList.distinctUntilChanged()

    init {
        loadUserList()
    }

    private fun loadUserList() {

        viewModelScope.launch(Dispatchers.IO) {
            db.collection(APP_USERS)
                .addSnapshotListener { snapshot , _ ->

                    _userList.postValue(snapshot?.let {
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

}