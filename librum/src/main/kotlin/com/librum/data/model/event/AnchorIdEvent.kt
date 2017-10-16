package com.brck.moja.epubreader.data.model.event

/**
 * Anchor id event that is used to trigger the chapter to move to in the book
 * @param href hyperlink to the chapter
 * */
data class AnchorIdEvent(val href: String)