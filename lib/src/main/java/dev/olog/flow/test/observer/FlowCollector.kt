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
fun <T> Flow<T>.test(): FlowTestCollector<T> {
    return FlowTestCollectorImpl(this)
}

internal class FlowTestCollectorImpl<T>(
    flow: Flow<T>
) : BaseTestFlowCollector<T>(flow) {

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

    override suspend fun assertComplete(): FlowTestCollector<T> {
        assertTrue("Not completed!", isCompleted())
        return this
    }

    override suspend fun assertNotComplete(): FlowTestCollector<T> {
        assertFalse("Completed!", isCompleted())
        return this
    }

    override suspend fun assertNoErrors(): FlowTestCollector<T> {
        when (val error = errorInternal()) {
            is Error.Wrapped -> fail("Error present=${error.throwable}")
        }
        return this
    }

    override suspend fun assertError(errorClass: Class<out Throwable>): FlowTestCollector<T> {
        assertError { it::class.java == errorClass }
        return this
    }

    override suspend fun assertError(errorPredicate: (Throwable) -> Boolean): FlowTestCollector<T> {
        val error = errorInternal()
        assertTrue("No errors found", error is Error.Wrapped)
        require(error is Error.Wrapped)
        assertTrue("Predicate doesn't match, actual=${error.throwable}", errorPredicate(error.throwable))
        return this
    }

    override suspend fun assertErrorMessage(message: String): FlowTestCollector<T> {
        val error = errorInternal()
        require(error is Error.Wrapped)
        assertEquals(message, error.throwable.message)
        return this
    }

    override suspend fun assertValue(value: T): FlowTestCollector<T> {
        if (flowValues().size != 1) {
            fail("Expected only 1 value, values=${flowValues()}")
        }
        val first = valueAt(0)
        assertEquals(value, first)
        return this
    }

    override suspend fun assertValue(predicate: (T) -> Boolean): FlowTestCollector<T> {
        if (flowValues().size != 1) {
            fail("Expected only 1 value, values=${flowValues()}")
        }

        val first = valueAt(0)
        assertTrue("Predicate doesn't match", predicate(first))
        return this
    }

    override suspend fun assertValueIsNull(): FlowTestCollector<T> {
        if (flowValues().size != 1) {
            fail("Expected only 1 value, values=${flowValues()}")
        }
        val first = valueAt(0)
        assertTrue("The item is not null=$first", first == null)
        return this
    }

    override suspend fun assertValueAt(index: Int, value: T): FlowTestCollector<T> {
        if (index > flowValues().lastIndex) {
            fail("Invalid index=$index")
        }

        assertEquals(value, valueAt(index))
        return this
    }

    override suspend fun assertValueAt(index: Int, predicate: (T) -> Boolean): FlowTestCollector<T> {
        if (index > flowValues().lastIndex) {
            fail("Invalid index=$index")
        }
        assertTrue("Predicate doesn't match", predicate(flowValues()[index]))
        return this
    }

    override suspend fun assertValues(vararg values: T): FlowTestCollector<T> {
        assertEquals(values.toList(), flowValues())
        return this
    }

    override suspend fun assertNoValues(): FlowTestCollector<T> {
        assertEquals(emptyList<T>(), flowValues())
        return this
    }

    override suspend fun assertValueCount(count: Int): FlowTestCollector<T> {
        assertEquals(count, flowValues().size)
        return this
    }


    // endregion

}