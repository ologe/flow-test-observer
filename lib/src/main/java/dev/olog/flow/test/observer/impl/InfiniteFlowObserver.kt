package dev.olog.flow.test.observer.impl

import dev.olog.flow.test.observer.FlowTestObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import org.junit.Assert.fail

internal class InfiniteFlowObserver<T>(
    flow: Flow<T>,
    private val timeout: Long
) : BaseFlowObserver<T>(flow) {

    override suspend fun computeFlowValues(flow: Flow<T>): List<T> {
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
        return false
    }

    override suspend fun assertIsFinite(): FlowTestObserver<T> {
        fail("Hot streams is always infinite")
        return this
    }

    override suspend fun assertIsNotFinite(): FlowTestObserver<T> {
        return this
    }

    override suspend fun assertTerminated(): FlowTestObserver<T> {
        fail("Hot stream cannot terminate")
        return this
    }

    override suspend fun assertNotTerminated(): FlowTestObserver<T> {
        return this
    }

}