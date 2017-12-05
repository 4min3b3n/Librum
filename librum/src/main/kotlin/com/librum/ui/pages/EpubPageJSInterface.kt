package com.librum.ui.pages

import android.content.Context
import android.webkit.JavascriptInterface
import com.librum.data.db.HighLightTable
import com.librum.ui.reader.EpubReaderActivity
import com.librum.ui.widgets.ObservableWebView
import com.librum.utils.HighlightUtil
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONException
import java.util.*

/**
 * @author lusinabrian on 07/09/17.
 * @Notes JavaScript interface for the web pages on the epub
 */
class EpubPageJSInterface constructor(val context: Context) {

    lateinit var mObservableWebView: ObservableWebView
    var mHighlightMap: Map<String, String>? = null
    lateinit var mBookTitle: String
    var mPosition: Int = 0

    constructor(context: Context, mObservableWebView: ObservableWebView,
                mHighlightMap: Map<String, String>?, mBookTitle: String, mPosition: Int) : this(context) {
        this.mObservableWebView = mObservableWebView
        this.mHighlightMap = mHighlightMap
        this.mBookTitle = mBookTitle
        this.mPosition = mPosition
    }

    @JavascriptInterface
    fun getHighlightJson(mJsonResponse: String?) {
        if (mJsonResponse != null) {
            mHighlightMap = stringToJsonMap(mJsonResponse)
            context.runOnUiThread({ mObservableWebView.loadUrl("javascript:alert(getHTML())") })
        }
    }

    @JavascriptInterface
    fun getHtmlAndSaveHighlight(html: String?) {
        //fixme: highlight util
        if (html != null && mHighlightMap != null) {
            var highlight = HighlightUtil.matchHighlight(html, mHighlightMap?.get("id"),
                    mBookTitle, mPosition)
            highlight.currentWebviewScrollPos = mObservableWebView.scrollY
            highlight = (context as EpubReaderActivity).setCurrentPagerPosition(highlight)
            HighLightTable.insertHighlight(highlight)
        }
    }

    @JavascriptInterface
    fun getRemovedHighlightId(id: String?) {
        if (id != null) {
            HighLightTable.deleteHighlight(id)
        }
    }

    @JavascriptInterface
    fun getUpdatedHighlightId(id: String?, style: String) {
        if (id != null) {
            HighLightTable.updateHighlightStyle(id, style)
        }
    }

    /**
     * Converts a string to JSON map*/
    fun stringToJsonMap(string: String): Map<String, String> {
        val map = HashMap<String, String>()
        try {
            val jsonArray = JSONArray(string)
            val jObject = jsonArray.getJSONObject(0)
            val keys = jObject.keys()

            keys.hasNext()
            val key = keys.next() as String
            val value = jObject.getString(key)
            map.put(key, value)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return map
    }
}