package dev.olog.flow.test.observer

import kotlinx.coroutines.flow.Flow
import org.junit.Assert
import java.util.*

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
        return Collections.unmodifiableList(flowValues())
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
        Assert.assertTrue("Not completed!", isCompleted())
        return this
    }

    override suspend fun assertNotComplete(): FlowTestCollector<T> {
        Assert.assertFalse("Completed!", isCompleted())
        return this
    }

    override suspend fun assertNoErrors(): FlowTestCollector<T> {
        when (val error = errorInternal()) {
            is Error.Wrapped -> Assert.fail(
                "Error present=${error.throwable}"
            )
        }
        return this
    }

    override suspend fun assertError(errorClass: Class<out Throwable>): FlowTestCollector<T> {
        assertError { it::class.java == errorClass }
        return this
    }

    override suspend fun assertError(errorPredicate: (Throwable) -> Boolean): FlowTestCollector<T> {
        val error = errorInternal()
        Assert.assertTrue(
            "No errors found",
            error is Error.Wrapped
        )
        require(error is Error.Wrapped)
        Assert.assertTrue(
            "Predicate doesn't match, actual=${error.throwable}",
            errorPredicate(error.throwable)
        )
        return this
    }

    override suspend fun assertErrorMessage(message: String): FlowTestCollector<T> {
        val error = errorInternal()
        require(error is Error.Wrapped)
        Assert.assertEquals(message, error.throwable.message)
        return this
    }

    override suspend fun assertValue(value: T): FlowTestCollector<T> {
        if (flowValues().size != 1) {
            Assert.fail("Expected only 1 value, values=${flowValues()}")
        }
        val first = valueAt(0)
        Assert.assertEquals(value, first)
        return this
    }

    override suspend fun assertValue(predicate: (T) -> Boolean): FlowTestCollector<T> {
        if (flowValues().size != 1) {
            Assert.fail("Expected only 1 value, values=${flowValues()}")
        }

        val first = valueAt(0)
        Assert.assertTrue("Predicate doesn't match", predicate(first))
        return this
    }

    override suspend fun assertValueIsNull(): FlowTestCollector<T> {
        if (flowValues().size != 1) {
            Assert.fail("Expected only 1 value, values=${flowValues()}")
        }
        val first = valueAt(0)
        Assert.assertTrue("The item is not null=$first", first == null)
        return this
    }

    override suspend fun assertValueAt(index: Int, value: T): FlowTestCollector<T> {
        if (index > flowValues().lastIndex) {
            Assert.fail("Invalid index=$index")
        }

        Assert.assertEquals(value, valueAt(index))
        return this
    }

    override suspend fun assertValueAt(index: Int, predicate: (T) -> Boolean): FlowTestCollector<T> {
        if (index > flowValues().lastIndex) {
            Assert.fail("Invalid index=$index")
        }
        Assert.assertTrue("Predicate doesn't match", predicate(flowValues()[index]))
        return this
    }

    override suspend fun assertValues(vararg values: T): FlowTestCollector<T> {
        Assert.assertEquals(values.toList(), flowValues())
        return this
    }

    override suspend fun assertNoValues(): FlowTestCollector<T> {
        Assert.assertEquals(emptyList<T>(), flowValues())
        return this
    }

    override suspend fun assertValueCount(count: Int): FlowTestCollector<T> {
        Assert.assertEquals(count, flowValues().size)
        return this
    }


    // endregion

}