package com.example.loadimagefromurl

import android.graphics.drawable.Drawable
import android.util.Log
import java.io.InputStream
import java.lang.Error
import java.net.URL

class MainActivityPresenter (view: MainActivity){
    fun loadImageFromWeb(imageUrl: String): Drawable? {
        try{
            val input: InputStream = URL(imageUrl).content as InputStream
            var d = Drawable.createFromStream(input, "src")
            Log.i("Rohit","Returning drawable")
            return d
        }
        catch (e: Error){
            Log.i("Rohit", "exception $e")
            return null
        }
    }
}