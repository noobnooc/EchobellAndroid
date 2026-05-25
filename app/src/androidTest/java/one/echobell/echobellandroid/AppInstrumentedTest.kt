package one.echobell.echobellandroid

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import one.echobell.echobellandroid.call.IncomingCallEvents
import one.echobell.echobellandroid.call.Extras
import one.echobell.echobellandroid.call.IncomingCallActions
import one.echobell.echobellandroid.call.IncomingCallActivity
import one.echobell.echobellandroid.data.NotificationType
import one.echobell.echobellandroid.data.Record
import one.echobell.echobellandroid.push.NotificationHelper
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppInstrumentedTest {
    @Test
    fun packageNameMatchesApplicationId() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("one.echobell.echobellandroid", context.packageName)
    }

    @Test
    fun incomingCallActivityLaunchesFromAppContext() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val intent = Intent(context, IncomingCallActivity::class.java).apply {
            action = IncomingCallActions.SHOW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Extras.CALL_ID, "instrumented-call")
            putExtra(Extras.NOTIFICATION_ID, 1001)
            putExtra(Extras.TITLE, "Instrumented call")
            putExtra(Extras.BODY, "Incoming call activity smoke test")
            putExtra(Extras.SOURCE_NAME, "Echobell")
            putExtra(Extras.SPEECH_TEXT, "Instrumented call")
        }

        val activity = instrumentation.startActivitySync(intent)
        instrumentation.waitForIdleSync()
        activity.finish()
    }

    @Test
    fun callingNotificationCanBePostedFromAppContext() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val record = Record(
            id = "instrumented-calling-notification",
            title = "Instrumented call",
            body = "Calling notification smoke test",
        )

        NotificationHelper.showRecordNotification(
            context = context,
            record = record,
            channelName = "Echobell",
            notificationType = NotificationType.Calling,
            data = mapOf(
                "callId" to record.id,
                "voiceMessage" to "Instrumented call",
            ),
        )
        instrumentation.waitForIdleSync()
        Thread.sleep(500)
        IncomingCallEvents.dismiss(record.id)
        NotificationHelper.cancel(context, record.id.hashCode())
    }
}
