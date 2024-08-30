package com.example.secretplace.adapters.messageAdapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.secretplace.databinding.ItemContainerRecivedMessageBinding
import com.example.secretplace.databinding.ItemContainerSendMessageBinding
import com.example.secretplace.models.Message

class MessageAdapter(var chatMessage :MutableList<Message>, var senderId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return if (viewType == VIEW_TYPE_SEND){
            val itemContainer = ItemContainerSendMessageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            SendMessageViewHolder(itemContainer)
        } else{
            val itemContainer = ItemContainerRecivedMessageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedMessageViewHolder(itemContainer)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == VIEW_TYPE_SEND){
            holder as SendMessageViewHolder
            holder.setData(chatMessage[position])
        } else {
            holder as ReceivedMessageViewHolder
            holder.setData(chatMessage[position])
        }
    }

    override fun getItemCount(): Int = chatMessage.size

    override fun getItemViewType(position: Int): Int {
        return if (chatMessage[position].senderId == senderId){
            VIEW_TYPE_SEND
        }else{
            VIEW_TYPE_RECEIVED
        }
    }

    inner class SendMessageViewHolder(var itemContainer: ItemContainerSendMessageBinding) : RecyclerView.ViewHolder(itemContainer.root) {
        fun setData(chatMessage: Message){
            itemContainer.textMessage.text = chatMessage.message
            itemContainer.textDateTime.text = convertTime(chatMessage.dateTime!!)
        }
    }
    inner class ReceivedMessageViewHolder(var itemContainer: ItemContainerRecivedMessageBinding) : RecyclerView.ViewHolder(itemContainer.root){
        fun setData(chatMessage: Message){
            itemContainer.textMessage.text = chatMessage.message
            itemContainer.textDateTime.text = convertTime(chatMessage.dateTime!!)
        }
    }

    private fun convertTime(dateTime: String): String {
        val timestamp = dateTime.toLong()

        val minutes = (timestamp / (1000 * 60)) % 60
        val hours = (timestamp / (1000 * 60 * 60)) % 24

        return String.format("%d:%02d", hours, minutes)
    }
    companion object {
        const val VIEW_TYPE_SEND = 1
        const val VIEW_TYPE_RECEIVED = 2
    }
}