package one.echobell.echobellandroid

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseTokenInstrumentedTest {
    @Test
    fun writesFirebaseMessagingToken() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        FirebaseApp.initializeApp(context)

        var token: String? = null
        val latch = CountDownLatch(1)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) token = task.result
            latch.countDown()
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS))
        assertTrue(!token.isNullOrBlank())
        context.filesDir.resolve("fcm-token.txt").writeText(token!!)
    }
}
