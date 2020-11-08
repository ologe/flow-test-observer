package dev.olog.flow.test.observer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import java.lang.RuntimeException

class FlowTestCollectorImplErrorMessagesTest {

    companion object {
        private var test: MutableList<String>? = null

        @BeforeClass
        @JvmStatic
        fun setup() {
            test = mutableListOf()
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            // sort then print all the messages to have an overview on how consistent are message
            for (s in test?.sorted().orEmpty()) {
                println(s)
            }
            test = null
        }
    }

    @Test
    fun `test valueAt, case out of bound lower`() = runBlockingTest {
        val flow = flowOf(0)

        flow.test(this) {
            try {
                valueAt(-1)
            } catch (ex: Throwable) {
                ex.printAndAssert("IndexOutOfBoundsException: cannot access index [-1], list has [1] items")
            }
        }
    }

    @Test
    fun `test valueAt, case out of bound upper`() = runBlockingTest {
        val flow = flowOf(0)

        flow.test(this) {
            try {
                valueAt(1)
            } catch (ex: Throwable) {
                ex.printAndAssert("IndexOutOfBoundsException: cannot access index [1], list has [1] items")
            }
        }
    }

    @Test
    fun `test assertComplete message`() = runBlockingTest {
        val flow = MutableStateFlow(Unit)

        flow.test(this) {
            try {
                assertComplete()
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected [Complete], but was [Not Complete]")
            }
        }
    }

    @Test
    fun `test assertNotComplete message`() = runBlockingTest {
        val flow = flowOf(1)

        flow.test(this) {
            try {
                assertNotComplete()
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected [Not Complete], but was [Complete]")
            }
        }
    }

    @Test
    fun `test assertNoErrors message`() = runBlockingTest {
        val flow = flow<Unit> {
            throw RuntimeException("message")
        }

        flow.test(this) {
            try {
                assertNoErrors()
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected [No Errors], but failed with [java.lang.RuntimeException: message]")
            }
        }
    }

    @Test
    fun `test assertError message, case no errors`() = runBlockingTest {
        val flow = flowOf(1)

        flow.test(this) {
            try {
                assertError { it is RuntimeException }
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected [Exception], but no Exception was thrown")
            }
        }
    }

    @Test
    fun `test assertError message, case wrong error`() = runBlockingTest {
        val flow = flow<Unit> {
            throw IllegalArgumentException("message")
        }

        flow.test(this) {
            try {
                assertError { it is IndexOutOfBoundsException }
            } catch (ex: Throwable) {
                ex.printAndAssert("Predicate doesn't match, error [java.lang.IllegalArgumentException: message]")
            }
        }
    }

    @Test
    fun `test assertError java class message`() = runBlockingTest {
        val flow = flow<Unit> {
            throw IllegalArgumentException("message")
        }

        flow.test(this) {
            try {
                assertError(IndexOutOfBoundsException::class.java)
            } catch (ex: Throwable) {
                ex.printAndAssert("Predicate doesn't match, error [java.lang.IllegalArgumentException: message]")
            }
        }
    }

    @Test
    fun `test assertError kotlin class message`() = runBlockingTest {
        val flow = flow<Unit> {
            throw IllegalArgumentException("message")
        }

        flow.test(this) {
            try {
                assertError(IndexOutOfBoundsException::class)
            } catch (ex: Throwable) {
                ex.printAndAssert("Predicate doesn't match, error [java.lang.IllegalArgumentException: message]")
            }
        }
    }

    @Test
    fun `test assertErrorMessage message`() = runBlockingTest {
        val flow = flow<Unit> {
            throw IllegalArgumentException("message")
        }

        flow.test(this) {
            try {
                assertErrorMessage("expected message")
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected error message [expected message], but was [message]")
            }
        }
    }

    @Test
    fun `test assertValue message, case value + empty list`() = runBlockingTest {
        val flow = flowOf<Int>()

        flow.test(this) {
            try {
                assertValue(value = 1)
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected exactly 1 value [1], but values were []")
            }
        }
    }

    @Test
    fun `test assertValue message, case value + 2 items list`() = runBlockingTest {
        val flow = flowOf(1, 2)

        flow.test(this) {
            try {
                assertValue(value = 1)
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected exactly 1 value [1], but values were [1, 2]")
            }
        }
    }

    @Test
    fun `test assertValue message, case predicate + empty list`() = runBlockingTest {
        val flow = flowOf<Int>()

        flow.test(this) {
            try {
                assertValue { it == 1 }
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected exactly 1 value, but values were []")
            }
        }
    }

    @Test
    fun `test assertValue message, case predicate + 2 items list`() = runBlockingTest {
        val flow = flowOf(1, 2)

        flow.test(this) {
            try {
                assertValue { it == 1 }
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected exactly 1 value, but values were [1, 2]")
            }
        }
    }

    @Test
    fun `test assertValue message, case predicate doesn't match`() = runBlockingTest {
        val flow = flowOf(1)

        flow.test(this) {
            try {
                assertValue(value = 2)
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected [2], but was [1]")
            }
        }
    }

    @Test
    fun `test assertValueIsNull message, case predicate + empty list`() = runBlockingTest {
        val flow = flowOf<Int>()

        flow.test(this) {
            try {
                assertValueIsNull()
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected exactly 1 value [null], but values were []")
            }
        }
    }

    @Test
    fun `test assertValueIsNull message, case predicate + 2 items list`() = runBlockingTest {
        val flow = flowOf(1, 2)

        flow.test(this) {
            try {
                assertValueIsNull()
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected exactly 1 value [null], but values were [1, 2]")
            }
        }
    }

    @Test
    fun `test assertValueIsNull message, case predicate doesn't match`() = runBlockingTest {
        val flow = flowOf(1)

        flow.test(this) {
            try {
                assertValueIsNull()
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected [null], but was [1]")
            }
        }
    }

    @Test
    fun `test assertValueAt message`() = runBlockingTest {
        val flow = flowOf("item")

        flow.test(this) {
            try {
                assertValueAt(0, "another item")
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected [another item], but was [item]")
            }
        }
    }

    @Test
    fun `test assertValueAt predicate message`() = runBlockingTest {
        val flow = flowOf("item")

        flow.test(this) {
            try {
                assertValueAt(0) { it == "another item" }
            } catch (ex: Throwable) {
                ex.printAndAssert("Predicate doesn't match for [item]")
            }
        }
    }

    @Test
    fun `test assertValues message`() = runBlockingTest {
        val flow = flowOf("item")

        flow.test(this) {
            try {
                assertValues("another item")
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected [another item], but was [item]")
            }
        }
    }

    @Test
    fun `test assertNoValues message`() = runBlockingTest {
        val flow = flowOf("item")

        flow.test(this) {
            try {
                assertNoValues()
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected no values, but was [item]")
            }
        }
    }

    @Test
    fun `test assertValueCount message`() = runBlockingTest {
        val flow = flowOf("item")

        flow.test(this) {
            try {
                assertValueCount(2)
            } catch (ex: Throwable) {
                ex.printAndAssert("Expected [2] items, but was [1]")
            }
        }
    }

    private fun Throwable.printAndAssert(expectedMessage: String) {
        test?.add(expectedMessage)
        assertEquals(expectedMessage, message)
    }

}