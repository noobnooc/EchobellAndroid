package one.echobell.echobellandroid.call

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object IncomingCallEvents {
    private val dismissedFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)

    val dismissed = dismissedFlow.asSharedFlow()

    fun dismiss(callId: String) {
        dismissedFlow.tryEmit(callId)
    }
}
