package com.mayank.vamanaappversion2.Backend

import android.content.Context
import android.content.SharedPreferences

fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("vamana data", Context.MODE_PRIVATE)
}
