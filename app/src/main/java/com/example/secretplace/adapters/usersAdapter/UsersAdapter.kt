package com.example.secretplace.adapters.usersAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.secretplace.databinding.ItemContainerUserBinding
import com.example.secretplace.models.User
import com.squareup.picasso.Picasso

class UsersAdapter(private val users:List<User>,val userListener: UserListener) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemContainer: ItemContainerUserBinding = ItemContainerUserBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(itemContainer)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.setUserData(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(itemContainer: ItemContainerUserBinding) : RecyclerView.ViewHolder(itemContainer.root) {
        var binding = itemContainer
        fun setUserData(user : User){
            Picasso.get().load(user.imageProfileUri).into(binding.imageProfile)
            binding.textName.text = user.login
            binding.textEmail.text = user.email
            binding.root.setOnClickListener { userListener.onUserClicked(user) }
        }
    }
}