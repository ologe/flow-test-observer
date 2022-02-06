package dev.olog.flow.test.observer

import kotlinx.coroutines.flow.Flow
import java.util.Collections
import java.util.concurrent.CancellationException
import kotlin.reflect.KClass

internal class FlowTestCollectorImpl<T>(
    flow: Flow<T>
) : BaseTestFlowCollector<T>(flow) {

    // region getters

    override suspend fun isCompleted(): Boolean {
        return hasCompletedInternal()
    }

    override suspend fun valueAt(index: Int): T {
        if (valuesCount() == 0) {
            throw fail("No values")
        }
        if (index < 0 || index >= valuesCount()) {
            throw fail("Index " + index + " is out of range [0, " + valuesCount() + ")");
        }
        return values()[index]
    }

    override suspend fun values(): List<T> {
        return Collections.unmodifiableList(flowValues())
    }

    override suspend fun valuesCount(): Int = flowValues().size

    override suspend fun error(): Throwable? {
        return when (val error = errorInternal()) {
            is Error.Wrapped -> error.throwable
            is Error.Empty -> null
        }
    }

    // endregion

    // region assertions

    override suspend fun assertComplete(): FlowTestCollector<T> {
        if (!isCompleted()) {
            throw fail("Not completed!")
        }
        return this
    }

    override suspend fun assertNotComplete(): FlowTestCollector<T> {
        if (isCompleted()) {
            throw fail("Completed!")
        }
        return this
    }

    override suspend fun assertNoErrors(): FlowTestCollector<T> {
        val error = errorInternal()
        if (error is Error.Wrapped) {
            throw fail("Error present: ${error.throwable}")
        }
        return this
    }

    override suspend fun assertError(javaClass: Class<out Throwable>): FlowTestCollector<T> {
        assertError { it::class.java == javaClass }
        return this
    }

    override suspend fun assertError(kotlinClass: KClass<out Throwable>): FlowTestCollector<T> {
        assertError { it::class.java == kotlinClass.java }
        return this
    }

    override suspend fun assertError(predicate: (Throwable) -> Boolean): FlowTestCollector<T> {
        when (val error = errorInternal()) {
            is Error.Empty -> throw fail("No errors")
            is Error.Wrapped -> {
                if (!predicate(error.throwable)) {
                    throw fail("Predicate doesn't match, got: ${error.throwable}")
                }
            }
        }
        return this
    }

    override suspend fun assertErrorMessage(message: String): FlowTestCollector<T> {
        when (val error = errorInternal()) {
            is Error.Empty -> throw fail("No errors")
            is Error.Wrapped -> {
                if (error.throwable.message != message) {
                    throw fail("\nexpected: $message\ngot: ${error.throwable.message}")
                }
            }
        }
        return this
    }

    override suspend fun assertValue(value: T): FlowTestCollector<T> {
        if (valuesCount() != 1) {
            throw fail("\nexpected: ${valueAndClass(value)}\ngot: ${values()}")
        }
        val first = values().first()
        if (value != first) {
            throw fail("\nexpected: ${valueAndClass(value)}\ngot: ${valueAndClass(first)}")
        }
        return this
    }

    override suspend fun assertValue(predicate: (T) -> Boolean): FlowTestCollector<T> {
        assertValueAt(0, predicate)
        if (valuesCount() > 1) {
            throw fail("The first value passed the predicate but this consumer received more than one value");
        }
        return this
    }

    override suspend fun assertValueIsNull(): FlowTestCollector<T> {
        if (valuesCount() != 1) {
            throw fail("\nexpected exactly one item\ngot :${valuesCount()}; Value count differs")
        }
        val first = values().first()
        if (first != null) {
            throw fail("\nexpected: ${valueAndClass(null)}\ngot: ${valueAndClass(first)}")
        }

        return this
    }

    override suspend fun assertValueAt(index: Int, value: T): FlowTestCollector<T> {
        if (valuesCount() == 0) {
            throw fail("No values")
        }
        if (index < 0 || index >= valuesCount()) {
            throw fail("Index $index is out of range [0, ${valuesCount()})");
        }
        val v = values()[index]
        if (value != v) {
            throw fail("\nexpected: ${valueAndClass(value)}\ngot: ${valueAndClass(v)}; Value at position $index differ")
        }
        return this
    }

    override suspend fun assertValueAt(index: Int, predicate: (T) -> Boolean): FlowTestCollector<T> {
        if (valuesCount() == 0) {
            throw fail("No values")
        }
        if (index < 0 || index >= valuesCount()) {
            throw fail("Index $index is out of range [0, ${valuesCount()})");
        }
        val v = values()[index]
        if (!predicate(v)) {
            throw fail("Value ${valueAndClass(v)} at position $index did not pass the predicate")
        }
        return this
    }

    override suspend fun assertValues(vararg values: T): FlowTestCollector<T> {
        if (valuesCount() != values.size) {
            throw fail("\nexpected: ${values.size} ${values.toList()}\ngot: ${valuesCount()} ${values()}; Value count differs")
        }
        for (i in values().indices) {
            val actual = values()[i]
            val expected = values[i]
            if (actual != expected) {
                throw fail("\nexpected: ${valueAndClass(expected)}\ngot: ${valueAndClass(actual)}; Value at position $i differ")
            }
        }

        return this
    }

    override suspend fun assertValues(values: Iterable<T>): FlowTestCollector<T> {
        return assertValues(
            actualIterator = values().iterator(),
            expectedIterator = values.iterator()
        )
    }

    override suspend fun assertValues(values: Sequence<T>): FlowTestCollector<T> {
        return assertValues(
            actualIterator = values().iterator(),
            expectedIterator = values.iterator()
        )
    }

    private suspend fun assertValues(actualIterator: Iterator<T>, expectedIterator: Iterator<T>): FlowTestCollector<T> {
        var i = 0

        var hasExpectedNext: Boolean
        var hasActualNext: Boolean

        while (true) {
            hasActualNext = actualIterator.hasNext()
            hasExpectedNext = expectedIterator.hasNext()

            if (!hasActualNext || !hasExpectedNext) {
                break
            }
            val actual = actualIterator.next()
            val expected = expectedIterator.next()
            if (actual != expected) {
                throw fail("\nexpected: ${valueAndClass(expected)}\ngot: ${valueAndClass(actual)}; Value at position $i differ")
            }
            i++
        }

        if (hasActualNext) {
            throw fail("More values received than expected ($i)")
        }
        if (hasExpectedNext) {
            throw fail("Fewer values received than expected ($i)")
        }

        return this
    }

    override suspend fun assertNoValues(): FlowTestCollector<T> {
        return assertValueCount(0)
    }

    override suspend fun assertValueCount(count: Int): FlowTestCollector<T> {
        if (valuesCount() != count) {
            throw fail("\nexpected: $count\ngot: ${valuesCount()}; Value counts differ");
        }
        return this
    }


    // endregion

    private suspend fun fail(message: String): AssertionError {
        val error = error()
        val string = buildString {
            append(message)
            append(" (values = ${values().size}, has error = ${error != null}, completed = ${isCompleted()}")
            if (error is CancellationException) {
                append(", disposed!")
            }
            append(")")
        }

        val assertion = AssertionError(string)
        if (error != null) {
            assertion.initCause(error)
        }

        return assertion
    }

    private fun valueAndClass(o: Any?): String {
        return if (o != null) {
            o.toString() + " (class: " + o.javaClass.simpleName + ")"
        } else "null"
    }

}