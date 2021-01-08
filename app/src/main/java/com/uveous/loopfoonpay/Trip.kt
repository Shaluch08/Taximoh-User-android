package com.uveous.loopfoonpay

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class Trip : AppCompatActivity(){

    private lateinit var cardview :CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trip)

        cardview=findViewById(R.id.card1)

        cardview.setOnClickListener(View.OnClickListener {
            val i= Intent(this,TripDetail::class.java)
            startActivity(i)
        })
    }
}