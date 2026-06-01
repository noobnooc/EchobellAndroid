package one.echobell.echobellandroid

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import one.echobell.echobellandroid.data.EchobellViewModel
import one.echobell.echobellandroid.ui.EchobellApp
import one.echobell.echobellandroid.ui.EchobellTheme

class MainActivity : ComponentActivity() {
    private val viewModel: EchobellViewModel by viewModels()
    private val pendingSubscriptionToken = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingSubscriptionToken.value = extractSubscriptionToken(intent?.dataString)

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) {
                viewModel.syncNotificationPermission()
            }
            val fullScreenIntentPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) {
                viewModel.syncFullScreenIntentPermission()
            }

            EchobellTheme {
                EchobellApp(
                    state = state,
                    viewModel = viewModel,
                    pendingSubscribeToken = pendingSubscriptionToken.value,
                    onSubscribeTokenConsumed = { token ->
                        if (pendingSubscriptionToken.value == token) {
                            pendingSubscriptionToken.value = null
                        }
                    },
                    requestNotificationPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.syncNotificationPermission()
                        }
                    },
                    requestFullScreenIntentPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                                data = Uri.parse("package:$packageName")
                            }
                            runCatching {
                                fullScreenIntentPermissionLauncher.launch(intent)
                            }.onFailure {
                                val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.parse("package:$packageName")
                                }
                                runCatching {
                                    fullScreenIntentPermissionLauncher.launch(fallbackIntent)
                                }.onFailure {
                                    viewModel.showMessage("Unable to open system settings.")
                                }
                            }
                        } else {
                            viewModel.syncFullScreenIntentPermission()
                        }
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.syncPermissionStates()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingSubscriptionToken.value = extractSubscriptionToken(intent.dataString)
    }
}
