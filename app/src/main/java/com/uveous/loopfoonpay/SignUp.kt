package com.uveous.loopfoonpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val signup=findViewById<Button>(R.id.signup)
        val login=findViewById<TextView>(R.id.login)

        signup.setOnClickListener(View.OnClickListener {
            val Intent= Intent(this,TravelDashboard::class.java)
            startActivity(Intent)
        })
        login.setOnClickListener(View.OnClickListener {
            val Intent= Intent(this,LoginActivity::class.java)
            startActivity(Intent)
        })
    }
}