package com.example.kotlinmessenger.objects

import com.example.kotlinmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val message: ChatMessage): Item<ViewHolder>(){

    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val chatPartnerID: String
        if(message.senderID == FirebaseAuth.getInstance().uid){
            chatPartnerID = message.receiverID
        }
        else{
            chatPartnerID = message.senderID
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerID")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_textview_latest_messages.text = chatPartnerUser?.username
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(viewHolder.itemView.profile_picture_imageview_latest_message_row)
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        viewHolder.itemView.latest_message_textview_latest_messages.text = message.text
        //Picasso.get().load(message.profileImageUrl).into(viewHolder.itemView.profile_picture_imageview_latest_message_row)
    }
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}