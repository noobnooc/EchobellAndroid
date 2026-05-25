package one.echobell.echobellandroid.call

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.VibratorManager

class IncomingCallAlertPlayer(context: Context) {
    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(AudioManager::class.java)
    private val vibrator = appContext.getSystemService(VibratorManager::class.java)?.defaultVibrator
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        .setAudioAttributes(audioAttributes)
        .build()
    private var ringtone: Ringtone? = null

    fun start() {
        audioManager?.requestAudioFocus(focusRequest)
        runCatching {
            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(appContext, ringtoneUri)?.apply {
                audioAttributes = this@IncomingCallAlertPlayer.audioAttributes
                isLooping = true
                play()
            }
        }
        runCatching {
            vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 900, 600), 0))
        }
    }

    fun stop() {
        runCatching { ringtone?.stop() }
        ringtone = null
        runCatching { vibrator?.cancel() }
        audioManager?.abandonAudioFocusRequest(focusRequest)
    }
}
