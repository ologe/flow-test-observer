package dev.olog.flow.test.observer

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.lang.AssertionError

class ScopeTest {

    @Test(expected = AssertionError::class)
    fun testWithSameScopeShouldFail() = runBlockingTest {
        val flow = flow {
            emit(1)
        }

        flow.test(this) {
            assertValues(2)
        }
    }

    // https://github.com/ologe/flow-test-observer/issues/3
    @Test(expected = AssertionError::class)
    fun testWithDifferentScopesShouldFail() {
        val externalScope = TestCoroutineScope()

        runBlockingTest {
            val flow = flow {
                emit(1)
            }

            flow.test(externalScope) {
                assertValues(2)
            }
        }
    }

}