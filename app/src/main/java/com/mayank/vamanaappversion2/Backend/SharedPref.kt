package com.mayank.vamanaappversion2.Backend

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("vamana data", Context.MODE_PRIVATE)
}


fun saveList(context: Context, key: String, list: List<String>) {
    val sharedPrefs = context.getSharedPreferences("vamana data", Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    val json = Gson().toJson(list)
    editor.putString(key, json)
    editor.apply()
}

// Retrieve list from SharedPreferences
fun getList(context: Context, key: String): List<String> {
    val sharedPrefs = context.getSharedPreferences("vamana data", Context.MODE_PRIVATE)
    val json = sharedPrefs.getString(key, null)
    return if (json != null) {
        val type = object : TypeToken<List<String>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}