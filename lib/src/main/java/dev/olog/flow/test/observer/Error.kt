package dev.olog.flow.test.observer

sealed class Error {

    object Empty : Error()

    data class Wrapped(val throwable: Throwable) : Error()

}