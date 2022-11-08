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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_singin.*

class singin : AppCompatActivity(),TextWatcher {
    private val mauth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singin)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        else
        {
            window.statusBarColor= Color.parseColor("#0C7897")
        }


        edtx_email_singin.addTextChangedListener(this)
        edtx_password_singin.addTextChangedListener(this)

        bu_login_singin.setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(edtx_email_singin.text.trim().toString()).matches())
            {
                edtx_email_singin.error="Email is valid"
                edtx_email_singin.requestFocus()
                return@setOnClickListener
            }
            if (edtx_password_singin.text.trim().toString().length<6)
            {
                edtx_password_singin.error="password is uncomplete"
                edtx_password_singin.requestFocus()
                return@setOnClickListener
            }
            singin()
        }

        bu_createNA_singin.setOnClickListener {
            var intent=Intent(this, singup::class.java)
            startActivity(intent)
        }
    }


    fun singin()
    {
        pb_singin_singin.visibility= View.VISIBLE
        var email=edtx_email_singin.text.trim().toString()
        var password=edtx_password_singin.text.trim().toString()

        mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful)
            {
                pb_singin_singin.visibility= View.GONE
                var intent=Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            else
            {
                pb_singin_singin.visibility= View.GONE
                Toast.makeText(this,"${it.exception}", Toast.LENGTH_LONG).show()
            }
        }
    }


    //-------------------------------------------START FUN OF TEXT WATCHER-----------------------------------
    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        bu_login_singin.isEnabled=edtx_email_singin.text.trim().toString().isNotEmpty()&&
                edtx_password_singin.text.trim().toString().isNotEmpty()
    }
    //----------------------------------------------------------------------------------------------------
}