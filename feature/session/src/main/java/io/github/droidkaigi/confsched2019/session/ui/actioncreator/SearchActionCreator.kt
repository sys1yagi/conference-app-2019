package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.AppCoroutineDispatchers
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.SearchResult
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@PageScope
class SearchActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    @PageScope private val lifecycle: Lifecycle,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : CoroutineScope by lifecycle.coroutineScope(appCoroutineDispatchers),
    ErrorHandler {
    fun search(query: String?, sessionContents: SessionContents) {
        // if we do not have query, we should show speakers
        if (query.isNullOrBlank()) {
            dispatcher.launchAndDispatch(
                Action.SearchResultLoaded(
                    SearchResult(
                        listOf(),
                        sessionContents.speakers,
                        query
                    )
                )
            )
            return
        }
        val searchResult = sessionContents.search(query)
        dispatcher.launchAndDispatch(Action.SearchResultLoaded(searchResult))
    }
}
