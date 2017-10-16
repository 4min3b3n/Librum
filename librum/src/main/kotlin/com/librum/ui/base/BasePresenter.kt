package com.librum.ui.base


/**
 * @author lusinabrian on 05/09/17.
 * @Notes presenter interface for reader
 */
interface BasePresenter<V : BaseView> {
    fun onAttach(mBaseView: V)

    fun onDetach()

}