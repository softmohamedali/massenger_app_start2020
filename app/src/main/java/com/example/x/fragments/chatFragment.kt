package com.example.x.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.x.R
import com.example.x.activity.SearchActicity
import com.example.x.activity.chatactivity
import com.example.x.model.MSG
import com.example.x.model.Massage
import com.example.x.model.user
import com.example.x.recyclerview.ChatItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_chat.*

class chatFragment : Fragment() {
    private val firebaseFirestore:FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    lateinit var sec:Section
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)


    }

    //------------------------------------------------fun on activity created----------------------------------------
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val title=activity?.findViewById(R.id.tv_titletb_main) as TextView
        title.text="chat"

        img_search_chatfragment.setOnClickListener {
            activity!!.startActivity(Intent(activity,SearchActicity::class.java))
        }


//        chatlistener(::prebaerRecy)
    }
    //----------------------------------------------------------------------------------------------------

    //-----------------------------------------------chatlistener------------------------------------------
    fun chatlistener(pr:(List<Item>) -> Unit):ListenerRegistration
    {

        return firebaseFirestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid).collection("chatchannel")
            .orderBy("data",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
            if (error != null)
            {
                return@addSnapshotListener
            }
            val usersforadpter= mutableListOf<Item>()
            value!!.documents.forEach {
                usersforadpter.add(ChatItem(it.id,it.toObject(user::class.java)!!,activity!!.applicationContext,it.toObject(Massage::class.java)!!))
//                ,it.toObject(MSG::class.java
            }
            pr(usersforadpter)
        }


    }
    //----------------------------------------------------------------------------------------------------

    //----------------------------------------------prebaerRecy------------------------------------------
    fun prebaerRecy(users:List<Item>)
    {
        recy_chatfrg.apply {
            layoutManager=LinearLayoutManager(activity,RecyclerView.VERTICAL,false)
            adapter=GroupAdapter<GroupieViewHolder>().apply {
                sec= Section(users)
                add(sec)
                setOnItemClickListener { item, view ->

                    if (item is ChatItem)
                    {var intent=Intent(activity,
                        chatactivity::class.java)
                        intent.putExtra("name",item.user.name)
                        intent.putExtra("img",item.user.img)
                        intent.putExtra("id",item.id)
                        activity!!.startActivity(intent)
                    }

                }
            }
        }
    }
    //----------------------------------------------------------------------------------------------------
}
