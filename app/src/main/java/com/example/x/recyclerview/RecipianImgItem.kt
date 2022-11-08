package com.example.x.recyclerview

import android.content.Context
import android.text.format.DateFormat
import com.bumptech.glide.Glide
import com.example.x.R
import com.example.x.model.ImgSender
import com.example.x.model.Massage
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.massge_layout.*
import kotlinx.android.synthetic.main.massge_layout.tv_data_massagelayout
import kotlinx.android.synthetic.main.recipian_img_item.*
import kotlinx.android.synthetic.main.sender_img_itm.*

class RecipianImgItem(var massge: ImgSender, var context: Context, chatChannelId:String): Item() {
    val firebaseStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tv_data_massagelayout.text= DateFormat.format("hh:mm a",massge.data).toString()
        Glide.with(context)
            .load(firebaseStorage.getReference(massge.mas))
            .placeholder(R.drawable.ic_imgsender)
            .into(viewHolder.img_masg_recipianlayout)
    }

    override fun getLayout(): Int {
        return R.layout.recipian_img_item
    }
}