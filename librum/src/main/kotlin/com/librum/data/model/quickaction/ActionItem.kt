package com.brck.moja.epubreader.data.model.quickaction

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

/**
 * @author lusinabrian on 26/09/17.
 * @Notes Action item data
 */
data class ActionItem(
        var actionId: Int = -1,
        var title: String? = null,
        var icon: Drawable? = null,
        var thumb: Bitmap? = null,
        var selected: Boolean = false,
        var sticky: Boolean = false
)