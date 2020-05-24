package dev.olog.flow.test.observer

import dev.olog.flow.test.observer.utils.stateFlowBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class StateFlowTest {

    @Test
    fun testSingleValue() = runBlockingTest {
        val flow = stateFlowBuilder(1) {}

        flow.test(this) {
            assertValue(1)
            assertNotComplete()
        }
    }

    @Test
    fun testMultipleValuesFastEmissionShouldEmitOnlyLast() = runBlockingTest {
        val flow = stateFlowBuilder(1) {
            value = 2
            value = 3
        }

        flow.test(this) {
            assertValues(3)
            assertNotComplete()
        }
    }

    @Test
    fun testMultipleValuesShouldKeepAllValues() = runBlockingTest {
        val flow = stateFlowBuilder(1) {
            delay(100)
            value = 2
            delay(100)
            value = 3
        }

        flow.test(this) {
            assertValues(1, 2, 3)
            assertNotComplete()
        }
    }

}