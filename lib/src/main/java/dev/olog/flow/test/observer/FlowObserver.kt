package dev.olog.flow.test.observer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import java.util.Collections.unmodifiableList

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
    private var _flowValues: List<T>? = null

    private suspend fun setup() {
        if (_isFinite == null || _flowValues == null) {
            val values = mutableListOf<T>()
            try {
                withTimeout(Long.MAX_VALUE) {
                    flow.collect { values.add(it) }
                }
                _isFinite = true
            } catch (ex: IllegalStateException) {
                _isFinite = false
            }
            _flowValues = values
        }
    }

    private suspend fun isFiniteInternal(): Boolean {
        setup()
        return _isFinite!!
    }

    private suspend fun flowValues(): List<T> {
        setup()
        return _flowValues!!
    }


    // region getters

    override suspend fun isFinite(): Boolean {
        return isFiniteInternal()
    }

    override suspend fun values(): List<T> {
        return unmodifiableList(flowValues())
    }

    override suspend fun valuesCount(): Int {
        return flowValues().size
    }

    // endregion

    // region assertions

    override suspend fun assertIsFinite(): FlowTestObserver<T> {
        if (!isFinite()) {
            fail("Hot streams is always infinite")
        }
        return this
    }

    override suspend fun assertIsNotFinite(): FlowTestObserver<T> {
        if (isFinite()) {
            fail("Cold streams is always finite")
        }
        return this
    }

    override suspend fun assertValues(vararg values: T): FlowTestObserver<T> {
        assertEquals(values.toList(), flowValues())
        return this
    }

    override suspend fun assertNoValues(): FlowTestObserver<T> {
        assertEquals(emptyList<T>(), flowValues())
        return this
    }

    override suspend fun assertValueCount(count: Int): FlowTestObserver<T> {
        assertEquals(count, flowValues().size)
        return this
    }

    override suspend fun assertTerminated(): FlowTestObserver<T> {
        if (!isFinite()) {
            fail("Hot stream cannot terminate")
        }
        return this
    }

    override suspend fun assertNotTerminated(): FlowTestObserver<T> {
        if (isFinite()) {
            fail("Cold streams always terminate")
        }
        return this
    }

    // endregion

}