package dev.olog.flow.test.observer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withTimeout

internal abstract class BaseTestFlowObserver<T>(
    private val flow: Flow<T>
) : FlowTestObserver<T> {

    private var _hasCompleted: Boolean? = null
    private var _flowValues: List<T>? = null
    private var _error: Error? = null

    private fun isNotInitialized(): Boolean {
        return _flowValues == null ||
                _hasCompleted == null ||
                _error == null
    }

    private suspend fun setup() {
        if (isNotInitialized()) {
            val values = mutableListOf<T>()
            try {
                withTimeout(Long.MAX_VALUE) {
                    flow.onCompletion { _hasCompleted = true }
                        .catch { _error = Error.Wrapped(it) }
                        .collect { values.add(it) }

                    if (_error !is Error.Wrapped) {
                        _error = Error.Empty
                    }
                }
            } catch (ex: IllegalStateException) {
                _hasCompleted = false
                _error = Error.Empty
            }
            _flowValues = values
        }

        require(_hasCompleted != null)
        require(_flowValues != null)
        require(_error != null)
    }

    protected suspend fun flowValues(): List<T> {
        setup()
        return _flowValues!!
    }

    protected suspend fun hasCompletedInternal(): Boolean {
        setup()
        return _hasCompleted!!
    }

    protected suspend fun errorInternal(): Error {
        setup()
        return _error!!
    }

}