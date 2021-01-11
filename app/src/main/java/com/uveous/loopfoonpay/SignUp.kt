package com.uveous.loopfoonpay

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.userlogin
import com.uveous.loopfoonpay.Api.Model.usersignup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SignUp : AppCompatActivity() {
    lateinit var dob: TextInputEditText
    lateinit var name: TextInputEditText
    lateinit var email: TextInputEditText
    lateinit var phone: TextInputEditText
    lateinit var pwd: TextInputEditText
    lateinit var cpwd: TextInputEditText
    lateinit var radioGroup: RadioGroup
    private var mYear = 0
    private  var mMonth:Int = 0
    private  var mDay:Int = 0
    lateinit var gender :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        dob=findViewById(R.id.dob)
        name=findViewById(R.id.name)
        email=findViewById(R.id.email)
        phone=findViewById(R.id.phone)
        pwd=findViewById(R.id.pwd)
        cpwd=findViewById(R.id.cpwd)
        radioGroup=findViewById(R.id.radioGroup)

        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                    val radio: RadioButton = findViewById(checkedId)
                      gender=radio.text.toString()
                    Toast.makeText(applicationContext," On checked change :"+
                            " ${radio.text}",
                            Toast.LENGTH_SHORT).show()
                })


        dob.setOnClickListener(View.OnClickListener {
        val c: Calendar = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth -> dob.setText(year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth) }, mYear, mMonth, mDay)
        datePickerDialog.show()

        })

        val signup=findViewById<Button>(R.id.signup)
        val login=findViewById<TextView>(R.id.login)

        signup.setOnClickListener(View.OnClickListener {
            signupfun()
           /* val Intent= Intent(this,TravelDashboard::class.java)
            startActivity(Intent)*/
        })
        login.setOnClickListener(View.OnClickListener {
            val Intent= Intent(this,LoginActivity::class.java)
            startActivity(Intent)
        })
    }

    fun signupfun(){
        if(email.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please enter email", Toast.LENGTH_SHORT).show()
        }else if(pwd.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please enter password", Toast.LENGTH_SHORT).show()
        }else if(phone.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please enter phone number", Toast.LENGTH_SHORT).show()
        }else if(name.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please enter name", Toast.LENGTH_SHORT).show()
        }else if(dob.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please select dob", Toast.LENGTH_SHORT).show()
        }else if(gender.isEmpty()){
            Toast.makeText(this@SignUp,"Please select gender", Toast.LENGTH_SHORT).show()
        }else if(cpwd.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please enter confirm password", Toast.LENGTH_SHORT).show()
        }else if(!cpwd.text.toString().equals(pwd.text.toString())){
            Toast.makeText(this@SignUp,"Please enter correct confirm password", Toast.LENGTH_SHORT).show()
        }else {
            val progressDialog = ProgressDialog(this@SignUp)
            // progressDialog.setTitle("Kotlin Progress Bar")
            progressDialog.setMessage("Please wait")
            progressDialog.show()
            var mAPIService: ApiService? = null
            mAPIService = ApiClient.apiService
            mAPIService!!.register(
                name.text.toString(),
                phone.text.toString(),
                email.text.toString(),
                gender,
                dob.text.toString(),
                pwd.text.toString()
            ).enqueue(object : Callback<usersignup> {
                override fun onResponse(call: Call<usersignup>, response: Response<usersignup>) {
                    Log.i("", "post submitted to API." + response.body()!!)
                    if (response.isSuccessful()) {
                        var lo: usersignup = response.body()!!
                        progressDialog.dismiss()
                        val mPrefs: SharedPreferences = this@SignUp.getSharedPreferences(
                            "TASK_ID",
                            Context.MODE_PRIVATE
                        )
                        val prefsEditor = mPrefs.edit()
                        lo.user_id?.let { prefsEditor.putInt("userid", it) }
                        prefsEditor.commit()
                        Toast.makeText(this@SignUp, "Register", Toast.LENGTH_SHORT).show()
                        Log.v("dd", "post registration to API" + response.body()!!.toString())
                        val Intent = Intent(applicationContext, TravelDashboard::class.java)
                        startActivity(Intent)

                    }
                }

                override fun onFailure(call: Call<usersignup>, t: Throwable) {
                    Toast.makeText(this@SignUp, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}