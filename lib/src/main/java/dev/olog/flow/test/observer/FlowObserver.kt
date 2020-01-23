package dev.olog.flow.test.observer

import dev.olog.flow.test.observer.impl.FiniteFlowObserver
import dev.olog.flow.test.observer.impl.InfiniteFlowObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout

fun <T> Flow<T>.test(timeout: Long = 50): FlowTestObserver<T> {
    return FlowTestObserverImpl(this, timeout)
}


internal class FlowTestObserverImpl<T>(
    private val flow: Flow<T>,
    private val timeout: Long
) : FlowTestObserver<T> {

    private var _isFinite: Boolean? = null
    private var _delegate: FlowTestObserver<T>? = null

    private suspend fun isFinite(): Boolean {
        if (_isFinite == null) {
            _isFinite = assertIsFinite()
        }
        return _isFinite!!
    }

    private suspend fun delegate(): FlowTestObserver<T> {
        if (_delegate == null) {
            if (isFinite()) {
                _delegate =
                    FiniteFlowObserver(flow)
            } else {
                _delegate =
                    InfiniteFlowObserver(flow)
            }
        }
        return _delegate!!
    }

    private suspend fun assertIsFinite(): Boolean {
        try {
            withTimeout(timeout) {
                flow.toList()
                return@withTimeout true
            }
        } catch (ignored: IllegalStateException) {

        }
        return false
    }

    override suspend fun assertValues(vararg expected: T) {
        delegate().assertValues(*expected)
    }

}