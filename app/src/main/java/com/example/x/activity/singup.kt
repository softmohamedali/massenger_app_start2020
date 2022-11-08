package com.example.x.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.x.R
import com.example.x.model.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_singup.*

class singup : AppCompatActivity() ,TextWatcher{

    private val mauth:FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore:FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val doument:DocumentReference
    get() = firestore.document("users/${mauth.currentUser?.uid.toString()}")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        else
        {
            window.statusBarColor= Color.parseColor("#0C7897")
        }
        edtx_password_singup.addTextChangedListener(this)
        edtx_email_singup.addTextChangedListener(this)

//        firestore.collection("users").document(mauth.currentUser?.uid.toString())

        bu_register_singup.setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(edtx_email_singup.text.trim().toString()).matches())
            {
                edtx_email_singup.error="Email is valid"
                edtx_email_singup.requestFocus()
                return@setOnClickListener
            }
            if (edtx_password_singup.text.trim().toString().length<6)
            {
                edtx_password_singup.error="password is uncomplete"
                edtx_password_singup.requestFocus()
                return@setOnClickListener
            }
           register()
        }
    }

    //-------------------------------------REGGISTER FUN-----------------------------------------------------
    fun register()
    {
        pb_register_singup.visibility= View.VISIBLE
        var name=edtx_username_singup.text.trim().toString()
        var email=edtx_email_singup.text.trim().toString()
        var password=edtx_password_singup.text.trim().toString()

        mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful)
            {
                pb_register_singup.visibility= View.GONE
                var user=user(name,"")
                doument.set(user)
                var intent=Intent(this, singin::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            else
            {
                pb_register_singup.visibility= View.GONE
                Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
            }
        }
    }
    //--------------------------------------------------------------------------------------------------------


    //-------------------------------------------START FUN OF TEXT WATCHER-----------------------------------
    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        bu_register_singup.isEnabled=edtx_email_singup.text.trim().toString().isNotEmpty()&&
                edtx_password_singup.text.trim().toString().isNotEmpty()
    }
    //----------------------------------------------------------------------------------------------------
}