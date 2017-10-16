package com.librum.webviewmarker

/**
 * Interface to receive notifications when a drag starts or stops
 */
interface DragListener {
    fun onDragStart(source: DragSource, info: Any, dragBehavior: DragController.DragBehavior)
    fun onDragEnd()
}
