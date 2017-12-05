@file:JvmName("HtmlUtil")

package com.librum.utils

import android.content.Context
import com.librum.R
import com.librum.data.db.HighLightTable
import java.util.regex.Pattern


/**
 * @author lusinabrian on 19/09/17.
 * @Notes HTML Utils
 */

/**
 * Function modifies input html string by adding extra css,js and font information.
 *
 * @param context     Activity Context
 * @param htmlContent input html raw data
 * @param mBookTitle  Epub book title
 * @return modified raw html string
 */
fun getHtmlContent(context: Context, htmlContent: String, mBookTitle: String): String {
    var htmlContent = htmlContent
    val cssPath = String.format(context.getString(R.string.css_tag), "file:///android_asset/Style.css")
    var jsPath = String.format(context.getString(R.string.script_tag),
            "file:///android_asset/Bridge.js")
    jsPath = jsPath + String.format(context.getString(R.string.script_tag),
            "file:///android_asset/jquery-1.8.3.js")
    jsPath = jsPath + String.format(context.getString(R.string.script_tag),
            "file:///android_asset/jpntext.js")
    jsPath = jsPath + String.format(context.getString(R.string.script_tag),
            "file:///android_asset/rangy-core.js")
    jsPath = jsPath + String.format(context.getString(R.string.script_tag),
            "file:///android_asset/rangy-serializer.js")
    jsPath = jsPath + String.format(context.getString(R.string.script_tag),
            "file:///android_asset/android.selection.js")
    jsPath = jsPath + String.format(context.getString(R.string.script_tag_method_call),
            "setMediaOverlayStyleColors('#C0ED72','#C0ED72')")
    val toInject = "\n" + cssPath + "\n" + jsPath + "\n</head>"
    htmlContent = htmlContent.replace("</head>", toInject)

    var classes = ""

    htmlContent = htmlContent.replace("<html ", "<html class=\"$classes\" ")
    val highlights = HighLightTable.getAllHighlights(mBookTitle)
    for (highlight in highlights) {
        val highlightStr = highlight.contentPre +
                "<highlight id=\"" + highlight.highlightId +
                "\" onclick=\"callHighlightURL(this);\" class=\"" +
                highlight.type + "\">" + highlight.content + "</highlight>" + highlight.contentPost
        val searchStr = highlight.contentPre +
                "" + highlight.content + "" + highlight.contentPost
        htmlContent = htmlContent.replaceFirst(Pattern.quote(searchStr).toRegex(), highlightStr)
    }
    return htmlContent
}