package one.echobell.echobellandroid.push

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import one.echobell.echobellandroid.MainActivity
import one.echobell.echobellandroid.R
import one.echobell.echobellandroid.call.Extras
import one.echobell.echobellandroid.call.IncomingCallActions
import one.echobell.echobellandroid.call.IncomingCallActionReceiver
import one.echobell.echobellandroid.call.IncomingCallPayload
import one.echobell.echobellandroid.data.NotificationType
import one.echobell.echobellandroid.data.Record

object NotificationHelper {
    private const val CHANNEL_ID = "echobell_notifications"
    private const val CALL_CHANNEL_ID = "echobell_calls"
    private const val CALL_TIMEOUT_MS = 60_000L

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Echobell notifications",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Channel and direct webhook notifications"
        }
        manager.createNotificationChannel(channel)

        val callAudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val callChannel = NotificationChannel(
            CALL_CHANNEL_ID,
            "Echobell calls",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Urgent call-style notifications"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            enableVibration(true)
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE), callAudioAttributes)
        }
        manager.createNotificationChannel(callChannel)
    }

    fun showRecordNotification(
        context: Context,
        record: Record,
        channelName: String?,
        notificationType: NotificationType,
        data: Map<String, String> = emptyMap(),
    ) {
        ensureChannel(context)
        if (!canPostNotifications(context)) {
            return
        }

        if (notificationType == NotificationType.Calling) {
            showIncomingCallNotification(context, IncomingCallPayload.fromRecord(record, channelName, data))
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("recordId", record.id)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            record.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val priority = when (notificationType) {
            NotificationType.Calling, NotificationType.TimeSensitive -> NotificationCompat.PRIORITY_HIGH
            NotificationType.Active -> NotificationCompat.PRIORITY_DEFAULT
        }
        val category = if (notificationType == NotificationType.Calling) {
            NotificationCompat.CATEGORY_CALL
        } else {
            NotificationCompat.CATEGORY_MESSAGE
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(record.title)
            .setContentText(record.body)
            .setSubText(channelName ?: record.directKeyName)
            .setStyle(NotificationCompat.BigTextStyle().bigText(record.body))
            .setPriority(priority)
            .setCategory(category)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notify(context, record.id.hashCode(), notification)
    }

    fun cancel(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    private fun showIncomingCallNotification(context: Context, payload: IncomingCallPayload) {
        val showIntent = payload.toIntent(context)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            payload.notificationId,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val answerPendingIntent = PendingIntent.getActivity(
            context,
            payload.notificationId + 1,
            payload.toIntent(context, IncomingCallActions.ANSWER),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val dismissIntent = Intent(context, IncomingCallActionReceiver::class.java).apply {
            action = IncomingCallActions.DISMISS
            putExtra(Extras.CALL_ID, payload.callId)
            putExtra(Extras.NOTIFICATION_ID, payload.notificationId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            payload.notificationId + 2,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val caller = Person.Builder()
            .setName(payload.displayCallerName)
            .setImportant(true)
            .build()

        val notification = NotificationCompat.Builder(context, CALL_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(payload.displayCallerName)
            .setContentText(payload.body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setTimeoutAfter(CALL_TIMEOUT_MS)
            .setContentIntent(fullScreenPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setStyle(NotificationCompat.CallStyle.forIncomingCall(caller, dismissPendingIntent, answerPendingIntent))
            .build()

        notify(context, payload.notificationId, notification)
    }

    private fun canPostNotifications(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun notify(context: Context, notificationId: Int, notification: Notification) {
        if (!canPostNotifications(context)) return
        runCatching {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
}
