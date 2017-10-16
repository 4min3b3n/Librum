package com.librum.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brck.moja.base.ui.base.BaseFragment
import com.librum.di.components.EpubActivityComponent
import org.jetbrains.anko.AnkoLogger

/**
 * @author lusinabrian on 05/09/17.
 * @Notes base reader fragment
 */
abstract class BaseReaderFragment : BaseFragment(), BaseReaderView, AnkoLogger{
    /**
     * Gets the base activity this fragment is attached to
     * @return [AppBaseActivity]
     */
    var baseReaderActivity: BaseReaderActivity? = null
        private set

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BaseReaderActivity) {
            val mBaseActivity = context
            this.baseReaderActivity = mBaseActivity
            mBaseActivity.onFragmentAttached()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    /**
     * Gets the activity component of the activity this fragment is attached to
     * @return [EpubActivityComponent]
     */
    val epubActivityComponent: EpubActivityComponent
        get() = baseReaderActivity!!.epubActivityComponent

    /**
     * Callback interface for this fragment */
    interface Callback {
        /**
         * Callback for when a fragment is attached to an activity
         */
        fun onFragmentAttached()

        /**
         * Callback for when a fragment is detached from an activity
         * @param tag the fragment tag to detach
         */
        fun onFragmentDetached(tag: String)
    }

    abstract override fun setUp(view: View)

    override fun onError() {

    }
}