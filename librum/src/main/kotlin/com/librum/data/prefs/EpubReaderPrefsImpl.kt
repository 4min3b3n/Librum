package com.librum.data.prefs

import android.content.Context
import com.librum.di.qualifiers.AppContextQualifier
import com.brck.moja.base.utils.removeData
import com.brck.moja.base.utils.saveData
import com.librum.BOOK_STATE
import com.librum.BOOK_TITLE
import com.librum.VIEWPAGER_POSITION
import com.librum.WEBVIEW_SCROLL_POSITION
import org.jetbrains.anko.error
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author lusinabrian on 05/09/17.
 * @Notes implementation for preference layer for the shared preference
 */
@Singleton
class EpubReaderPrefsImpl
@Inject
constructor(@AppContextQualifier context: Context,
            @Named("EpubPrefs") prefFileName: String) : EpubReaderPrefs {

    override val loggerTag: String get() = super.loggerTag

    private val mSharedPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    override fun saveEpubFileLocation(fileKeyName: String, fileLocation: String) {
        mSharedPrefs.saveData(fileKeyName, fileLocation)
    }

    override fun getEpubFileLocation(epubId: String): String {
        return mSharedPrefs.getString(epubId, "")
    }

    override fun removeBookState(bookTitleKey: String) {
        mSharedPrefs.removeData(bookTitleKey + BOOK_STATE)
    }

    override fun saveBookState(bookTitle: String, pageViewPagerPosition: Int, webViewScrollPosition: Int) {
        val obj = JSONObject()
        try {
            obj.put(BOOK_TITLE, bookTitle)
            obj.put(WEBVIEW_SCROLL_POSITION, webViewScrollPosition)
            obj.put(VIEWPAGER_POSITION, pageViewPagerPosition)
            mSharedPrefs.saveData(bookTitle + BOOK_STATE, obj.toString())
        } catch (je: JSONException) {
            je.printStackTrace()
            error("Failed to save book state ${je.message}")
        }
    }

    override fun checkPreviousBookStateExist(bookName: String): Boolean {
        val json = mSharedPrefs.getString(bookName + BOOK_STATE, null)
        if (json != null) {
            try {
                val jsonObject = JSONObject(json)
                val bookTitle = jsonObject.getString(BOOK_TITLE)
                if (bookTitle == bookName) {
                    return true
                }
            } catch (je: JSONException) {
                je.printStackTrace()
                error("Failed to check book state with err ${je.message}", je.cause)
                return false
            }
        }
        return false
    }

    override fun getPreviousBookStatePosition(bookName: String): Int {
        val json = mSharedPrefs.getString(bookName + BOOK_STATE, null)
        return prefsJsonUtil(json, VIEWPAGER_POSITION)
    }

    override fun getPreviousBookStateWebViewPosition(bookTitle: String): Int {
        val json = mSharedPrefs.getString(bookTitle + BOOK_STATE, "")
        return prefsJsonUtil(json, WEBVIEW_SCROLL_POSITION)
    }

    /**
     * Used to retrieve the json data from the key
     * @param json the json key to use to create a json object
     * @param jsonName node name to retrieve data from
     * @return [Int] position of the given book
     * */
    private fun prefsJsonUtil(json: String, jsonName: String): Int {
        return try {
            val jsonObject = JSONObject(json)
            jsonObject.getInt(jsonName)
        } catch (e: JSONException) {
            error("Failed to get $jsonName position of book ${e.message}", e.cause)
            0
        }
    }
}