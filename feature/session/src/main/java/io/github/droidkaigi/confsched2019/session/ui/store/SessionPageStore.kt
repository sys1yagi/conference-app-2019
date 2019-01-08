package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.AppCoroutineDispatchers
import io.github.droidkaigi.confsched2019.ext.android.toSingleLiveData
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.widget.BottomSheetBehavior
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.map

class SessionPageStore @AssistedInject constructor(
    dispatcher: Dispatcher,
    appCoroutineDispatchers: AppCoroutineDispatchers,
    @Assisted val sessionPage: SessionPage
) : ViewModel() {
    @AssistedInject.Factory
    interface Factory {
        fun create(sessionPage: SessionPage): SessionPageStore
    }

    val filterSheetState: LiveData<Int> = dispatcher
        .subscribe<Action.BottomSheetFilterStateChanged>()
        .filter { it.page == sessionPage }
        .map { it.bottomSheetState }
        .toSingleLiveData(BottomSheetBehavior.STATE_EXPANDED, appCoroutineDispatchers)

    val toggleFilterSheet: LiveData<Unit> = dispatcher
        .subscribe<Action.BottomSheetFilterToggled>()
        .filter { it.page == sessionPage }
        .map { Unit }
        .toSingleLiveData(Unit, appCoroutineDispatchers)
}
