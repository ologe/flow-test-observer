package dev.olog.flow.test.observer.impl

import dev.olog.flow.test.observer.FlowTestObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.junit.Assert

internal class FiniteFlowObserver<T>(
    private val flow: Flow<T>
) : FlowTestObserver<T> {

    override suspend fun assertValues(vararg expected: T) {
        Assert.assertEquals(expected.toList(), flow.toList())
    }
}