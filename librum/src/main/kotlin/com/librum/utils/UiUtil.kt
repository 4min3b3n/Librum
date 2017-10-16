@file:JvmName("UiUtil")

package com.librum.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import java.lang.ref.SoftReference
import java.util.*

private val fontCache = Hashtable<String, SoftReference<Typeface>>()

fun setCustomFont(view: View, ctx: Context, attrs: AttributeSet,
                  attributeSet: IntArray, fontId: Int) {
    val a = ctx.obtainStyledAttributes(attrs, attributeSet)
    val customFont = a.getString(fontId)
    setCustomFont(view, ctx, customFont)
    a.recycle()
}

fun setCustomFont(view: View, ctx: Context, asset: String?): Boolean {
    if (TextUtils.isEmpty(asset)) {
        return false
    }
    var tf: Typeface? = null
    try {
        tf = getFont(ctx, asset!!)
        if (view is TextView) {
            view.typeface = tf
        } else {
            (view as Button).typeface = tf
        }
    } catch (e: Exception) {
        Log.e("AppUtil", "Could not get typface  " + asset!!)
        return false
    }

    return true
}

fun getFont(c: Context, name: String): Typeface {
    synchronized(fontCache) {
        if (fontCache[name] != null) {
            val ref = fontCache[name]
            if (ref?.get() != null) {
                return ref.get()!!
            }
        }

        val typeface = Typeface.createFromAsset(c.assets, name)
        fontCache.put(name, SoftReference(typeface))

        return typeface
    }
}

fun getColorList(context: Context, selectedColor: Int,
                 unselectedColor: Int): ColorStateList {
    val states = arrayOf(intArrayOf(android.R.attr.state_pressed), // pressed
            intArrayOf(android.R.attr.state_selected), // focused
            intArrayOf())
    val colors = intArrayOf(ContextCompat.getColor(context, selectedColor), // green
            ContextCompat.getColor(context, selectedColor), // green
            ContextCompat.getColor(context, unselectedColor)  // white
    )
    return ColorStateList(states, colors)
}

fun keepScreenAwake(enable: Boolean, context: Context) {
    if (enable) {
        (context as Activity)
                .window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
        (context as Activity)
                .window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

fun setBackColorToTextView(textView: UnderlinedTextView, type: String) {
    val context = textView.context
    when (type) {
        "highlight-yellow" -> {
            textView.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.yellow))
            textView.underlineWidth = 0.0f
        }
        "highlight-green" -> {
            textView.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.green))
            textView.underlineWidth = 0.0f
        }
        "highlight-blue" -> {
            textView.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.blue))
            textView.underlineWidth = 0.0f
        }
        "highlight-pink" -> {
            textView.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.pink))
            textView.underlineWidth = 0.0f
        }
        "highlight-underline" -> {
            textView.underLineColor = ContextCompat.getColor(context,
                    android.R.color.holo_red_dark)
            textView.underlineWidth = 2.0f
        }
    }
}

fun convertDpToPixel(dp: Float, context: Context): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("copy", text)
    clipboard.primaryClip = clip
}

fun share(context: Context, text: String) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, text)
    sendIntent.type = "text/plain"
    context.startActivity(Intent.createChooser(sendIntent, context.resources.getText(R.string.send_to)))
}
