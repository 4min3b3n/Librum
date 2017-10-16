package com.librum.ui.base

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.View
import android.widget.TextView
import com.librum.di.components.EpubActivityComponent
import com.librum.di.components.EpubComponent
import com.librum.di.modules.EpubActivityModule
import com.librum.di.modules.EpubDatabaseModule
import com.librum.di.modules.EpubModule
import com.librum.di.modules.EpubServerModule
import com.librum.utils.isNetworkAvailable
import org.jetbrains.anko.AnkoLogger

/**
 * @author lusinabrian on 05/09/17.
 * @Notes base reader activity
 */
abstract class BaseActivity : AppCompatActivity(), BaseView, BaseFragment.Callback, AnkoLogger {

    lateinit var epubActivityComponent: EpubActivityComponent
        private set

    lateinit var epubComponent: EpubComponent

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        epubComponent = DaggerEpubComponent.builder()
                .epubModule(EpubModule())
                .epubServerModule(EpubServerModule())
                .epubDatabaseModule(EpubDatabaseModule())
                .build()

        epubActivityComponent = DaggerEpubActivityComponent.builder()
                .epubComponent(epubComponent)
                .baseActivityModule(BaseActivityModule(this))
                .epubActivityModule(EpubActivityModule())
                .build()
    }

    override fun onError() {
    }

    override fun showErrorSnackbar(message: String, rootLayout: Int, length: Int) {
        val snackbar = Snackbar.make(findViewById<View>(rootLayout), message, length)
        val view = snackbar.view
        val textView = view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }

    override fun showErrorSnackbar(message: Int, rootLayout: Int, length: Int) {
        showErrorSnackbar(getString(message), rootLayout, length)
    }

    override fun isNetworkConnected(): Boolean {
        return isNetworkAvailable(this)
    }

    override fun onFragmentAttached() {
    }

    override fun onFragmentDetached(tag: String) {
    }
}