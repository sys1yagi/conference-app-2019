package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.AppCoroutineDispatchers
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.SearchResult
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class SearchStore @Inject constructor(
    dispatcher: Dispatcher,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : ViewModel() {
    val query get() = searchResult.value?.query

    val searchResult = dispatcher
        .subscribe<Action.SearchResultLoaded>()
        .map { it.searchResult }
        .toLiveData(SearchResult.EMPTY, appCoroutineDispatchers)
}
