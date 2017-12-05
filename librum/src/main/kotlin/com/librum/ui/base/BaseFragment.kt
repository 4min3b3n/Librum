package com.librum.ui.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.librum.di.components.EpubActivityComponent
import org.jetbrains.anko.AnkoLogger

/**
 * @author lusinabrian on 05/09/17.
 * @Notes base reader fragment
 */
abstract class BaseFragment : Fragment(), BaseView, AnkoLogger {
    /**
     * Gets the base activity this fragment is attached to
     * @return [AppBaseActivity]
     */
    lateinit var baseActivity: BaseActivity
        private set

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BaseActivity) {
            val mBaseActivity = context
            this.baseActivity = mBaseActivity
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
        get() = baseActivity.epubActivityComponent

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

    abstract fun setUp(view: View)

    override fun onError() {

    }

    override fun showErrorSnackbar(message: String, rootLayout: Int, length: Int) {
        if (baseActivity != null) {
            baseActivity.showErrorSnackbar(message, rootLayout, length)
        }
    }

    override fun showErrorSnackbar(message: Int, rootLayout: Int, length: Int) {
        if (baseActivity != null) {
            baseActivity.showErrorSnackbar(message, rootLayout, length)
        }
    }

    override fun isNetworkConnected(): Boolean {
        return baseActivity != null && baseActivity.isNetworkConnected()
    }
}