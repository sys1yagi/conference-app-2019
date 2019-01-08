package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.AppCoroutineDispatchers
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.session.di.SessionPageScope
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import io.github.droidkaigi.confsched2019.widget.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@SessionPageScope
class SessionPageActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    @SessionPageScope private val lifecycle: Lifecycle,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : CoroutineScope by lifecycle.coroutineScope(appCoroutineDispatchers),
    ErrorHandler {
    fun toggleFilterExpanded(page: SessionPage) {
        dispatcher.launchAndDispatch(Action.BottomSheetFilterToggled(page))
    }

    fun changeFilterSheet(
        page: SessionPage,
        @BottomSheetBehavior.Companion.State bottomSheetState: Int
    ) {
        dispatcher.launchAndDispatch(Action.BottomSheetFilterStateChanged(page, bottomSheetState))
    }
}
