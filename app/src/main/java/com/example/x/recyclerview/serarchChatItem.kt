package com.example.x.recyclerview

import android.content.Context
import com.example.x.R
import com.example.x.model.user
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.itemfound_searchactivity.view.*

class serarchChatItem(var user: user,var context: Context,var id:String):Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView2.text=user.name
    }

    override fun getLayout(): Int {
        return R.layout.itemfound_searchactivity
    }
}