package dev.olog.flow.test.observer

import kotlinx.coroutines.flow.Flow
import org.junit.Assert.*
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
    flow: Flow<T>
) : BaseTestFlowObserver<T>(flow) {

    // region getters

    override suspend fun isCompleted(): Boolean {
        return hasCompletedInternal()
    }

    override suspend fun valueAt(index: Int): T {
        if (index >= valuesCount()) {
            throw IllegalArgumentException("Index out of bound=$index, size=${valuesCount()}")
        }
        return flowValues()[index]
    }

    override suspend fun values(): List<T> {
        return unmodifiableList(flowValues())
    }

    override suspend fun valuesCount(): Int {
        return flowValues().size
    }

    override suspend fun error(): Throwable? {
        return when (val error = errorInternal()) {
            is Error.Wrapped -> error.throwable
            is Error.Empty -> null
        }
    }

    // endregion

    // region assertions

    override suspend fun assertComplete(): FlowTestObserver<T> {
        assertTrue("Not completed!", isCompleted())
        return this
    }

    override suspend fun assertNotComplete(): FlowTestObserver<T> {
        assertFalse("Completed!", isCompleted())
        return this
    }

    override suspend fun assertNoErrors(): FlowTestObserver<T> {
        assertTrue(errorInternal() is Error.Empty)
        return this
    }

    override suspend fun assertError(errorClass: Class<out Throwable>): FlowTestObserver<T> {
        assertError { it::class.java == errorClass }
        return this
    }

    override suspend fun assertError(errorPredicate: (Throwable) -> Boolean): FlowTestObserver<T> {
        val error = errorInternal()
        assertTrue("No errors found", error is Error.Wrapped)
        require(error is Error.Wrapped)
        assertTrue("Predicate doesn't match", errorPredicate(error.throwable))
        return this
    }

    override suspend fun assertErrorMessage(message: String): FlowTestObserver<T> {
        val error = errorInternal()
        require(error is Error.Wrapped)
        assertEquals(message, error.throwable.message)
        return this
    }

    override suspend fun assertValue(value: T): FlowTestObserver<T> {
        if (flowValues().size != 1) {
            fail("Expected only 1 value")
        }
        val first = valueAt(0)
        assertEquals(value, first)
        return this
    }

    override suspend fun assertValue(predicate: (T) -> Boolean): FlowTestObserver<T> {
        if (flowValues().size != 1) {
            fail("Expected only 1 value")
        }

        val first = valueAt(0)
        assertTrue("Predicate doesn't match", predicate(first))
        return this
    }

    override suspend fun assertValueIsNull(): FlowTestObserver<T> {
        if (flowValues().size != 1) {
            fail("Expected only 1 value")
        }
        val first = valueAt(0)
        assertTrue("The item is not null=$first", first == null)
        return this
    }

    override suspend fun assertValueAt(index: Int, value: T): FlowTestObserver<T> {
        if (index > flowValues().lastIndex) {
            fail("Invalid index=$index")
        }

        assertEquals(value, valueAt(index))
        return this
    }

    override suspend fun assertValueAt(index: Int, predicate: (T) -> Boolean): FlowTestObserver<T> {
        if (index > flowValues().lastIndex) {
            fail("Invalid index=$index")
        }
        assertTrue("Predicate doesn't match", predicate(flowValues()[index]))
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


    // endregion

}