package one.echobell.echobellandroid.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import one.echobell.echobellandroid.push.NotificationHelper

class IncomingCallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != IncomingCallActions.DISMISS) return

        val callId = intent.getStringExtra(Extras.CALL_ID) ?: return
        val notificationId = intent.getIntExtra(Extras.NOTIFICATION_ID, callId.hashCode())
        NotificationHelper.cancel(context, notificationId)
        IncomingCallEvents.dismiss(callId)
    }
}
