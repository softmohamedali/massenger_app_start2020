package com.example.x.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.x.R
import com.example.x.model.*
import com.example.x.recyclerview.MassgesItem
import com.example.x.recyclerview.RecipianImgItem
import com.example.x.recyclerview.SenderImgItem
import com.example.x.recyclerview.recicpienMassageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.okhttp.internal.framed.Header
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chatactivity.*
import java.io.ByteArrayOutputStream
import java.util.*

class chatactivity : AppCompatActivity() {
    //######################################## var and val ########################################
    val firebaseStorage:FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    val storageReference:StorageReference
    get() = firebaseStorage.reference

    val firebaseFirestore:FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val currnetuserDoucument:DocumentReference
    get() = firebaseFirestore.document("users/${FirebaseAuth.getInstance().currentUser!!.uid}")

    val adaptermassages by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    var otherUserId:String?=null
    var mchatChannelId:String?=null
    var currentUser=FirebaseAuth.getInstance().currentUser!!.uid
    var user:user?=null
    //#################################################################################################

    //--------------------------------------------on create function---------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatactivity)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        else
        {
            window.statusBarColor= Color.parseColor("#0C7897")
        }
        getuserInfo {
            user=it
        }
        otherUserId=intent.extras!!.getString("id")
        tv_name_chatactivity.text=intent.extras!!.getString("name")
        var imgi=intent.extras!!.getString("img")

        createChatChaneel() {chat->
            mchatChannelId=chat
            getmassages(chat)
            btn_send_chatactivity.setOnClickListener {
                var massage= Massage(
                    edtx_massage_chatactivity.text.toString(),
                    currentUser,
                    otherUserId!!,
                    user!!.name,
                    "",
                    Calendar.getInstance().time
                )
                sendMassage(massage,chat,edtx_massage_chatactivity.text.toString())
                edtx_massage_chatactivity.setText("")
            }
        }

        recy_massages_chatactivity.apply {
            adapter=adaptermassages
        }


        if (imgi!!.isNotEmpty())
        {
            Glide.with(this)
                .load(firebaseStorage.getReference(imgi))
                .into(img_imgprofile_chatactivity)
        }
        else
        {
            img_imgprofile_chatactivity.setImageResource(R.drawable.ic_userimg)
        }
        img_back_chatactivity.setOnClickListener {
            finish()
        }
        btn_sendimg_chatactivity.setOnClickListener {
            var sendImgIntent=Intent().apply {
                type="image/*"
                action=Intent.ACTION_PICK
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
            }
            startActivityForResult(Intent.createChooser(sendImgIntent,"chose gallary"),7)
        }

    }
    //----------------------------------------------------------------------------------------------------

    //------------------------------------------------send massge---------------------------------------------
    fun sendMassage(msg:MSG,chatchannelid:String,text:String)
    {
        firebaseFirestore.collection("chatchannel").document(chatchannelid)
            .collection("massags").document().set(msg)
        var msgContent= mutableMapOf<String,Any>()
        msgContent["mas"]=text
        msgContent["senderId"]=msg.senderId
        msgContent["recipienId"]=msg.recipienId
        msgContent["senderName"]=msg.senderName
        msgContent["recipientNamr"]=msg.recipientNamr
        msgContent["data"]=msg.data
        msgContent["type"]=msg.type

        firebaseFirestore.collection("users").document(currentUser)
            .collection("chatchannel").document(otherUserId!!).update(msgContent)
        firebaseFirestore.collection("users").document(otherUserId!!)
            .collection("chatchannel").document(currentUser).update(msgContent)

    }
    //----------------------------------------------------------------------------------------------------

    //--------------------------------------------------create chatchanel-----------------------------------------
    fun createChatChaneel(oncomplete:(chatchnelid:String)-> Unit)
    {

        firebaseFirestore.collection("users").document(currentUser)
            .collection("chatchannel").document(otherUserId!!)
            .get().addOnSuccessListener {
                if (it.exists())
                {
                    oncomplete(it["channelid"] as String)
                    return@addOnSuccessListener
                }

                var newchatChaneel=firebaseFirestore.collection("users").document()

                firebaseFirestore.collection("users").document(otherUserId!!)
                    .collection("chatchannel")
                    .document(currentUser)
                    .set(mapOf("channelid" to newchatChaneel.id))

                firebaseFirestore.collection("users").document(currentUser)
                    .collection("chatchannel")
                    .document(otherUserId!!)
                    .set(mapOf("channelid" to newchatChaneel.id))


                oncomplete(newchatChaneel.id)
            }
    }
    //----------------------------------------------------------------------------------------------------

    //-------------------------------------fun get massages------------------------------------------
    fun getmassages(chatchannelId:String)
    {
        var querysnap=firebaseFirestore.collection("chatchannel")
            .document(chatchannelId).collection("massags").orderBy("data",Query.Direction.DESCENDING)
        querysnap.addSnapshotListener { value, error ->
            adaptermassages.clear()
            value!!.documents.forEach {
                if(it["type"]==MassageType.Text)
                {
                    var massage=it.toObject(Massage::class.java)!!
                    if (massage.senderId==currentUser)
                    {
                        adaptermassages.add(MassgesItem(it.toObject(Massage::class.java)!!,this,it.id))
                    }
                    else
                    {
                        adaptermassages.add(recicpienMassageItem(it.toObject(Massage::class.java)!!,this,it.id))
                    }
                }
                if(it["type"]==MassageType.IMG)
                {
                    var massage=it.toObject(ImgSender::class.java)!!
                    if (massage.senderId==currentUser)
                    {
                        adaptermassages.add(SenderImgItem(it.toObject(ImgSender::class.java)!!,this,it.id))
                    }
                    else
                    {
                        adaptermassages.add(RecipianImgItem(it.toObject(ImgSender::class.java)!!,this,it.id))
                    }
                }
            }
        }
    }
    //-------------------------------fun on activity resault---------------------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode==Activity.RESULT_OK&&requestCode==7&&data!=null&&data.data!=null)
        {
            var imgPath=data.data
            var pitmapImg=MediaStore.Images.Media.getBitmap(this.contentResolver,imgPath)
            var outbutStreamByteArray=ByteArrayOutputStream()
            pitmapImg.compress(Bitmap.CompressFormat.JPEG,25,outbutStreamByteArray)
            var imgbyteArr=outbutStreamByteArray.toByteArray()
            uploadImgToFireStore(imgbyteArr)
            {
                sendMassage(ImgSender(
                    it,
                    currentUser,
                    otherUserId!!,
                    user!!.name,
                    "",
                    Calendar.getInstance().time
                ),mchatChannelId!!,"photo")
            }
        }

    }
    //----------------------------------------------------------------------------------------------------

    //-----------------------------------------------------------fun uploadImgToFireStore-----------------------------------------
    fun uploadImgToFireStore(byteArray:ByteArray,onsucces:(imgPath:String)-> Unit)
    {
        var ref=storageReference.child("${FirebaseAuth.getInstance().currentUser!!.uid}/images/${UUID.nameUUIDFromBytes(byteArray)}")
            ref.putBytes(byteArray).addOnCompleteListener {
                if (it.isSuccessful)
                {
                    onsucces(ref.path)
                }
                else
                {
                    Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
                }
            }
    }
    //----------------------------------------------------------------------------------------------------

    //-------------------------------------------fun getuserinfo()--------------------------------------
    fun getuserInfo(oncomplete:(user:user)-> Unit)
    {
        currnetuserDoucument.get().addOnSuccessListener {
            oncomplete(it.toObject(user()::class.java)!!)
        }
    }
    //----------------------------------------------------------------------------------------------------
}