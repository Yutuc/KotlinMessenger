package com.example.kotlinmessenger.messages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.view.Menu
import android.view.MenuItem
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.objects.ChatMessage
import com.example.kotlinmessenger.objects.LatestMessageRow
import com.example.kotlinmessenger.objects.User
import com.example.kotlinmessenger.registerlogin.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

    //initializing global variables in Kotlin
    companion object {
        var currentUser: User? = null
        val adapter = GroupAdapter<ViewHolder>()
        val latestMessagesMap = HashMap<String, ChatMessage>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        recyclerview_latest_messages.adapter = adapter
        //adds dividers between LatestMessageRows
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)
            //casting item into LatestMessageRow
            val row = item as LatestMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listenForLatestMessages()

        getCurrentUser()
        verifyUserIsLoggedIn()
    }//onCreate function

    //listens for any latest messages sent to firebase database
    private fun listenForLatestMessages(){
        val senderID = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$senderID")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(ChatMessage::class.java)
                latestMessagesMap[p0.key!!] = message!!
                refreshRecyclerView()
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(ChatMessage::class.java)
                latestMessagesMap[p0.key!!] = message!!
                refreshRecyclerView()
            }
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }//listenForLatestMessages function

    //loops through latestMessagesMap and reads all latest messages using a forEach loop
    private fun refreshRecyclerView(){
        adapter.clear()
        latestMessagesMap.values.forEach {
            //"it" refers to objects inside the hashmap
            adapter.add(LatestMessageRow(it))
        }
    }//refreshRecyclerView function

    //gets the current user account logged in
    private fun getCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }//getCurrentUser function

    //verifies if the user is logged in, if not, send back to login screen
    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }//verifyUserIsLoggedIn function

    //handles the actions of each menu item when clicked
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //acts like a switch/case
        when (item?.itemId){
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //displays menu items
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
