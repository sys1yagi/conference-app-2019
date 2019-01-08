package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummySessionData
import io.github.droidkaigi.confsched2019.firstDummySpeechSession
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.widget.component.TestLifecycleOwner
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class SessionContentsActionCreatorTest {
    @RelaxedMockK lateinit var dispatcher: Dispatcher
    @RelaxedMockK lateinit var sessionRepository: SessionRepository

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test fun load() = runBlocking<Unit> {
        val lifecycleOwner = TestLifecycleOwner().handleEvent(Lifecycle.Event.ON_RESUME)
        coEvery { sessionRepository.sessionContents() } returns SessionContents.EMPTY
        val sessionsActionCreator = SessionContentsActionCreator(
            dispatcher,
            sessionRepository,
            lifecycleOwner.lifecycle
        )

        sessionsActionCreator.load()

        coVerify(ordering = Ordering.SEQUENCE) {
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADING))
            sessionRepository.sessionContents()
            dispatcher.dispatch(Action.SessionContentsLoaded(SessionContents.EMPTY))
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADED))
        }
    }

    @Test fun toggleFavorite() = runBlocking<Unit> {
        val lifecycleOwner = TestLifecycleOwner().handleEvent(Lifecycle.Event.ON_RESUME)
        val dummySessionData = dummySessionData()
        val dummySessionContents = SessionContents.EMPTY.copy(
            sessions = dummySessionData
        )
        coEvery { sessionRepository.sessionContents() } returns dummySessionContents
        val sessionsActionCreator = SessionContentsActionCreator(
            dispatcher,
            sessionRepository,
            lifecycleOwner.lifecycle
        )

        sessionsActionCreator.toggleFavorite(
            firstDummySpeechSession()
        )

        coVerify(ordering = Ordering.SEQUENCE) {
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADING))
            sessionRepository.toggleFavorite(firstDummySpeechSession())
            sessionRepository.sessionContents()
            dispatcher.dispatch(
                Action.SessionContentsLoaded(dummySessionContents)
            )
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADED))
        }
    }
}
