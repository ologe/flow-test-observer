package dev.olog.flow.test.observer

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ScopeTest {

    @Test
    fun testWithSameScopeShouldFail() = runTest(UnconfinedTestDispatcher()) {
        val flow = flow {
            emit(1)
        }

        flow.test(this) {
            assertValues(1)
        }
    }

    // https://github.com/ologe/flow-test-observer/issues/3
    @Test
    fun testWithDifferentScopesShouldFail() {
        val externalScope = TestScope(UnconfinedTestDispatcher())

        runTest(UnconfinedTestDispatcher()) {
            val flow = flow {
                emit(1)
            }

            flow.test(externalScope) {
                assertValues(1)
            }
        }
    }

}