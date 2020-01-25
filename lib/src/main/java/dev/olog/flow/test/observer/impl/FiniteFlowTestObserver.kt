package dev.olog.flow.test.observer.impl

import dev.olog.flow.test.observer.FlowTestObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.junit.Assert.fail

internal class FiniteFlowTestObserver<T>(
    flow: Flow<T>
) : BaseFlowTestObserver<T>(flow) {

    override suspend fun computeFlowValues(flow: Flow<T>): List<T> {
        return flow.toList()
    }

    override suspend fun isFinite(): Boolean {
        return true
    }

    override suspend fun assertIsFinite(): FlowTestObserver<T> {
        return this
    }

    override suspend fun assertIsNotFinite(): FlowTestObserver<T> {
        fail("Cold streams is always finite")
        return this
    }

    override suspend fun assertTerminated(): FlowTestObserver<T> {
        return this
    }

    override suspend fun assertNotTerminated(): FlowTestObserver<T> {
        fail("Cold streams always terminate")
        return this
    }

}