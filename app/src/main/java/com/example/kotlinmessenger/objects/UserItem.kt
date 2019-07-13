package com.example.kotlinmessenger.objects

import com.example.kotlinmessenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_row_newmessage.view.*

//each individual card in the recyclerview when starting a new chat
class UserItem(val user: User) : Item<ViewHolder>(){
    //sends data into the layout
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview.text = user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.profile_picture_image_view)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_newmessage
    }
}