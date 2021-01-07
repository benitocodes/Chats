package com.thuraaung.chats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.thuraaung.chats.R
import com.thuraaung.chats.model.AppUser

class UserAdapter(private val userClickListener : ((AppUser) -> Unit)? = null) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var userList = emptyList<AppUser>()

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
        userList = users
        notifyDataSetChanged()
    }

    inner class UserViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val tvUserName = view.findViewById<TextView>(R.id.tv_user_name)
        private val tvEmail = view.findViewById<TextView>(R.id.tv_email)
        private val imgProfile = view.findViewById<ImageView>(R.id.img_profile)

        init {
            view.setOnClickListener {
                userClickListener?.invoke(userList[adapterPosition])
            }
        }

        fun bind(user : AppUser) {

            imgProfile.load(user.photoUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_baseline_account_circle_24)
                transformations(CircleCropTransformation())
            }

            tvUserName.text = user.name
            tvEmail.text = user.email

        }
    }
}