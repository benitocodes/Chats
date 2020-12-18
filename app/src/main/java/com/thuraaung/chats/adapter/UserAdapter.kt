package com.thuraaung.chats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thuraaung.chats.R
import com.thuraaung.chats.model.AppUser

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val userList = mutableListOf<AppUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_user_item,parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    fun updateUserList(users : List<AppUser>) {
        userList.clear()
        userList.addAll(users)
        notifyDataSetChanged()
    }

    class UserViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val tvUserName = view.findViewById<TextView>(R.id.tv_user_name)
        private val tvEmail = view.findViewById<TextView>(R.id.tv_email)

        fun bind(user : AppUser) {

            tvUserName.text = user.name
            tvEmail.text = user.email

        }
    }
}