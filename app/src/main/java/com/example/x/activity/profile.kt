package com.example.x.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.x.R
import com.example.x.model.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import java.util.*

class profile : AppCompatActivity() {
    var  name:String?=null

    val firebaseStorage:FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    val firebaseFirestore:FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val doucument:DocumentReference
    get() = firebaseFirestore.collection("users")
        .document("${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    val firebaseStorageReference:StorageReference
    get() = firebaseStorage.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    //-----------------------------------------------on create--------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar_profile)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        else
        {
            window.statusBarColor= Color.parseColor("#0C7897")
        }

        getuserinfo {
            name=it.name
            textView.text=it.name
            if (it.img.isNotEmpty())
            {
                Glide.with(this@profile).load(firebaseStorage.getReference(it.img))
                    .into(imageView_profile)
            }
        }

        imageView_profile.setOnClickListener {
            val intent= Intent().apply {
                type="image/*"
                action=Intent.ACTION_PICK
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
            }
            startActivityForResult(Intent.createChooser(intent,"chose img"),2)
        }
        btn_singout_profile.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            var i=Intent(this, singin::class.java)
            startActivity(i)
            finish()
        }
    }
    //----------------------------------------------------------------------------------------------------

    //-------------------------------------on activity result---------------------------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==2&&resultCode==Activity.RESULT_OK&&data != null&&data.data!=null)
        {
            imageView_profile.setImageURI(data.data)
            var selectimguri=data.data
            var selectomgbmp=MediaStore.Images.Media.getBitmap(this.contentResolver,selectimguri)
            var aoutputstream=ByteArrayOutputStream()
            selectomgbmp.compress(Bitmap.CompressFormat.JPEG,20,aoutputstream)
            var selectimgbyte=aoutputstream.toByteArray()

            uploadimg(selectimgbyte){
                var user= mutableMapOf<String,Any>()
                user["name"]=name!!
                user["img"]=it
                doucument.update(user)
            }

        }
    }
    //----------------------------------------------------------------------------------------------------

    //-----------------------------------------------upload img------------------------------------------
    fun uploadimg(imgbyte:ByteArray,onsucces:(namebyte:String) -> Unit)
    {
        var ref= firebaseStorageReference.child("images/${UUID.nameUUIDFromBytes(imgbyte).toString()}")
        ref.putBytes(imgbyte).addOnCompleteListener {
            if (it.isSuccessful)
            {
                Toast.makeText(this,"sucsseful upload img ",Toast.LENGTH_LONG).show()
                onsucces(ref.path)
            }
            else{
                Toast.makeText(this,it.exception.toString(),Toast.LENGTH_LONG).show()
            }
        }

    }
    //----------------------------------------------------------------------------------------------------

    //------------------------------------------------on option scelected meanu-------------------------------------
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            android.R.id.home ->
            {
                finish()
                return true
            }
            else -> return false
        }
    }
    //----------------------------------------------------------------------------------------------------

    //--------------------------------------get userinfo---------------------------------------------------
    private fun getuserinfo(oncomplete:(user:user) -> Unit)
    {
        doucument.get().addOnSuccessListener {
            oncomplete(it.toObject(user::class.java)!!)
        }
    }
    //----------------------------------------------------------------------------------------------------
}