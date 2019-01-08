package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.AppCoroutineDispatchers
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import io.github.droidkaigi.confsched2019.util.logd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@PageScope
class SessionContentsActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    private val sessionRepository: SessionRepository,
    @PageScope private val lifecycle: Lifecycle,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : CoroutineScope by lifecycle.coroutineScope(appCoroutineDispatchers),
    ErrorHandler {
    fun refresh() = launch {
        try {
            logd { "SessionContentsActionCreator: refresh start" }
            dispatcher.dispatchLoadingState(LoadingState.LOADING)
            logd { "SessionContentsActionCreator: At first, load db data" }
            // At first, load db data
            val sessionContents = sessionRepository.sessionContents()
            dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))

            // fetch api data
            logd { "SessionContentsActionCreator: fetch api data" }
            sessionRepository.refresh()

            // reload db data
            logd { "SessionContentsActionCreator: reload db data" }
            val refreshedSessionContents = sessionRepository.sessionContents()
            dispatcher.dispatch(Action.SessionContentsLoaded(refreshedSessionContents))
            logd { "SessionContentsActionCreator: refresh end" }
            dispatcher.dispatchLoadingState(LoadingState.LOADED)
        } catch (e: Exception) {
            onError(e)
            dispatcher.dispatchLoadingState(LoadingState.INITIALIZED)
        }
    }

    fun load() {
        launch {
            try {
                dispatcher.dispatchLoadingState(LoadingState.LOADING)
                val sessionContents = sessionRepository.sessionContents()
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))
                dispatcher.dispatchLoadingState(LoadingState.LOADED)
            } catch (e: Exception) {
                onError(e)
                dispatcher.dispatchLoadingState(LoadingState.INITIALIZED)
            }
        }
    }

    fun toggleFavorite(session: Session.SpeechSession) {
        launch {
            try {
                dispatcher.dispatchLoadingState(LoadingState.LOADING)
                sessionRepository.toggleFavorite(session)
                val sessionContents = sessionRepository.sessionContents()
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))
                dispatcher.dispatchLoadingState(LoadingState.LOADED)
            } catch (e: Exception) {
                onError(e)
                dispatcher.dispatchLoadingState(LoadingState.INITIALIZED)
            }
        }
    }

    private suspend fun Dispatcher.dispatchLoadingState(loadingState: LoadingState) {
        dispatch(Action.SessionLoadingStateChanged(loadingState))
    }
}
