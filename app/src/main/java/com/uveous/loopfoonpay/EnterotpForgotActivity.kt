package com.uveous.loopfoonpay

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText

class EnterotpForgotActivity : AppCompatActivity(){
    lateinit var toolbar: Toolbar
    lateinit var logindata: Button
    lateinit var enterotp: TextInputEditText
    var otp:Int=0
    var userid:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterotpforgot)
        toolbar=findViewById(R.id.toolbar)
        logindata=findViewById(R.id.logindata)

        enterotp=findViewById(R.id.enterotp)

        otp=intent.getIntExtra("otp",0)
        userid=intent.getIntExtra("userid",0)


        toolbar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this,ForgotActivity::class.java))
        })

        logindata.setOnClickListener(View.OnClickListener {
            if(enterotp.text.toString().contentEquals(otp.toString())){
                startActivity(Intent(this,ConfirmForgotActivity::class.java)
                        .putExtra("userid",userid))
                logindata.isEnabled=false

            }else{
                Toast.makeText(this,"OTP not matched", Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,ForgotActivity::class.java))

    }
}
