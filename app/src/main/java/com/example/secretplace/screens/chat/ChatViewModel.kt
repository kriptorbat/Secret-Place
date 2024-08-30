package com.example.secretplace.screens.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.secretplace.models.Message
import com.example.secretplace.utilities.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {

    var auth: FirebaseAuth = Firebase.auth
    private val databaseReferenceMessage: DatabaseReference = FirebaseDatabase.getInstance().getReference(
        Constants.KEY_MESSAGE)
    val chat = MutableLiveData<List<Message>>()

    fun getUserId() = auth.uid.toString()

    fun loadChat(receivedId: String,userId: String) {
        val messageList = mutableListOf<Message>()

        val messageListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val message = dataSnapshot.getValue(Message::class.java)
                if (message != null && (message.senderId == receivedId || message.senderId == userId)) {
                    messageList.add(message)
                    messageList.sortBy { it.dateTime }
                    Log.d("suka", "gg bro")
                    chat.value = messageList
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("YourViewModel", "Ошибка при загрузке чата: ${databaseError.message}")
            }
        }

        databaseReferenceMessage.orderByChild("receivedId").equalTo(userId).addChildEventListener(messageListener)
        databaseReferenceMessage.orderByChild("receivedId").equalTo(receivedId).addChildEventListener(messageListener)
    }
    fun sendMessage(message: Message){
        databaseReferenceMessage.push().setValue(message)
    }
}
