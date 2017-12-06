package com.librum.utils

/**
 * @author lusinabrian on 06/12/17.
 * @Notes Custom Librum Error
 */
class LibrumError(message: String) : Exception(message){

    constructor(message: String, cause: Throwable) : this(message)
}