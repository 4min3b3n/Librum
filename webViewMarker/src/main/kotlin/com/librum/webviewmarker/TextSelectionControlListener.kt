package com.librum.webviewmarker

interface TextSelectionControlListener {
    fun jsError(error: String)
    fun jsLog(message: String)
    fun startSelectionMode()
    fun endSelectionMode()

    /**
     * Tells the listener to show the context menu for the given range and selected text.
     * The bounds parameter contains a json string representing the selection bounds in the form
     * { 'left': leftPoint, 'top': topPoint, 'right': rightPoint, 'bottom': bottomPoint }
     * @param range
     * @param text
     * @param handleBounds
     * @param isReallyChanged
     */
    fun selectionChanged(range: String, text: String, handleBounds: String, isReallyChanged: Boolean)

    fun setContentWidth(contentWidth: Float)
}