package com.librum.ui.base

/**
 * @author lusinabrian on 05/09/17.
 * @Notes callback that manages page changes on the EPUB document
 */
interface PageFragmentCallback {

    fun hideOrShowToolBar()

    fun hideToolBarIfVisible()

    fun setPagerToPosition(href: String)

    fun setLastWebViewPosition(position: Int)

    fun goToChapter(href: String)
}