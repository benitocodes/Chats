package com.thuraaung.chats.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestore
import com.thuraaung.chats.Constants.APP_USERS
import com.thuraaung.chats.model.AppUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel @ViewModelInject constructor(
    private val db : FirebaseFirestore) : ViewModel() {

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