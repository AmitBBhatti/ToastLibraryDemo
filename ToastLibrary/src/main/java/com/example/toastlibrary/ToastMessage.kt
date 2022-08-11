package com.example.toastlibrary

import android.content.Context
import android.widget.Toast
import java.security.AccessControlContext

object ToastMessage {

    fun showMessage(s:String,context: Context){
        Toast.makeText(context,s,Toast.LENGTH_LONG).show()
    }
}