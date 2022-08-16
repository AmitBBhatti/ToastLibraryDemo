package com.example.librarydemo


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvSubmit=findViewById<TextView>(R.id.tvSubmit)

        tvSubmit.setOnClickListener {
           /* var intent=Intent(MainActivity@this,TestActivity::class.java)
            startActivity(intent)*/
        }
    }
}