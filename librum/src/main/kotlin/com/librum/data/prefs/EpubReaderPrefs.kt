package com.librum.data.prefs

import org.jetbrains.anko.AnkoLogger

/**
 * @author lusinabrian on 05/09/17.
 * @Notes preference interface
 */
interface EpubReaderPrefs : AnkoLogger {

    /**
     * get epub file location string
     * @param epubId unique identifier for the epub file
     * @return [String] file path to the epub file location
     * */
    fun getEpubFileLocation(epubId : String) : String

    /**
     * Saves the epub file location
     * */
    fun saveEpubFileLocation(fileKeyName: String, fileLocation: String)

    /**
     * remove book state
     * @param bookTitleKey Book title which will act as a key for accessing the shared prefs
     * */
    fun removeBookState(bookTitleKey : String)

    /**
     * saves the current book state
     * @param bookTitle book title, used as key
     * @param pageViewPagerPosition the position of the book in the view pager
     * @param webViewScrollPosition the scroll position of the web view
     * */
    fun saveBookState(bookTitle: String, pageViewPagerPosition : Int, webViewScrollPosition: Int)

    /**
     * Checks if the previous book state exists
     * @param bookName, used to check for the book in the shared preferences file
     * @return [Boolean] returns True if the state exists
     * */
    fun checkPreviousBookStateExist(bookName : String) : Boolean

    /**
     * Get the previous book state position
     * @param bookName the book name to retrieve the state o the position
     * @return [Int] the previous book state position for this given book
     * */
    fun getPreviousBookStatePosition(bookName: String) : Int

    /**
     * Gets previous book state web view position
     * @param bookTitle the book title to use to retrieve the previous book state webview position
     * @return [Int] the previous webview position of the given book
     * */
    fun getPreviousBookStateWebViewPosition(bookTitle: String) : Int
}