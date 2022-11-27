package com.dewerro.measurer.util.async

class PendingTask<T> {

    private val onCompleteActions = Invokable<T>()
    private val onErrorActions = Invokable<Throwable?>()
    private val onAnyActions = Invokable<Unit>()

    private var value: T? = null
    private var throwable: Throwable? = null
    private var completed: Boolean = false
    private var success: Boolean = false

    fun onComplete(completeAction: (T) -> Unit): PendingTask<T> {
        if (completed) {
            if (success) completeAction(value!!)
            return this
        }

        onCompleteActions.addAction(completeAction)

        return this
    }

    fun onError(errorAction: (Throwable?) -> Unit): PendingTask<T> {
        if (completed) {
            if (!success) errorAction(throwable)
            return this
        }
        onErrorActions.addAction(errorAction)
        return this
    }

    fun onAny(action: (Unit) -> Unit): PendingTask<T> {
        onAnyActions.addAction(action)
        return this
    }

    fun setCompleted(value: T) {
        this.value = value
        completed = true
        success = true

        invokeCompleteActions(value)
    }

    fun setFailure(throwable: Throwable?) {
        this.throwable = throwable
        completed = true
        success = false

        invokeErrorActions(throwable)
    }

    private fun invokeCompleteActions(value: T) {
        onCompleteActions.invoke(value)
        invokeAnyActions()
    }

    private fun invokeErrorActions(throwable: Throwable?) {
        onErrorActions.invoke(throwable)
        invokeAnyActions()
    }

    private fun invokeAnyActions() {
        onAnyActions.invoke(Unit)
    }

}