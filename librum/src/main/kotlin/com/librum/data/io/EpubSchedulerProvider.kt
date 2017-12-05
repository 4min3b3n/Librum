package com.librum.data.io

import io.reactivex.Scheduler

/**
 * @author lusinabrian on 19/09/17.
 * @Notes Scheduler provider
 */
interface EpubSchedulerProvider {

    fun ui(): Scheduler

    fun computation(): Scheduler

    fun io(): Scheduler

    fun newThread(): Scheduler

    fun trampoline(): Scheduler

    fun start()

    fun shutdown()

    fun single(): Scheduler
}