package com.example.kotlinmessenger.objects

import com.example.kotlinmessenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.received_chat_row_layout.view.*
import kotlinx.android.synthetic.main.received_chat_row_layout.view.received_imageview

class ChatReceivedItem(val user: User, val text: String): Item<ViewHolder>() {
    //sends data into the reycler view layout in ChatLogActivity
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.received_textview.text = text
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.received_imageview)
    }

    override fun getLayout(): Int {
        return R.layout.received_chat_row_layout
    }
}