package dev.olog.flow.test.observer

interface FlowTestObserver<T> {

    suspend fun assertValues(vararg expected: T)

}