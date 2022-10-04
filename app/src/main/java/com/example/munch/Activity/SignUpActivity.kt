package com.example.munch.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.munch.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid


        Signup_btn.setOnClickListener {
            val userName = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirm_passsword.text.toString()

            database = FirebaseDatabase
                .getInstance()
                .getReference("Users/")
            val user = Users(userName, email, "$uid")
                /*.addOnSuccessListener{

           // database.child("$uid").setValue(user)



            }.addOnFailureListener {

                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }*/

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Email is:" + email)
                Log.d("MainActivity", "Password: $password")
                return@setOnClickListener
            } else if (password.length <6){
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            }
            else if (confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show()
                //return@setOnClickListener
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        Log.d("Main", "Failed to create user: ${it.result.user}")

                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener() {
                                if (it.isSuccessful) {

                                    Toast.makeText(this, "Sign up successful. Check Email for verification link", Toast.LENGTH_SHORT).show()
                                }
                                onBackPressed()
                            }

                    }
            }
        }


        tv_Login.setOnClickListener {
            onBackPressed()
        }
    }
}