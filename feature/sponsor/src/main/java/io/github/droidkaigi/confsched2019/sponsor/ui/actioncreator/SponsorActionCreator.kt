package io.github.droidkaigi.confsched2019.sponsor.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import io.github.droidkaigi.confsched2019.data.repository.SponsorRepository
import io.github.droidkaigi.confsched2019.ext.android.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SponsorActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    private val sponsorRepository: SponsorRepository,
    @PageScope private val lifecycle: Lifecycle,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : CoroutineScope by lifecycle.coroutineScope(appCoroutineDispatchers),
    ErrorHandler {
    fun load() = launch {
        try {
            dispatcher.dispatch(Action.SponsorLoadingStateChanged(LoadingState.LOADING))
            // load cache data
            dispatcher.dispatch(Action.SponsorLoaded(sponsorRepository.sponsors()))
            // fetch from api
            sponsorRepository.refresh()
            // load fetched data
            dispatcher.dispatch(Action.SponsorLoaded(sponsorRepository.sponsors()))
            dispatcher.dispatch(Action.SponsorLoadingStateChanged(LoadingState.LOADED))
        } catch (e: Exception) {
            onError(e)
            dispatcher.dispatch(Action.SponsorLoadingStateChanged(LoadingState.INITIALIZED))
        }
    }
}
