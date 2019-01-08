package io.github.droidkaigi.confsched2019.announcement.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.AppCoroutineDispatchers
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AnnouncementActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    private val fireStore: FireStore,
    @PageScope private val lifecycle: Lifecycle,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : CoroutineScope by lifecycle.coroutineScope(appCoroutineDispatchers),
    ErrorHandler {

    fun load() = launch {
        try {
            dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADING))
            dispatcher.dispatch(Action.AnnouncementLoaded(fireStore.getAnnouncements()))
            dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADED))
        } catch (e: Exception) {
            onError(e)
            dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.INITIALIZED))
        }
    }
}
