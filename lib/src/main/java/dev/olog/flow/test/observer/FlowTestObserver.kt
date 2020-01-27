package dev.olog.flow.test.observer

/**
 * Computes eagerly flow values to allow testing, works with both cold/finite and hot/infinite streams.
 */
interface FlowTestObserver<T> {

    // region getters

    /**
     * @return true if the given flow is completed or cancelled.
     */
    suspend fun isCompleted(): Boolean

    /**
     * @return the value at the given index
     */
    suspend fun valueAt(index: Int): T

    /**
     * @return all the values emitted from the given flow.
     */
    suspend fun values(): List<T>

    /**
     * @return the number of values emitted from given flow
     */
    suspend fun valuesCount(): Int

    /**
     * @return the error, if any
     */
    suspend fun error(): Throwable?

    // endregion

    // region assertions

    /**
     * Assert that the given flow is completed or cancelled
     * @return this
     */
    suspend fun assertComplete(): FlowTestObserver<T>

    /**
     * Assert that the given flow is not completed or cancelled
     * @return this
     */
    suspend fun assertNotComplete(): FlowTestObserver<T>

    /**
     * Assert that the given flow has not received any errors.
     * @return this
     */
    suspend fun assertNoErrors(): FlowTestObserver<T>

    /**
     * Asserts that the given flow received an error which is an
     * instance of the specified errorClass class.
     * @return this
     */
    suspend fun assertError(errorClass: Class<out Throwable>): FlowTestObserver<T>

    /**
     * Asserts that the given flow received an error for which
     * the provided predicate returns true.
     * @return this
     */
    suspend fun assertError(errorPredicate: (Throwable) -> Boolean): FlowTestObserver<T>

    /**
     * Assert that the given flow received exactly one value.
     * @return this
     */
    suspend fun assertValue(value: T): FlowTestObserver<T>

    /**
     * Assert that the given flow received exactly one value for which
     * the provided predicate returns true.
     * @return this
     */
    suspend fun assertValue(predicate: (T) -> Boolean): FlowTestObserver<T>

    /**
     * Asserts that the given flow received exactly one value at the given index
     * which is equal to the given value.
     * @return this
     */
    suspend fun assertValueAt(index: Int, value: T): FlowTestObserver<T>

    /**
     * Asserts that the given flow received a value at the given index for which
     * the provided predicate returns true.
     * @return this
     */
    suspend fun assertValueAt(index: Int, predicate: (T) -> Boolean): FlowTestObserver<T>

    /**
     * Assert that the given flow received only the specified values in the specified order.
     * @return this
     */
    suspend fun assertValues(vararg values: T): FlowTestObserver<T>

    /**
     * Assert that the given flow received the specified number of values.
     * @return this
     */
    suspend fun assertValueCount(count: Int): FlowTestObserver<T>

    /**
     * Assert that the given flow has not received any value.
     * @return this
     */
    suspend fun assertNoValues(): FlowTestObserver<T>

    /**
     * Assert that the error has the given message.
     * @param message the message expected
     * @return this
     */
    suspend fun assertErrorMessage(message: String): FlowTestObserver<T>

    // endregion

}