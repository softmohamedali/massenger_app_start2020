package com.example.x.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.x.R
import com.example.x.fragments.chatFragment
import com.example.x.fragments.discoverFragment
import com.example.x.fragments.peopleFragment
import com.example.x.model.user
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() ,BottomNavigationView.OnNavigationItemSelectedListener {

    private val mauth:FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firebsaefirestore:FirebaseFirestore by lazy{
        FirebaseFirestore.getInstance()
    }
    val mchat=chatFragment()
    val mpeople=peopleFragment()
    val mdiscover=discoverFragment()

    //----------------------------------------on create------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        else
        {
            window.statusBarColor=Color.parseColor("#0C7897")
        }

        firebsaefirestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .get().addOnSuccessListener {
                val user=it.toObject(user::class.java)
                if (user!!.img.isNotEmpty())
                {
                    Glide.with(this)
                        .load(FirebaseStorage.getInstance().getReference(user.img))
                        .into(imv_userimg_maintool)
                }
                else
                {
                    imv_userimg_maintool.setImageResource(R.drawable.ic_userimg)
                }
            }

        setSupportActionBar(toolbar_main)
        supportActionBar?.title=""

        bnv_nav_main.setOnNavigationItemSelectedListener(this)
        setfragment(mchat)

        imv_userimg_maintool.setOnClickListener {
            startActivity(Intent(this, profile::class.java))
        }

    }
    //----------------------------------------------------------------------------------------------------

    //-----------------------------------------on start----------------------------------------------------
    override fun onStart() {
        super.onStart()
        if (mauth.currentUser?.uid==null)
        {
            var intent=Intent(this, singin::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

    }

    //------------------------------------------------------------------------------------------------------

    //---------------------------------------------on nav select--------------------------------------------
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.item_chatfr_menu ->
            {
                setfragment(mchat)
                return true
            }
            R.id.item_peoplefr_menu ->
            {
                setfragment(mpeople)
                return true
            }
            R.id.item_discoverfr_menu ->
            {
                setfragment(mdiscover)
                return true
            }
            else ->
            {
                return false
            }
        }
    }
    //----------------------------------------------------------------------------------------------------

    //------------------------------------set frag--------------------------------------------------------
    fun setfragment(fra:Fragment)
    {
        var btr=supportFragmentManager.beginTransaction()
        btr.replace(R.id.coordinato,fra)
        btr.commit()
    }
    //----------------------------------------------------------------------------------------------------
}