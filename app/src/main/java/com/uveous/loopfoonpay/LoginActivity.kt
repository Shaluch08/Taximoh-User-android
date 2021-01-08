package com.uveous.loopfoonpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val butlogin= findViewById<Button>(R.id.login)
        val reg= findViewById<TextView>(R.id.reg)
        val forgotpassword= findViewById<TextView>(R.id.forgotpassword)

        butlogin.setOnClickListener(View.OnClickListener {
            val Intent= Intent(this,TravelDashboard::class.java)
            startActivity(Intent)
        })

        reg.setOnClickListener(View.OnClickListener {
            val i= Intent(this,SignUp::class.java)
            startActivity(i)
        })
        forgotpassword.setOnClickListener(View.OnClickListener {
            val i= Intent(this,ForgotActivity::class.java)
            startActivity(i)
        })
    }
}