package com.example.secretplace.screens.chat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.secretplace.adapters.messageAdapter.MessageAdapter
import com.example.secretplace.databinding.ActivityChatBinding
import com.example.secretplace.models.Message
import com.example.secretplace.utilities.Constants
import java.util.Date

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by lazy {
        ViewModelProvider(this)[ChatViewModel::class.java]
    }
    private var receivedId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadReceivedUser()
        setListeners()
    }
    private fun setListeners(){
        binding.buttonBack.setOnClickListener {
            finish()
        }
        binding.buttonSendMessage.setOnClickListener {
            sendMessage()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadReceivedUser() {
        val userId = viewModel.getUserId()
        val receivedLogin = intent.getStringExtra(Constants.KEY_LOGIN)
        receivedId = intent.getStringExtra(Constants.KEY_ID)
        binding.receiverProfile.text = receivedLogin

        var chat: MutableList<Message> = mutableListOf()
        val adapter = MessageAdapter(chat, userId)
        binding.chat.adapter = adapter

        viewModel.loadChat(receivedId!!, userId)
        viewModel.chat.observe(this) { newMessages ->
            chat = newMessages.toMutableList()
            adapter.chatMessage = chat
            val startPosition = adapter.itemCount // Сохраняем начальную позицию для обновления
            adapter.notifyItemRangeInserted(startPosition, newMessages.size)
            binding.chat.scrollToPosition(adapter.itemCount - 1)
        }
    }
    private fun sendMessage(){
        val msg = binding.edMessage.text.toString()
        if(msg.isNotEmpty()){
            viewModel.sendMessage(Message(
                senderId = viewModel.getUserId(),
                receivedId = receivedId,
                message = msg,
                dateTime = Date().time.toString()
            ))
        }
        binding.edMessage.text = null
    }
}