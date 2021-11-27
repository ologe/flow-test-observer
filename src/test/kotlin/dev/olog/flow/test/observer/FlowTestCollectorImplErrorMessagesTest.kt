package dev.olog.flow.test.observer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test

class FlowTestCollectorImplErrorMessagesTest {

    data class TestModel(
        val id: Long,
        val firstName: String,
        val lastName: String,
    ) {

        companion object {
            val item1 = TestModel(1, "firstName1", "lastName1")
            val item2 = TestModel(2, "firstName2", "lastName2")
            val item3 = TestModel(3, "firstName3", "lastName3")
        }

    }

    companion object {
        private var messages: MutableList<String>? = null

        @BeforeClass
        @JvmStatic
        fun setup() {
            messages = mutableListOf()
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            // sort then print all the messages to have an overview on how consistent are message
            for (s in messages?.sorted().orEmpty()) {
                println(s)
            }
            messages = null
        }
    }

    @Test
    fun `test valueAt, case no values`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf<TestModel>()

        flow.test(this) {
            try {
                valueAt(0)
            } catch (ex: Throwable) {
                ex.printAndAssert("No values (values = 0, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test valueAt, case out of bound lower`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1)

        flow.test(this) {
            try {
                valueAt(-1)
            } catch (ex: Throwable) {
                ex.printAndAssert("Index -1 is out of range [0, 1) (values = 1, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test valueAt, case out of bound upper`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1)

        flow.test(this) {
            try {
                valueAt(2)
            } catch (ex: Throwable) {
                ex.printAndAssert("Index 2 is out of range [0, 1) (values = 1, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertComplete message`() = runTest(UnconfinedTestDispatcher()) {
        val flow = MutableStateFlow(Unit)

        flow.test(this) {
            try {
                assertComplete()
            } catch (ex: Throwable) {
                ex.printAndAssert("Not completed! (values = 1, has error = false, completed = false)")
            }
        }
    }

    @Test
    fun `test assertNotComplete message`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1)

        flow.test(this) {
            try {
                assertNotComplete()
            } catch (ex: Throwable) {
                ex.printAndAssert("Completed! (values = 1, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertNoErrors message`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flow<Unit> {
            throw RuntimeException("message")
        }

        flow.test(this) {
            try {
                assertNoErrors()
            } catch (ex: Throwable) {
                ex.printAndAssert("Error present: java.lang.RuntimeException: message (values = 0, has error = true, completed = true)")
            }
        }
    }

    @Test
    fun `test assertError message, case no errors`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1)

        flow.test(this) {
            try {
                assertError { it is RuntimeException }
            } catch (ex: Throwable) {
                ex.printAndAssert("No errors (values = 1, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertError message, case wrong error`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flow<Unit> {
            throw IllegalArgumentException("message")
        }

        flow.test(this) {
            try {
                assertError { it is IndexOutOfBoundsException }
            } catch (ex: Throwable) {
                ex.printAndAssert("Predicate doesn't match, got: java.lang.IllegalArgumentException: message (values = 0, has error = true, completed = true)")
            }
        }
    }

    @Test
    fun `test assertError java class message`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flow<Unit> {
            throw java.lang.IllegalArgumentException("message")
        }

        flow.test(this) {
            try {
                assertError(IndexOutOfBoundsException::class.java)
            } catch (ex: Throwable) {
                ex.printAndAssert("Predicate doesn't match, got: java.lang.IllegalArgumentException: message (values = 0, has error = true, completed = true)")
            }
        }
    }

    @Test
    fun `test assertError kotlin class message`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flow<Unit> {
            throw IllegalArgumentException("message")
        }

        flow.test(this) {
            try {
                assertError(IndexOutOfBoundsException::class)
            } catch (ex: Throwable) {
                ex.printAndAssert("Predicate doesn't match, got: java.lang.IllegalArgumentException: message (values = 0, has error = true, completed = true)")
            }
        }
    }

    @Test
    fun `test assertErrorMessage message`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flow<Unit> {
            throw IllegalArgumentException("message")
        }

        flow.test(this) {
            try {
                assertErrorMessage("expected message")
            } catch (ex: Throwable) {
                ex.printAndAssert("\nexpected: expected message\ngot: message (values = 0, has error = true, completed = true)")
            }
        }
    }

    @Test
    fun `test assertValue message, case value + empty list`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf<TestModel>()

        flow.test(this) {
            try {
                assertValue(value = TestModel.item1)
            } catch (ex: Throwable) {
                ex.printAndAssert("\nexpected: TestModel(id=1, firstName=firstName1, lastName=lastName1) (class: TestModel)\ngot: [] (values = 0, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertValue message, case value + 2 items list`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1, TestModel.item2, TestModel.item3)

        flow.test(this) {
            try {
                assertValue(value = TestModel.item1)
            } catch (ex: Throwable) {
                ex.printAndAssert("\nexpected: TestModel(id=1, firstName=firstName1, lastName=lastName1) (class: TestModel)\ngot: [TestModel(id=1, firstName=firstName1, lastName=lastName1), TestModel(id=2, firstName=firstName2, lastName=lastName2), TestModel(id=3, firstName=firstName3, lastName=lastName3)] (values = 3, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertValue message, case predicate + empty list`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf<TestModel>()

        flow.test(this) {
            try {
                assertValue { it == TestModel.item1 }
            } catch (ex: Throwable) {
                ex.printAndAssert("No values (values = 0, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertValue message, case predicate + 2 items list`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1, TestModel.item2, TestModel.item3)

        flow.test(this) {
            try {
                assertValue { it == TestModel.item1 }
            } catch (ex: Throwable) {
                ex.printAndAssert("The first value passed the predicate but this consumer received more than one value (values = 3, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertValue message, case predicate doesn't match`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1, TestModel.item2, TestModel.item3)

        flow.test(this) {
            try {
                assertValue(value = TestModel.item2)
            } catch (ex: Throwable) {
                ex.printAndAssert("\nexpected: TestModel(id=2, firstName=firstName2, lastName=lastName2) (class: TestModel)\ngot: [TestModel(id=1, firstName=firstName1, lastName=lastName1), TestModel(id=2, firstName=firstName2, lastName=lastName2), TestModel(id=3, firstName=firstName3, lastName=lastName3)] (values = 3, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertValueIsNull message, case predicate + empty list`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf<TestModel>()

        flow.test(this) {
            try {
                assertValueIsNull()
            } catch (ex: Throwable) {
                ex.printAndAssert("\nexpected exactly one item\ngot :0; Value count differs (values = 0, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertValueIsNull message, case predicate + 2 items list`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1, TestModel.item2)

        flow.test(this) {
            try {
                assertValueIsNull()
            } catch (ex: Throwable) {
                ex.printAndAssert("\nexpected exactly one item\ngot :2; Value count differs (values = 2, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertValueAt message`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1)

        flow.test(this) {
            try {
                assertValueAt(0, TestModel.item2)
            } catch (ex: Throwable) {
                ex.printAndAssert("\nexpected: TestModel(id=2, firstName=firstName2, lastName=lastName2) (class: TestModel)\ngot: TestModel(id=1, firstName=firstName1, lastName=lastName1) (class: TestModel); Value at position 0 differ (values = 1, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertValueAt predicate message`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1)

        flow.test(this) {
            try {
                assertValueAt(0) { it == TestModel.item2 }
            } catch (ex: Throwable) {
                ex.printAndAssert("Value TestModel(id=1, firstName=firstName1, lastName=lastName1) (class: TestModel) at position 0 did not pass the predicate (values = 1, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test vararg assertValues message`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1)

        flow.test(this) {
            try {
                assertValues(TestModel.item2)
            } catch (ex: Throwable) {
                ex.printAndAssert("\nexpected: TestModel(id=2, firstName=firstName2, lastName=lastName2) (class: TestModel)\ngot: TestModel(id=1, firstName=firstName1, lastName=lastName1) (class: TestModel); Value at position 0 differ (values = 1, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test iterable assertValues message, less values`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1, TestModel.item2)

        flow.test(this) {
            try {
                assertValues(listOf(TestModel.item1))
            } catch (ex: Throwable) {
                ex.printAndAssert("More values received than expected (1) (values = 2, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test iterable assertValues message, more values`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1)

        flow.test(this) {
            try {
                assertValues(listOf(TestModel.item1, TestModel.item2))
            } catch (ex: Throwable) {
                ex.printAndAssert("Fewer values received than expected (1) (values = 1, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test sequence assertValues message, less values`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1, TestModel.item2)

        flow.test(this) {
            try {
                assertValues(sequenceOf(TestModel.item1))
            } catch (ex: Throwable) {
                ex.printAndAssert("More values received than expected (1) (values = 2, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test sequence assertValues message, more values`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1)

        flow.test(this) {
            try {
                assertValues(sequenceOf(TestModel.item1, TestModel.item2))
            } catch (ex: Throwable) {
                ex.printAndAssert("Fewer values received than expected (1) (values = 1, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertNoValues message`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1)

        flow.test(this) {
            try {
                assertNoValues()
            } catch (ex: Throwable) {
                ex.printAndAssert("\nexpected: 0\ngot: 1; Value counts differ (values = 1, has error = false, completed = true)")
            }
        }
    }

    @Test
    fun `test assertValueCount message`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flowOf(TestModel.item1, TestModel.item2, TestModel.item3)

        flow.test(this) {
            try {
                assertValueCount(2)
            } catch (ex: Throwable) {
                ex.printAndAssert("\nexpected: 2\ngot: 3; Value counts differ (values = 3, has error = false, completed = true)")
            }
        }
    }

    private fun Throwable.printAndAssert(expectedMessage: String) {
        messages?.add(expectedMessage)
        assertEquals(expectedMessage, message)
    }

}