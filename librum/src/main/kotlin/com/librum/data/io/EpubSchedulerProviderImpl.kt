package com.librum.data.io

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author lusinabrian on 19/09/17.
 * @Notes Scheduler provider implementation
 */
class EpubSchedulerProviderImpl : EpubSchedulerProvider {
    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun newThread(): Scheduler {
        return Schedulers.newThread()
    }

    override fun trampoline(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun start() {
        Schedulers.start()
    }

    override fun shutdown() {
        Schedulers.shutdown()
    }

    override fun single(): Scheduler {
        return Schedulers.single()
    }
}