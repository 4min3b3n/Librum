package com.librum.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener

/**
 * @author lusinabrian on 05/12/17.
 * @Notes Extension utils
 */

fun <T> SharedPreferences.saveData(key: String, value: T) {
    when (value) {
        is String -> this.edit().putString(key, value.toString()).apply()
        is Boolean -> this.edit().putBoolean(key, value).apply()
        is Int -> this.edit().putInt(key, value.toInt()).apply()
        is Float -> this.edit().putFloat(key, value.toFloat()).apply()
        is Long -> this.edit().putLong(key, value.toLong()).apply()
    }
}

/**
 * Removes data from the shared preference file given the key of the data item
 * @param key, the key used to find the data item*/
fun SharedPreferences.removeData(key: String): Boolean {
    return edit().remove(key).commit()
}

fun ImageView.loadImageFromUrl(context: Context, url: String, progressBar: View? = null) {
    Glide.with(context)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .thumbnail(1F)
            .into(this)
}