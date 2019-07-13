package com.example.kotlinmessenger.objects

import com.example.kotlinmessenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.sent_chat_row_layout.view.*

class ChatSentItem(val user: User, val text: String): Item<ViewHolder>() {
    //sends data into the reycler view layout in ChatLogActivity
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.sent_textview.text = text
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.sent_imageview)
    }

    override fun getLayout(): Int {
        return R.layout.sent_chat_row_layout
    }
}