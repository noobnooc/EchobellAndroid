package one.echobell.echobellandroid.call

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import one.echobell.echobellandroid.push.NotificationHelper
import one.echobell.echobellandroid.ui.EchobellTheme

class IncomingCallActivity : ComponentActivity() {
    private var payload by mutableStateOf<IncomingCallPayload?>(null)
    private var answered by mutableStateOf(false)
    private var alertPlayer: IncomingCallAlertPlayer? = null
    private var textToSpeech: TextToSpeech? = null
    private var ttsReady = false
    private var pendingSpeech: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        handleIntentAction()
        if (isFinishing || payload == null) {
            finish()
            return
        }

        alertPlayer = IncomingCallAlertPlayer(this).also { it.start() }
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                textToSpeech?.language = Locale.getDefault()
                pendingSpeech?.let {
                    pendingSpeech = null
                    speakNow(it)
                }
            }
        }

        lifecycleScope.launch {
            IncomingCallEvents.dismissed.collect { callId ->
                if (payload?.callId == callId) finishCall()
            }
        }
        lifecycleScope.launch {
            delay(CALL_TIMEOUT_MS)
            if (!answered) finishCall()
        }

        setContent {
            EchobellTheme {
                payload?.let { currentPayload ->
                    IncomingCallScreen(
                        payload = currentPayload,
                        answered = answered,
                        onAnswer = { answerCall(currentPayload) },
                        onDismiss = { finishCall() },
                    )
                }
            }
        }

        if (intent?.action == IncomingCallActions.ANSWER) {
            payload?.let { answerCall(it) }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntentAction()
        payload?.let {
            when (intent.action) {
                IncomingCallActions.ANSWER -> answerCall(it)
                IncomingCallActions.DISMISS -> finishCall()
            }
        }
    }

    override fun onDestroy() {
        alertPlayer?.stop()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    private fun handleIntentAction() {
        val incomingPayload = IncomingCallPayload.fromIntent(intent)
        if (incomingPayload != null) payload = incomingPayload
        if (intent?.action == IncomingCallActions.DISMISS) {
            finishCall()
        }
    }

    private fun answerCall(currentPayload: IncomingCallPayload) {
        if (answered) return
        answered = true
        alertPlayer?.stop()
        NotificationHelper.cancel(this, currentPayload.notificationId)
        speak(currentPayload.speechText)
    }

    private fun finishCall() {
        payload?.let { NotificationHelper.cancel(this, it.notificationId) }
        alertPlayer?.stop()
        finish()
    }

    private fun speak(text: String) {
        if (ttsReady) {
            speakNow(text)
        } else {
            pendingSpeech = text
        }
    }

    private fun speakNow(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "echobell-incoming-call")
    }

    private companion object {
        const val CALL_TIMEOUT_MS = 60_000L
    }
}

@Composable
private fun IncomingCallScreen(
    payload: IncomingCallPayload,
    answered: Boolean,
    onAnswer: () -> Unit,
    onDismiss: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.95f),
                            Color(0xFF111418),
                        ),
                    ),
                )
                .padding(horizontal = 28.dp, vertical = 36.dp)
                .navigationBarsPadding(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (answered) "Connected" else "Incoming notification",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.82f),
                    )
                    Spacer(modifier = Modifier.height(42.dp))
                    Text(
                        text = payload.displayCallerName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (payload.body.isNotBlank()) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = payload.body,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.86f),
                            textAlign = TextAlign.Center,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CallActionButton(
                        label = if (answered) "End" else "Dismiss",
                        icon = Icons.Default.CallEnd,
                        containerColor = Color(0xFFD93025),
                        onClick = onDismiss,
                    )
                    if (!answered) {
                        CallActionButton(
                            label = "Answer",
                            icon = Icons.Default.Phone,
                            containerColor = Color(0xFF188038),
                            onClick = onAnswer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CallActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(72.dp),
            containerColor = containerColor,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp),
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(34.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(label, color = Color.White, style = MaterialTheme.typography.labelLarge)
    }
}
