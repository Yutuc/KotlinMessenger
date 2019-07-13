package com.example.kotlinmessenger.messages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.objects.User
import com.example.kotlinmessenger.objects.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    //Initializing global variables in Kotlin
    companion object {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"
        pullUsers() //pulls all authenticated users from firebase
    }

    //pulls all registered users
    private fun pullUsers(){
        val currentUser = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach{
                    val user = it.getValue(User::class.java) //getValue of "it"/"this" user of type User::class.java
                    if(user != null && currentUser != user.uid) { adapter.add(UserItem(user)) } //if the user is valid and not equal to itself, add to the recyclerview adapter
                }

                //needs context of "view" not "this"
                adapter.setOnItemClickListener { item, view ->

                    //casting Item to User *casting in Kotlin*
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user) //sends data(User object) from NewMessageActivity to ChatLogActivity
                    startActivity(intent)
                    finish()
                }

                recyclerview_newmessage.adapter = adapter //after looping through all users, set recyclerview.adapter to adapter
            }
            override fun onCancelled(p0: DatabaseError) {
                //TODO
            }
        })
    }//pullUsers function
}
