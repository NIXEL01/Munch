package com.example.munch.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.munch.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {

    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        handler = Handler()
        handler.postDelayed({

            val intent = Intent(this,LoginActivity::class.java )
            startActivity(intent)
            finish()
        },3000)
    }
}