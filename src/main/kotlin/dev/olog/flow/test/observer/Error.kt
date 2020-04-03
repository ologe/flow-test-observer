package dev.olog.flow.test.observer

internal sealed class Error {

    object Empty : Error()

    data class Wrapped(val throwable: Throwable) : Error()

}