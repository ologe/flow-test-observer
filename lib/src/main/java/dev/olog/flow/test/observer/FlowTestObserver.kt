package dev.olog.flow.test.observer

/**
 * Computes eagerly flow values to allow testing, works with both cold and hot/infinite streams.
 * All delays will be ignored.
 */
interface FlowTestObserver<T> {

    // region getters

    /**
     * @return true if the given flow is cold/finite.
     */
    suspend fun isFinite(): Boolean

    /**
     * @return true if the given flow is completed or cancelled.
     */
    suspend fun isTerminated(): Boolean

    /**
     * @return all the values emitted from the given flow.
     */
    suspend fun values(): List<T>

    /**
     * @return the number of values emitted from the stream.
     */
    suspend fun valuesCount(): Int

    // endregion

    // region assertions

    /**
     * Assert the given flow is cold/finite.
     * @return this
     */
    suspend fun assertIsFinite(): FlowTestObserver<T>

    /**
     * Assert the given flow is hot/infinite.
     * @return this
     */
    suspend fun assertIsNotFinite(): FlowTestObserver<T>

    /**
     * Assert that this given flow received only the specified values in the specified order.
     * @param values the values expected
     * @return this
     */
    suspend fun assertValues(vararg values: T): FlowTestObserver<T>

    /**
     * Assert that this given flow has not received any value.
     * @return this
     */
    suspend fun assertNoValues(): FlowTestObserver<T>

    /**
     * Assert that this given flow received the specified number of values.
     * @param count the expected number of events
     * @return this
     */
    suspend fun assertValueCount(count: Int): FlowTestObserver<T>

    /**
     * Assert that the given is completed or cancelled
     * @return this
     */
    suspend fun assertTerminated(): FlowTestObserver<T>

    /**
     * Assert that the given is not completed or cancelled
     * @return this
     */
    suspend fun assertNotTerminated(): FlowTestObserver<T>

    // endregion

}