package com.librum.webviewmarker

import android.view.View

/**
 * Interface defining an object where drag operations originate.
 *
 */
interface DragSource {

    /**
     * This method is called to determine if the DragSource has something to drag.
     *
     * @return True if there is something to drag
     */

    fun allowDrag(): Boolean

    /**
     * This method is used to tell the DragSource which drag controller it is working with.
     *
     * @param dragger DragController
     */

    fun setDragController(dragger: DragController)

    /**
     * This method is called on the completion of the drag operation so the DragSource knows
     * whether it succeeded or failed.
     *
     * @param target View - the view that accepted the dragged object
     * @param success boolean - true means that the object was dropped successfully
     */

    fun onDropCompleted(target: View, success: Boolean)
}
