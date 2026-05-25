package one.echobell.echobellandroid.push

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object FirebaseBootstrap {
    fun initializeIfConfigured(context: Context): Boolean {
        if (FirebaseApp.getApps(context).isNotEmpty()) return true
        return runCatching { FirebaseApp.initializeApp(context) != null }.getOrDefault(false)
    }

    suspend fun notificationToken(context: Context): String? {
        if (!initializeIfConfigured(context)) return null
        return suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (continuation.isActive) {
                        continuation.resume(if (task.isSuccessful) task.result else null)
                    }
                }
        }
    }
}
