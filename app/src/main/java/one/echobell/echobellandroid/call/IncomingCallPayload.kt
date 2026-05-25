package one.echobell.echobellandroid.call

import android.content.Context
import android.content.Intent
import one.echobell.echobellandroid.data.Record

data class IncomingCallPayload(
    val callId: String,
    val notificationId: Int,
    val title: String,
    val body: String,
    val sourceName: String,
    val speechText: String,
) {
    val displayCallerName: String
        get() = if (title.isNotBlank() && title != sourceName) "$sourceName - $title" else sourceName

    fun toIntent(context: Context, action: String = IncomingCallActions.SHOW): Intent =
        Intent(context, IncomingCallActivity::class.java).apply {
            this.action = action
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(Extras.CALL_ID, callId)
            putExtra(Extras.NOTIFICATION_ID, notificationId)
            putExtra(Extras.TITLE, title)
            putExtra(Extras.BODY, body)
            putExtra(Extras.SOURCE_NAME, sourceName)
            putExtra(Extras.SPEECH_TEXT, speechText)
        }

    companion object {
        fun fromRecord(record: Record, channelName: String?, data: Map<String, String> = emptyMap()): IncomingCallPayload {
            val sourceName = channelName ?: record.directKeyName ?: "Echobell"
            val speechText = data["voiceMessage"] ?: listOf(record.title, record.body)
                .filter { it.isNotBlank() }
                .joinToString(" - ")
            val callId = data["callId"] ?: record.id
            return IncomingCallPayload(
                callId = callId,
                notificationId = record.id.hashCode(),
                title = record.title,
                body = record.body,
                sourceName = sourceName,
                speechText = speechText.ifBlank { sourceName },
            )
        }

        fun fromIntent(intent: Intent): IncomingCallPayload? {
            val callId = intent.getStringExtra(Extras.CALL_ID) ?: return null
            val sourceName = intent.getStringExtra(Extras.SOURCE_NAME) ?: "Echobell"
            return IncomingCallPayload(
                callId = callId,
                notificationId = intent.getIntExtra(Extras.NOTIFICATION_ID, callId.hashCode()),
                title = intent.getStringExtra(Extras.TITLE).orEmpty(),
                body = intent.getStringExtra(Extras.BODY).orEmpty(),
                sourceName = sourceName,
                speechText = intent.getStringExtra(Extras.SPEECH_TEXT)?.takeIf { it.isNotBlank() } ?: sourceName,
            )
        }
    }
}

object IncomingCallActions {
    const val SHOW = "one.echobell.echobellandroid.action.SHOW_INCOMING_CALL"
    const val ANSWER = "one.echobell.echobellandroid.action.ANSWER_INCOMING_CALL"
    const val DISMISS = "one.echobell.echobellandroid.action.DISMISS_INCOMING_CALL"
}

object Extras {
    const val CALL_ID = "callId"
    const val NOTIFICATION_ID = "notificationId"
    const val TITLE = "title"
    const val BODY = "body"
    const val SOURCE_NAME = "sourceName"
    const val SPEECH_TEXT = "speechText"
}
