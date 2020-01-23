package dev.olog.flow.test.observer

import dev.olog.flow.test.observer.impl.FiniteFlowObserver
import dev.olog.flow.test.observer.impl.InfiniteFlowObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout

fun <T> Flow<T>.test(timeout: Long = 10): FlowTestObserver<T> {
    return FlowTestObserverImpl(this, timeout)
}


internal class FlowTestObserverImpl<T>(
    private val flow: Flow<T>,
    private val timeout: Long
) : FlowTestObserver<T> {

    private var _isFinite: Boolean? = null
    private var _delegate: FlowTestObserver<T>? = null

    override suspend fun isFinite(): Boolean {
        if (_isFinite == null) {
            _isFinite = assertIsFinite()
        }
        return _isFinite!!
    }

    private suspend fun delegate(): FlowTestObserver<T> {
        if (_delegate == null) {
            _delegate = if (isFinite()) {
                FiniteFlowObserver(flow)
            } else {
                InfiniteFlowObserver(flow, timeout)
            }
        }
        return _delegate!!
    }

    private suspend fun assertIsFinite(): Boolean {
        var isFinite = false
        try {
            withTimeout(timeout) {
                flow.toList()
                isFinite = true
            }
        } catch (ignored: IllegalStateException) {

        }
        return isFinite
    }

    override suspend fun values(): List<T> {
        return delegate().values()
    }

    override suspend fun valuesCount(): Int {
        return delegate().valuesCount()
    }

    override suspend fun assertValues(vararg values: T): FlowTestObserver<T> {
        delegate().assertValues(*values)
        return this
    }

    override suspend fun assertNoValues(): FlowTestObserver<T> {
        delegate().assertNoValues()
        return this
    }

    override suspend fun assertValueCount(count: Int): FlowTestObserver<T> {
        delegate().assertValueCount(count)
        return this
    }

    override suspend fun assertTerminated(): FlowTestObserver<T> {
        delegate().assertTerminated()
        return this
    }

    override suspend fun assertNotTerminated(): FlowTestObserver<T> {
        delegate().assertNotTerminated()
        return this
    }
}