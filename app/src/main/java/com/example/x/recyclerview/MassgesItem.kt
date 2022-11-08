package com.example.x.recyclerview

import android.content.Context
import android.text.format.DateFormat
import com.example.x.R
import com.example.x.model.Massage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.massge_layout.*



class MassgesItem(var massge:Massage,var context:Context,chatChannelId:String):Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tv_masg_massagelayout.text=massge.mas
        viewHolder.tv_data_massagelayout.text=DateFormat.format("hh:mm a",massge.data).toString()
    }

    override fun getLayout(): Int {
        return R.layout.massge_layout
    }
}