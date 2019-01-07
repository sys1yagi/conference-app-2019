package io.github.droidkaigi.confsched2019.user.actioncreator

import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.AppCoroutineDispatchers
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserActionCreator @Inject constructor(
    val activity: FragmentActivity,
    override val dispatcher: Dispatcher,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : CoroutineScope by activity.coroutineScope(appCoroutineDispatchers), ErrorHandler {

    val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun load() {
        if (firebaseAuth.currentUser == null) {
            signIn()
        } else {
            dispatcher.launchAndDispatch(Action.UserRegistered)
        }
    }

    private fun signIn() {
        launch {
            try {
                firebaseAuth.signInAnonymously().await()
                dispatcher.dispatch(Action.UserRegistered)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
