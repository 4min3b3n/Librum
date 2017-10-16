package com.librum.ui.reader

import android.os.Handler
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brck.moja.epubreader.R

import com.brck.moja.epubreader.data.model.TOCLinkWrapper
import com.librum.utils.DiffUtilCallback
import kotlinx.android.synthetic.main.row_epub_chapter.view.*
import org.jetbrains.anko.doAsync
import java.util.*
import javax.inject.Inject

/**
 * @author lusinabrian on 06/09/17.
 * @Notes adapter class for the chapters in the epub document
 */
class EpubReaderChapterAdapter
@Inject
constructor(var tocLinkWrapperList: ArrayList<TOCLinkWrapper>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_CHAPTER = 1
    private val VIEW_TYPE_EMPTY = 0
    lateinit var chapterCallback : EpubReaderChapterCallback

    /**
     * sets the callback for the chapter
     * @param chapterCallback Chapter callback interface
     * */
    fun setCallback(chapterCallback: EpubReaderChapterCallback){
        this.chapterCallback = chapterCallback
    }

    private val pendingUpdates = ArrayDeque<ArrayList<TOCLinkWrapper>>()

    override fun getItemCount() = tocLinkWrapperList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is TocViewHolder) {
            holder.onBind(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_CHAPTER -> {
                return TocViewHolder(LayoutInflater.from(parent?.context)
                        .inflate(R.layout.row_epub_chapter, parent, false),
                        tocLinkWrapperList)
            }
            VIEW_TYPE_EMPTY -> {
                return EmptyViewHolder(LayoutInflater.from(parent?.context)
                        .inflate(R.layout.row_epub_chapter_empty, parent, false))

            }
            else -> {
                return EmptyViewHolder(LayoutInflater.from(parent?.context)
                        .inflate(R.layout.row_epub_chapter_empty, parent, false))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (tocLinkWrapperList.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            VIEW_TYPE_CHAPTER
        }
    }

    /**
     * table of content item in TOC wrapper
     * */
    inner class TocViewHolder(itemView: View, var tocLinkWrapperList: ArrayList<TOCLinkWrapper>)
        : RecyclerView.ViewHolder(itemView) {
        fun onBind(position: Int) {
            val tocLinkWrapper = tocLinkWrapperList[position]
            with(itemView) {
                epubChapterTitle.text = tocLinkWrapper.tocLink.bookTitle

                setOnClickListener{ chapterCallback.onChapterClicked(adapterPosition) }
            }
        }
    }

    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(position: Int) {
            with(itemView) {

            }
        }
    }

    /**
     * gets the adapter item at the given position
     * @param position the position in the adapter
     * */
    fun getItemAt(position: Int) : TOCLinkWrapper {
        return tocLinkWrapperList[position]
    }

    /**
     * Add items to the list
     * Will check if the trending list items has a given item and only add items that are not
     * in the adapter
     * Uses [DiffUtil] to calculate whether this adapter should be updated
     * This allows us to perform quicker operations and not keep updating the same items over and
     * over again
     * Calculation of diffs is done on a background thread to avoid blocking operations,
     * hence the reason for a [doAsync] callback
     * @param newTocWrapperList Trending list with trending items
     * */
    fun addItems(newTocWrapperList: ArrayList<TOCLinkWrapper>) {
        pendingUpdates.add(newTocWrapperList)

        if (pendingUpdates.size > 1) {
            return
        }

        updateItemsInternal(newTocWrapperList)
    }

    /**
     * Updates items in adapter and calls a background thread to process
     * the diff and return it before updating the adapter of the change
     * */
    private fun updateItemsInternal(newTocLinkWrapperList: ArrayList<TOCLinkWrapper>) {
        val handler = Handler()
        Thread(Runnable {
            val diff = DiffUtilCallback(tocLinkWrapperList, newTocLinkWrapperList)
            val diffResult = DiffUtil.calculateDiff(diff)

            handler.post { applyDiffResult(newTocLinkWrapperList, diffResult) }
        }).start()
    }

    /**
     * Applies the diff result to the new items which will apply th dispatch to the adapter
     * @param newTocLinkWrapperItems
     * @param diffResult, result from Diffing of new and old items
     * */
    private fun applyDiffResult(newTocLinkWrapperItems: ArrayList<TOCLinkWrapper>, diffResult: DiffUtil.DiffResult) {
        pendingUpdates.remove()

        // dispatch updates
        dispatchUpdates(newTocLinkWrapperItems, diffResult)

        if (pendingUpdates.size > 0) {
            updateItemsInternal(pendingUpdates.peek())
        }
    }

    /**
     * Dispatches updates to adapter with new items and diff result
     * @param newTocLinkWrapperItems new items to add to adapter
     * @param diffResult diff result from callback
     * */
    private fun dispatchUpdates(newTocLinkWrapperItems: ArrayList<TOCLinkWrapper>, diffResult: DiffUtil.DiffResult) {
        diffResult.dispatchUpdatesTo(this)
        tocLinkWrapperList.clear()
        tocLinkWrapperList.addAll(newTocLinkWrapperItems)
    }

}