package com.example.android.politicalpreparedness.utils

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import com.google.android.material.snackbar.Snackbar

class Utils {

    companion object {
        fun isConnectionAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }

        fun useSnackBar(view: View, message: String){
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        }

    }
}