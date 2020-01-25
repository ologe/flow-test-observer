package dev.olog.flow.test.observer

import dev.olog.flow.test.observer.impl.FiniteFlowTestObserver
import dev.olog.flow.test.observer.impl.InfiniteFlowTestObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout

/**
 * Allows hot and cold [Flow] streams testing.
 * [hot vs cold](https://medium.com/@luukgruijs/understanding-hot-vs-cold-observables-62d04cf92e03)
 *
 * Usage example (using a cold flow here for simplicity):
 * ```
 * val flow = callbackFlow {
 *      offer(1)
 *      offer(2)
 *      awaitClose()
 *  }
 *
 * flow.test()
 *      .assertValues(1, 2, 3)
 *      .assertValueCount(3)
 * ```
 */
fun <T> Flow<T>.test(): FlowTestObserver<T> {
    return FlowTestObserverImpl(this)
}

internal class FlowTestObserverImpl<T>(
    private val flow: Flow<T>
) : FlowTestObserver<T> {

    private var _isFinite: Boolean? = null
    private var _delegate: FlowTestObserver<T>? = null

    private suspend fun checkIsFinite(): Boolean {
        if (_isFinite == null) {
            _isFinite = checkIsFiniteImpl()
        }
        return _isFinite!!
    }

    private suspend fun checkIsFiniteImpl(): Boolean {
        var isFinite = false
        try {
            withTimeout(Long.MAX_VALUE) { // workaround to skip all delays
                flow.toList()
                isFinite = true
            }
        } catch (ignored: IllegalStateException) {

        }
        return isFinite
    }

    private suspend fun delegate(): FlowTestObserver<T> {
        if (_delegate == null) {
            _delegate = if (checkIsFinite()) {
                FiniteFlowTestObserver(flow)
            } else {
                InfiniteFlowTestObserver(flow)
            }
        }
        return _delegate!!
    }

    // region getters

    override suspend fun isFinite(): Boolean {
        return delegate().isFinite()
    }

    override suspend fun values(): List<T> {
        return delegate().values()
    }

    override suspend fun valuesCount(): Int {
        return delegate().valuesCount()
    }

    // endregion

    // region assertions

    override suspend fun assertIsFinite(): FlowTestObserver<T> {
        return delegate().assertIsFinite()
    }

    override suspend fun assertIsNotFinite(): FlowTestObserver<T> {
        return delegate().assertIsNotFinite()
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

    // endregion

}