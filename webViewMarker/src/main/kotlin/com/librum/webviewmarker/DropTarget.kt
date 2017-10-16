package com.librum.webviewmarker

import android.graphics.Rect

/**
 * Interface defining an object that can receive a view at the end of a drag operation.
 *
 */
interface DropTarget {
    val left: Int
    val top: Int

    /**
     * Handle an object being dropped on the DropTarget
     *
     * @param source DragSource where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param xOffset Horizontal offset with the object being dragged where the original
     * touch happened
     * @param yOffset Vertical offset with the object being dragged where the original
     * touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     */
    fun onDrop(source: DragSource, x: Int, y: Int, xOffset: Int, yOffset: Int, dragView: DragView, dragInfo: Any)

    fun onDragEnter(source: DragSource, x: Int, y: Int, xOffset: Int, yOffset: Int, dragView: DragView, dragInfo: Any)

    fun onDragOver(source: DragSource, x: Int, y: Int, xOffset: Int, yOffset: Int, dragView: DragView, dragInfo: Any)

    fun onDragExit(source: DragSource, x: Int, y: Int, xOffset: Int, yOffset: Int, dragView: DragView, dragInfo: Any)

    /**
     * Check if a drop action can occur at, or near, the requested location.
     * This may be called repeatedly during a drag, so any calls should return
     * quickly.
     *
     * @param source DragSource where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param xOffset Horizontal offset with the object being dragged where the
     * original touch happened
     * @param yOffset Vertical offset with the object being dragged where the
     * original touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     * @return True if the drop will be accepted, false otherwise.
     */
    fun acceptDrop(source: DragSource, x: Int, y: Int, xOffset: Int, yOffset: Int, dragView: DragView, dragInfo: Any): Boolean

    /**
     * Estimate the surface area where this object would land if dropped at the
     * given location.
     *
     * @param source DragSource where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param xOffset Horizontal offset with the object being dragged where the
     * original touch happened
     * @param yOffset Vertical offset with the object being dragged where the
     * original touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     * @param recycle [Rect] object to be possibly recycled.
     * @return Estimated area that would be occupied if object was dropped at
     * the given location. Should return null if no estimate is found,
     * or if this target doesn't provide estimations.
     */
    fun estimateDropLocation(source: DragSource, x: Int, y: Int, xOffset: Int, yOffset: Int, dragView: DragView, dragInfo: Any, recycle: Rect): Rect

    // These methods are implemented in Views
    fun getHitRect(outRect: Rect)

    fun getLocationOnScreen(loc: IntArray)
}
