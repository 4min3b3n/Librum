package com.librum.ui.base

import android.os.Bundle
import com.brck.moja.base.di.modules.BaseActivityModule
import com.brck.moja.base.di.modules.BaseAppModule
import com.brck.moja.base.ui.base.BaseActivity
import com.moja.brck.epubreader.di.components.DaggerEpubActivityComponent
import com.moja.brck.epubreader.di.components.DaggerEpubComponent
import com.librum.di.components.EpubActivityComponent
import com.librum.di.components.EpubComponent
import com.librum.di.modules.EpubActivityModule
import com.librum.di.modules.EpubDatabaseModule
import com.librum.di.modules.EpubModule
import com.librum.di.modules.EpubServerModule
import org.jetbrains.anko.AnkoLogger

/**
 * @author lusinabrian on 05/09/17.
 * @Notes base reader activity
 */
abstract class BaseReaderActivity : BaseActivity(), BaseReaderView, BaseReaderFragment.Callback, AnkoLogger {

    lateinit var epubActivityComponent : EpubActivityComponent
        private set

    lateinit var epubComponent : EpubComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        epubComponent = DaggerEpubComponent.builder()
                .baseAppModule(BaseAppModule(application))
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
}