package com.thuraaung.chats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.thuraaung.chats.R
import com.thuraaung.chats.databinding.LayoutUserItemBinding
import com.thuraaung.chats.model.AppUser

class UserAdapter(private val userClickListener : ((AppUser) -> Unit)? = null) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var userList = emptyList<AppUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = LayoutUserItemBinding
            .inflate(LayoutInflater.from(parent.context),parent,false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    fun updateUserList(users : List<AppUser>) {
        userList = users
        notifyDataSetChanged()
    }

    inner class UserViewHolder(private val binding : LayoutUserItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                userClickListener?.invoke(userList[adapterPosition])
            }
        }

        fun bind(user : AppUser) {

            binding.imgProfile.load(user.photoUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_baseline_account_circle_24)
                transformations(CircleCropTransformation())
            }

            binding.tvUserName.text = user.name
            binding.tvEmail.text = user.email

        }
    }
}