package com.librum.ui.base

import android.support.annotation.StringRes

/**
 * @author lusinabrian on 05/09/17.
 * @Notes base reader view interface for epub reader
 */
interface BaseView {
    fun onError()

    /**
     * shows the connection error snackbar. If there is no connection to any internet connection
     * @param message message to displaye
     * *
     * @param length how long to display this message
     */
    fun showErrorSnackbar(message: String, rootLayout: Int, length: Int)

    fun showErrorSnackbar(@StringRes message: Int, rootLayout: Int, length: Int)

    /**
     * Checks if there is network connected
     * Returns True if the device is connected to a network, false otherwise
     * @return [Boolean]
     */
    fun isNetworkConnected(): Boolean
}