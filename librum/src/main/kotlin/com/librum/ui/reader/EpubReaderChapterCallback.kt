package com.librum.ui.reader

/**
 * @author lusinabrian on 07/09/17.
 * @Notes interface to handle chapter click events on drawer, if any
 */
interface EpubReaderChapterCallback {
    /**
     * on chapter clicked, we get the current position of the view and navigate to it
     * @param position the position of the given item in the adapter
     * */
    fun onChapterClicked(position: Int)

    /**
     * on Chapter expanded callback used to expand the current chapter selection, if it has children
     * @param position the current position of the chapter item
     * */
    fun onChapterExpanded(position: Int)
}