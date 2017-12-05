package com.librum.ui.pages

import android.os.Bundle
import android.view.View
import com.librum.ui.base.BasePresenter

/**
 * @author lusinabrian on 06/09/17.
 * @Notes page fragment presenter
 */
interface EpubPageFragmentPresenter<V : EpubPageFragmentView> : BasePresenter<V> {

    /**
     * on view created, initialize properties and fields from saved instance state
     * @param savedInstanceState saved instance state
     * */
    fun onViewCreated(savedInstanceState: Bundle?)

    /**
     * initialize animations
     * */
    fun onInitializeAnimations()

    /**
     * this will update the text for the pages left and time in book
     * @param percent the percentage of the scroll
     * */
    fun onScrollStateChanged(percent: Int)

    /**
     * Initializes the observable webview, where the epub document will be loaded
     * */
    fun onInitializeObservableWebView()

    /**
     * On set web view position
     * @param position position to set the web view at
     * */
    fun onSetWebViewPosition(position: Int)

    /**
     * gets the previous book state position
     * @param bookTitle the book title to use to query for the web position
     * @return [Int] the web view position to return
     * */
    fun onGetPreviousBookStateWebViewPosition(bookTitle: String): Int

    /**
     * callback for when a highlight item is clicked
     * @param mSelectedText the text that is selected
     * @param actionId action id of the action highlighted
     * @param view view to be used to access
     * @param isCreated is it created?
     * */
    fun onHighlightActionItemClicked(mSelectedText: String, actionId: Int, view: View, isCreated: Boolean)

    /**
     * Callback for when a highlight action item is clicked
     * @param actionId the action id
     * @param view the view to use
     * @param isCreated has the view been created
     */
    fun onHighlightColorsActionItemClicked(actionId: Int, view: View, isCreated: Boolean)

    /**
     * action for when a text action item is clicked
     * @param mSelectedText selecte text
     * @param actionId
     * @param view the view to use
     * @param width width of the text selection
     * @param height height of the text selection
     * */
    fun onTextSelectionActionItemClicked(mSelectedText: String, actionId: Int, view: View, width: Int, height: Int)

    /**
     * executes html download of a web page
     * @param webViewUrl the Url to download of a given web page
     * */
    fun onDownloadHtmlWebPage(webViewUrl: String)
}