package com.example.x.recyclerview

import android.content.Context
import android.text.format.DateFormat
import com.bumptech.glide.Glide
import com.example.x.R
import com.example.x.model.MSG
import com.example.x.model.user
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.recycler_view_item.*


class ChatItem(var id:String,var user:user,var context:Context,var msg: MSG):Item() {
    val firebaseStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    val firestore:FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val curentdouc:DocumentReference
    get() = firestore.collection("users").document(id)
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tv_lastmasg_recyitem.text=msg.mas
        viewHolder.tv_time_recyitem.text= DateFormat.format("hh:mm a",msg.data).toString()

        getcurrentuse {
            viewHolder.tv_name_recyitem.text=it.name
            if (it.img.isNotEmpty())
            {
                Glide.with(context)
                    .load(firebaseStorage.getReference(it.img))
                    .into(viewHolder.img_userimg_recyitem)
            }
            viewHolder.img_userimg_recyitem.setImageResource(R.drawable.ic_userimg)
        }

    }
    fun getcurrentuse(oncomplete:(user)->Unit)
    {
        curentdouc.get().addOnSuccessListener {
            oncomplete(it.toObject(user::class.java)!!)
        }
    }

    override fun getLayout(): Int {
        return R.layout.recycler_view_item
    }
}
