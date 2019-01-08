package io.github.droidkaigi.confsched2019.ext.android

import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.INITIALIZED
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * from: https://github.com/Kotlin/kotlinx.coroutines/pull/760
 * Returns a [CoroutineScope] that uses [Dispatchers.Main] by default, and that will be cancelled as
 * soon as this [Lifecycle] [currentState][Lifecycle.getCurrentState] is no longer
 * [at least][Lifecycle.State.isAtLeast] the passed [activeWhile] state.
 *
 * **Beware**: if the current state is lower than the passed [activeWhile] state, you'll get an
 * already cancelled scope.
 */
fun Lifecycle.createScope(
    activeWhile: Lifecycle.State,
    appCoroutineDispatchers: AppCoroutineDispatchers
): CoroutineScope {
    return CoroutineScope(
        createJob(activeWhile, appCoroutineDispatchers) + appCoroutineDispatchers.Main
    )
}

/**
 * Creates a [SupervisorJob] that will be cancelled as soon as this [Lifecycle]
 * [currentState][Lifecycle.getCurrentState] is no longer [at least][Lifecycle.State.isAtLeast] the
 * passed [activeWhile] state.
 *
 * **Beware**: if the current state is lower than the passed [activeWhile] state, you'll get an
 * already cancelled job.
 */
fun Lifecycle.createJob(
    activeWhile: Lifecycle.State = INITIALIZED,
    appCoroutineDispatchers: AppCoroutineDispatchers
): Job {
    require(activeWhile != Lifecycle.State.DESTROYED) {
        "DESTROYED is a terminal state that is forbidden for createJob(…), to avoid leaks."
    }
    return SupervisorJob().also { job ->
        when (currentState) {
            Lifecycle.State.DESTROYED -> job.cancel() // Fast path if already destroyed
            else -> GlobalScope.launch(appCoroutineDispatchers.Main) {
                // State is usually synced on next loop,
                // this allows to use STARTED from onStart in Activities for example.
                addObserver(object : GenericLifecycleObserver {
                    override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event) {
                        if (!currentState.isAtLeast(activeWhile)) {
                            removeObserver(this)
                            job.cancel()
                        }
                    }
                })
            }
        }
    }
}
