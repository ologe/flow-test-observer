package dev.olog.flow.test.observer.impl

import dev.olog.flow.test.observer.FlowTestObserver
import kotlinx.coroutines.flow.Flow
import org.junit.Assert.assertEquals
import java.util.Collections.unmodifiableList

internal abstract class BaseFlowObserver<T>(
    private val flow: Flow<T>
): FlowTestObserver<T> {

    private var _values: List<T>? = null

    suspend fun flowValues(): List<T> {
        if (_values == null) {
            _values = computeFlowValues(flow)
        }
        return _values!!
    }

    protected abstract suspend fun computeFlowValues(flow: Flow<T>): List<T>

    // region getters

    override suspend fun values(): List<T> {
        return unmodifiableList(flowValues())
    }

    override suspend fun valuesCount(): Int {
        return flowValues().size
    }

    // endregion

    // regions assertions

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