package com.librum.ui.pages

import android.os.Bundle
import android.view.View
import com.librum.ui.base.BaseView

/**
 * @author lusinabrian on 05/09/17.
 * @Notes Page Fragment view
 */
interface EpubPageFragmentView : BaseView {

    /**
     * receive bundle for page of fragment
     * @param savedInstanceState Bundle for the page
     * */
    fun receiveBundleForPage(savedInstanceState: Bundle?)

    /**
     * initialize animations
     * */
    fun initializeAnimations()

    /**
     * initialize the web view
     * */
    fun initializeWebView()

    /**
     * Updates the pages left in the book
     * */
    fun updatePagesLeft(percent: Int)

    /**
     * sets the web view position
     * @param position the position to set the web view at
     * */
    fun setWebViewPosition(position: Int)

    /**
     * update the view highlight from the text selection
     * @param view view to update
     * @param width width to update
     * @param height the height to update
     * @param isCreated is it created? check for whether the view is created
     * */
    fun onHighlightView(view: View, width: Int, height: Int, isCreated: Boolean)

    /**
     * Updates the highlight colors
     * @param view view to be used
     * @param isCreated has this view been created?
     * */
    fun highlightColorAction(view: View, isCreated: Boolean)

    /**
     * Action to highlight the given text
     * @param style the highlight style
     * @param isCreated whether the view has been created
     * */
    fun highlight(style: Highlight.HighlightStyle, isCreated: Boolean)

    /**
     * HighLight action
     * @param x
     * @param y
     * @param width width of the text selection
     * @param height height of the selected text
     * */
    fun onHighlightAction(x: Int, y: Int, width: Int, height: Int)

    /**
     * share action to enable sharing the given highlighted text to another user
     * @param mSelectedText the text selected
     * */
    fun shareAction(mSelectedText: String)

    /**
     * This removes the given highlight from the higlight table
     * */
    fun removeHighlightAction()

    /**
     * show the define action dialog
     * @param mSelectedText the selected text to define
     * */
    fun showDefineActionDialog(mSelectedText: String)

    /**
     * copy the given selected text to clip board
     * @param mSelectedText Selected text to copy to clipboard
     * */
    fun copyToClipBoardAction(mSelectedText: String)

    /**
     * on receiving the html document string, it is set to the web view for viewing
     * @param html the html string to load into the web page
     * */
    fun onReceiveHtml(html: String)

    /**
     * sets the html document string
     * @param reloaded whether the document has been reloaded or not
     * */
    fun setHtmlToObservableWebView(reloaded: Boolean)

    /**
     * jumps to a given anchor point when an item on the navigation is clicked
     * */
    fun jumpToAnchorPoint()
}