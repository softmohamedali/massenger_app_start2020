package com.example.x.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.x.R
import com.example.x.model.user
import com.example.x.recyclerview.ChatItem
import com.example.x.recyclerview.serarchChatItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_search_acticity.*

class SearchActicity : AppCompatActivity() {
    val firestroe:FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var section:Section
    var initrecy=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_acticity)
        setSupportActionBar(toolbar_search_searchaccticity)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_searchactivity,menu)
        

        var searchManger=getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu!!.findItem(R.id.icsearch_menu_searchActivity).actionView as SearchView).apply {
            setSearchableInfo(searchManger.getSearchableInfo(componentName))
            setOnQueryTextListener(object :SearchView.OnQueryTextListener
            {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isEmpty())
                    {
                        return false
                    }
                    var query=firestroe.collection("users")
                        .orderBy("name")
                        .startAt(newText!!.trim())
                        .endAt(newText.trim()+"\uf8ff")

                    searchByQuery(query,::prepaerRecy)
                    return true
                }
            })
        }
        return true
    }
    fun searchByQuery(query:Query,onsucecc:(List<Item>) -> Unit)
    {

        var list= mutableListOf<Item>()
        query.get().addOnSuccessListener {
            it.documents.forEach {
                list.add(serarchChatItem(it.toObject(user::class.java)!!,this,it.id))
            }
            onsucecc(list)
        }
    }
    fun prepaerRecy(list:List<Item>) {
        fun init() {
            recycler_itemfound_searchactivity.apply {
                layoutManager =
                    LinearLayoutManager(this@SearchActicity, RecyclerView.VERTICAL, false)
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    section = Section(list)
                    add(section)
                    setOnItemClickListener { item, view ->

                        if (item is serarchChatItem) {
                            var intent = Intent(
                                this@SearchActicity,
                                chatactivity::class.java
                            )
                            intent.putExtra("name", item.user.name)
                            intent.putExtra("img", item.user.img)
                            intent.putExtra("id", item.id)
                            startActivity(intent)
                        }
                    }
                }
                initrecy = true
            }
        }
        fun updateRecycler() = section.update(list)
        if (initrecy == false) {
            init()
        } else {
            updateRecycler()


        }
    }
}