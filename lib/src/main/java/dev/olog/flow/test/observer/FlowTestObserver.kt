package dev.olog.flow.test.observer

/**
 * Computes eagerly flow values to allow testing, works with both cold and hot streams
 */
interface FlowTestObserver<T> {

    suspend fun isFinite(): Boolean

    suspend fun values(): List<T>

    suspend fun valuesCount(): Int

    /**
     * Assert that the [FlowTestObserver] this is a cold stream
     * @return this
     */
    suspend fun assertIsFinite(): FlowTestObserver<T>

    /**
     * Assert that the [FlowTestObserver] this is a hot stream
     * @return this
     */
    suspend fun assertIsNotFinite(): FlowTestObserver<T>

    /**
     * Assert that the [FlowTestObserver] received only the specified values in the specified order.
     * @param values the values expected
     * @return this
     */
    suspend fun assertValues(vararg values: T): FlowTestObserver<T>

    /**
     * Assert that this [FlowTestObserver] has not received any onNext events.
     * @return this
     */
    suspend fun assertNoValues(): FlowTestObserver<T>

    /**
     * Assert that this [FlowTestObserver] received the specified number of events.
     * @param count the expected number of onNext events
     * @return this
     */
    suspend fun assertValueCount(count: Int): FlowTestObserver<T>

    /**
     * Assert that the [FlowTestObserver] terminated
     * @return this
     */
    suspend fun assertTerminated(): FlowTestObserver<T>

    /**
     * Assert that the [FlowTestObserver] has not terminated
     * @return this
     */
    suspend fun assertNotTerminated(): FlowTestObserver<T>

}