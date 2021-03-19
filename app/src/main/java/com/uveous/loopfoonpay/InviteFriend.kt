package com.uveous.loopfoonpay

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.uveous.taximohdriver.TravelDashboard

class InviteFriend :AppCompatActivity(){
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.invitefriend)
        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this, TravelDashboard::class.java))

        })


    }
}