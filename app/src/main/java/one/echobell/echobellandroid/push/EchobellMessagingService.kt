package one.echobell.echobellandroid.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking
import one.echobell.echobellandroid.data.ApiClient
import one.echobell.echobellandroid.data.LocalStore
import one.echobell.echobellandroid.data.NotificationType
import one.echobell.echobellandroid.data.Record
import one.echobell.echobellandroid.data.nowEpoch

class EchobellMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        runBlocking {
            val store = LocalStore(applicationContext)
            val saved = store.load()
            store.saveNotificationToken(token)
            if (saved.deviceToken != null) {
                runCatching {
                    val response = ApiClient().refresh(saved.deviceToken, token)
                    store.saveAuth(response.jwt, saved.deviceToken)
                    store.saveUser(response.user.toUser())
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val title = data["title"] ?: message.notification?.title ?: return
        val body = data["body"] ?: message.notification?.body ?: ""
        val channelId = data["channelId"]?.toIntOrNull()
        val directKeyId = data["directKeyId"]?.toIntOrNull()
        val notificationType = NotificationType.fromValue(data["notificationType"]) ?: NotificationType.Active

        val record = Record(
            channelId = channelId,
            directKeyId = directKeyId,
            directKeyName = data["directKeyName"],
            title = title,
            body = body,
            requestLink = data["requestLink"],
            externalLink = data["externalLink"],
            createdAt = nowEpoch(),
        )

        runBlocking {
            val store = LocalStore(applicationContext)
            val saved = store.load()
            store.saveRecords(listOf(record) + saved.records)
        }

        NotificationHelper.showRecordNotification(
            context = applicationContext,
            record = record,
            channelName = data["channelName"],
            notificationType = notificationType,
            data = data,
        )
    }
}
