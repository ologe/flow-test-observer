package dev.olog.flow.test.observer.impl

import dev.olog.flow.test.observer.FlowTestObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import org.junit.Assert
import org.junit.Assert.assertEquals
import java.util.*

internal class InfiniteFlowObserver<T>(
    private val flow: Flow<T>,
    private val timeout: Long
) : FlowTestObserver<T> {

    private var _values: List<T>? = null

    private suspend fun flowValues(): List<T> {
        if (_values == null) {
            _values = computeFlowValues()
        }
        return _values!!
    }

    private suspend fun computeFlowValues(): List<T> {
        val result = mutableListOf<T>()
        try {
            var index = 0
            while (true) {
                withTimeout(timeout) {
                    result.add(flow.drop(index).first())
                }
                index++
            }
        } catch (ex: IllegalStateException) {

        }
        return result
    }

    override suspend fun isFinite(): Boolean {
        throw NotImplementedError()
    }

    override suspend fun values(): List<T> {
        return Collections.unmodifiableList(flowValues())
    }

    override suspend fun valuesCount(): Int {
        return flowValues().size
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
        Assert.fail("infinite stream cannot terminate")
        return this
    }

    override suspend fun assertNotTerminated(): FlowTestObserver<T> {
        return this
    }
}