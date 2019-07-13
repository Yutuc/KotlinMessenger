package com.example.kotlinmessenger.messages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.objects.ChatMessage
import com.example.kotlinmessenger.objects.ChatReceivedItem
import com.example.kotlinmessenger.objects.ChatSentItem
import com.example.kotlinmessenger.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*



class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    var chattingWith: User? = null //person you are chatting with

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chatlog.adapter = adapter

        chattingWith = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = chattingWith?.username

        listenForMessages()//listens for any new messaged added to firebase database

        send_button_chatlog.setOnClickListener {
            sendMessage()
        }
    }//onCreate function

    private fun listenForMessages() {
        val senderID = FirebaseAuth.getInstance().uid
        val receiverID = chattingWith?.uid
        val ref  = FirebaseDatabase.getInstance().getReference("/user-messages/$senderID/$receiverID")
        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                //ChatMessage::class.java basically casts the snapshot to be a ChatMessage object
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if(chatMessage != null) {
                    //determines if the message is sent or received
                    if(chatMessage.senderID == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatSentItem(LatestMessagesActivity.currentUser!!, chatMessage.text))
                    }
                    else{
                        adapter.add(ChatReceivedItem(chattingWith!!, chatMessage.text))
                    }
                }
                recyclerview_chatlog.scrollToPosition(adapter.getItemCount()-1)
            }
            // have to implement all even if they aren't used
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })

    }//listenForMessages function

    //sends a message to firebasedatabase
    private fun sendMessage(){
        val text = message_edittext_chat_log.text.toString()
        val username = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) //uid of the person I'm sending a message to
        val senderID = FirebaseAuth.getInstance().uid //current user logged in
        val receiverID = username.uid //user that senderID is sending messages to

        if(senderID == null) { return }

        val senderRef = FirebaseDatabase.getInstance().getReference("/user-messages/$senderID/$receiverID").push() //pushes senderID messages to firebase with unique key
        val receiverRef = FirebaseDatabase.getInstance().getReference("/user-messages/$receiverID/$senderID").push() //pushes receiverID messages to firebase with unique key
        //"ref.key" returns the unique id of the new message
        val chatMessage = ChatMessage(senderRef.key!!, senderID, receiverID, text, System.currentTimeMillis() / 1000) //time in seconds
        //sets value of the unqiue key that was just pushed
        senderRef.setValue(chatMessage).addOnSuccessListener {
            message_edittext_chat_log.text.clear() //clears text field every time a message is sent
            recyclerview_chatlog.scrollToPosition(adapter.getItemCount()-1) //scrolls to bottom of page whenever a new message is sent
        }
        /*
            every time a chat is created between users, 2 nodes are created
            one node that references senderID -> receiverID messages
            and another node that references receiverID -> senderID messages
            so messages are showed both ways
         */
        receiverRef.setValue(chatMessage).addOnSuccessListener {
            message_edittext_chat_log.text.clear() //clears text field every time a message is sent
        }

        val senderLatestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$senderID/$receiverID")
        val receiverLatestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$receiverID/$senderID")
        senderLatestMessageRef.setValue(chatMessage)
        receiverLatestMessageRef.setValue(chatMessage)
    }//sendMessage function

}
