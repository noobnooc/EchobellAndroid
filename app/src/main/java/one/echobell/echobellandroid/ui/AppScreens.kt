package one.echobell.echobellandroid.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.app.Activity
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.compose.AsyncImage
import one.echobell.echobellandroid.BuildConfig
import one.echobell.echobellandroid.R
import one.echobell.echobellandroid.extractSubscriptionToken
import one.echobell.echobellandroid.data.Announcement
import one.echobell.echobellandroid.data.AnnouncementLevel
import one.echobell.echobellandroid.data.ApiChannel
import one.echobell.echobellandroid.data.ApiChannelSubscriber
import one.echobell.echobellandroid.data.AppUiState
import one.echobell.echobellandroid.data.BillingProduct
import one.echobell.echobellandroid.data.Channel
import one.echobell.echobellandroid.data.DirectKey
import one.echobell.echobellandroid.data.EchobellViewModel
import one.echobell.echobellandroid.data.FREE_USER_CHANNEL_LIMIT
import one.echobell.echobellandroid.data.FREE_USER_DIRECT_KEY_LIMIT
import one.echobell.echobellandroid.data.NotificationType
import one.echobell.echobellandroid.data.Record
import one.echobell.echobellandroid.data.GoogleBillingManager
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private object Routes {
    const val Records = "records"
    const val Channels = "channels"
    const val ChannelNew = "channel-new"
    const val ChannelDetail = "channel/{id}"
    const val ChannelEdit = "channel/{id}/edit"
    const val Subscribers = "channel/{id}/subscribers"
    const val Subscribe = "subscribe?token={token}"
    const val Direct = "direct"
    const val Settings = "settings"
    const val User = "user"
    const val Invite = "invite"
    const val Paywall = "paywall"
    const val Announcements = "announcements"
    const val AnnouncementDetail = "announcement/{id}"

    fun channel(id: Int) = "channel/$id"
    fun editChannel(id: Int) = "channel/$id/edit"
    fun subscribers(id: Int) = "channel/$id/subscribers"
    fun subscribe(token: String? = null) = "subscribe?token=${token.orEmpty().urlEncode()}"
    fun announcement(id: Int) = "announcement/$id"
}

private val TopLevelRoutes = setOf(
    Routes.Records,
    Routes.Channels,
    Routes.Direct,
    Routes.Settings,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchobellApp(
    state: AppUiState,
    viewModel: EchobellViewModel,
    pendingSubscribeToken: String?,
    onSubscribeTokenConsumed: (String) -> Unit,
    requestNotificationPermission: () -> Unit,
    requestFullScreenIntentPermission: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.messages) {
        val message = state.messages.firstOrNull() ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message.text)
        viewModel.consumeMessage(message.id)
    }

    if (!state.initialized) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (!state.authenticated) {
        AuthScreen(state, viewModel, snackbarHostState)
        return
    }

    val navController = rememberNavController()
    var consumedSubscribeToken by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(state.authenticated, pendingSubscribeToken) {
        val token = pendingSubscribeToken?.takeIf { it != consumedSubscribeToken } ?: return@LaunchedEffect
        if (state.authenticated) {
            consumedSubscribeToken = token
            onSubscribeTokenConsumed(token)
            navController.navigate(Routes.subscribe(token))
        }
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentRoute in TopLevelRoutes) {
                BottomNav(navController)
            }
        },
        floatingActionButton = {
            when (currentRoute) {
                Routes.Channels -> FloatingActionButton(
                    onClick = {
                        if (state.canCreateOrSubscribeChannel) navController.navigate(Routes.subscribe()) else navController.navigate(Routes.Paywall)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(Icons.Default.Link, contentDescription = stringResource(R.string.channels_subscribe_fab))
                }
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            NavHost(navController = navController, startDestination = Routes.Records) {
                composable(Routes.Records) {
                    RecordsScreen(
                        state,
                        viewModel,
                        navController,
                        requestNotificationPermission,
                        requestFullScreenIntentPermission,
                    )
                }
                composable(Routes.Channels) {
                    ChannelsScreen(state, viewModel, navController)
                }
                composable(Routes.ChannelNew) {
                    ChannelFormScreen(state, viewModel, navController, channel = null)
                }
                composable(
                    Routes.ChannelDetail,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { entry ->
                    val channel = state.channels.firstOrNull { it.remoteId == entry.arguments?.getInt("id") }
                    if (channel == null) MissingScreen(stringResource(R.string.channel_not_found), navController) else ChannelDetailScreen(state, viewModel, navController, channel)
                }
                composable(
                    Routes.ChannelEdit,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { entry ->
                    val channel = state.channels.firstOrNull { it.remoteId == entry.arguments?.getInt("id") }
                    if (channel == null) MissingScreen(stringResource(R.string.channel_not_found), navController) else ChannelFormScreen(state, viewModel, navController, channel)
                }
                composable(
                    Routes.Subscribers,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { entry ->
                    val channel = state.channels.firstOrNull { it.remoteId == entry.arguments?.getInt("id") }
                    if (channel == null) MissingScreen(stringResource(R.string.channel_not_found), navController) else SubscribersScreen(viewModel, navController, channel)
                }
                composable(
                    Routes.Subscribe,
                    arguments = listOf(navArgument("token") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    }),
                ) { entry ->
                    SubscribeScreen(state, viewModel, navController, entry.arguments?.getString("token").orEmpty())
                }
                composable(Routes.Direct) {
                    DirectKeysScreen(state, viewModel, navController)
                }
                composable(Routes.Settings) {
                    SettingsScreen(
                        state,
                        viewModel,
                        navController,
                        requestNotificationPermission,
                        requestFullScreenIntentPermission,
                    )
                }
                composable(Routes.User) {
                    UserSettingsScreen(state, viewModel, navController)
                }
                composable(Routes.Invite) {
                    InviteScreen(state, viewModel, navController)
                }
                composable(Routes.Paywall) {
                    PaywallScreen(state, viewModel, navController)
                }
                composable(Routes.Announcements) {
                    AnnouncementsScreen(state, viewModel, navController)
                }
                composable(
                    Routes.AnnouncementDetail,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { entry ->
                    val announcement = state.announcements.firstOrNull { it.id == entry.arguments?.getInt("id") }
                    if (announcement == null) MissingScreen(stringResource(R.string.announcement_not_found), navController) else AnnouncementDetailScreen(announcement, viewModel, navController)
                }
            }
        }
    }
}

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$")

@Composable
private fun AuthScreen(state: AppUiState, viewModel: EchobellViewModel, snackbarHostState: SnackbarHostState) {
    var email by rememberSaveable { mutableStateOf("") }
    var code by rememberSaveable { mutableStateOf("") }
    var codeSent by rememberSaveable { mutableStateOf(false) }
    val emailValid = remember(email) { EMAIL_REGEX.matches(email.trim()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val focusManager = LocalFocusManager.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
                .imePadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.ic_echobell_mark),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            stringResource(R.string.auth_tagline),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (!codeSent) {
                            AuthFormLabel(stringResource(R.string.auth_continue_with_email))
                            Spacer(Modifier.height(16.dp))
                            AppTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = stringResource(R.string.auth_email_label),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (emailValid && !state.busy) {
                                            focusManager.clearFocus()
                                            viewModel.sendVerificationCode(email) { codeSent = true }
                                        }
                                    }
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                isError = email.isNotEmpty() && !emailValid
                            )
                            if (email.isNotEmpty() && !emailValid) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    stringResource(R.string.auth_email_invalid),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.sendVerificationCode(email) { codeSent = true }
                                },
                                enabled = emailValid && !state.busy,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.auth_send_code))
                            }
                        } else {
                            AuthFormLabel(stringResource(R.string.auth_enter_code_title))
                            Spacer(Modifier.height(4.dp))
                            Text(stringResource(R.string.auth_code_sent_to_fmt, email), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(16.dp))
                            AppTextField(
                                value = code,
                                onValueChange = { code = it.filter(Char::isDigit).take(6) },
                                label = stringResource(R.string.auth_code_label),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (code.length == 6 && !state.busy) {
                                            focusManager.clearFocus()
                                            viewModel.signIn(email, code)
                                        }
                                    }
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                isError = code.isNotEmpty() && code.length < 6
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.signIn(email, code)
                                },
                                enabled = code.length == 6 && !state.busy,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.auth_sign_in))
                            }
                            Spacer(Modifier.height(8.dp))
                            TextButton(
                                onClick = { codeSent = false; code = "" },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.auth_use_another_email))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthFormLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun BottomNav(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
    ) {
        listOf(
            Triple(Routes.Records, Icons.Default.Notifications, R.string.nav_records),
            Triple(Routes.Channels, Icons.Default.Link, R.string.nav_channels),
            Triple(Routes.Direct, Icons.Default.Key, R.string.nav_direct),
            Triple(Routes.Settings, Icons.Default.Settings, R.string.nav_settings),
        ).forEach { (route, icon, labelRes) ->
            val label = stringResource(labelRes)
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { navController.navigateTopLevel(route) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordsScreen(
    state: AppUiState,
    viewModel: EchobellViewModel,
    navController: NavHostController,
    requestNotificationPermission: () -> Unit,
    requestFullScreenIntentPermission: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var expandedRecordId by rememberSaveable { mutableStateOf<String?>(null) }

    ScreenScaffold(
        title = stringResource(R.string.app_name),
        actions = {
            IconButton(onClick = { navController.navigate(Routes.Announcements) }) {
                Icon(Icons.Default.Campaign, contentDescription = stringResource(R.string.announcements_title))
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.records_inbox_actions))
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.records_mark_all_read)) },
                        onClick = {
                            menuExpanded = false
                            viewModel.markAllRecordsRead()
                        },
                        leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.records_clear_all), color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            menuExpanded = false
                            showClearConfirm = true
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        },
    ) { padding ->
        if (showClearConfirm) {
            ConfirmDialog(
                title = stringResource(R.string.records_clear_all_title),
                text = stringResource(R.string.records_clear_all_message),
                confirm = stringResource(R.string.records_clear_all),
                onDismiss = { showClearConfirm = false },
                onConfirm = {
                    showClearConfirm = false
                    viewModel.deleteAllRecords()
                }
            )
        }
        val grouped = remember(state.records) {
            state.records
                .sortedByDescending { it.createdAt }
                .groupBy { dayKey(it.createdAt) }
        }
        val latestUnread = remember(state.announcements, state.readAnnouncementIds) {
            state.announcements
                .filterNot { it.id in state.readAnnouncementIds }
                .maxWithOrNull(compareBy<Announcement> { levelPriority(it.level) }.thenBy { maxOf(it.startsAt, it.updatedAt) })
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (latestUnread != null) {
                item {
                    AnnouncementPreview(latestUnread) {
                        navController.navigate(Routes.announcement(latestUnread.id))
                    }
                }
            }
            if (!state.notificationAuthorized) {
                item {
                    PermissionBanner(requestNotificationPermission)
                }
            } else if (!state.fullScreenIntentAuthorized) {
                item {
                    FullScreenIntentBanner(requestFullScreenIntentPermission)
                }
            }
            if (state.records.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Notifications,
                        title = stringResource(R.string.records_empty_title),
                        description = stringResource(R.string.records_empty_description),
                    )
                }
            }
            grouped.forEach { (day, records) ->
                item {
                    Text(day, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
                items(records, key = { it.id }) { record ->
                    val currentOnToggleRead by rememberUpdatedState { viewModel.markRecord(record.id, !record.checked) }
                    val currentOnDelete by rememberUpdatedState { viewModel.deleteRecord(record.id) }
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            when (dismissValue) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    currentOnDelete()
                                    true
                                }
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    currentOnToggleRead()
                                    false
                                }
                                SwipeToDismissBoxValue.Settled -> false
                            }
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val direction = dismissState.dismissDirection
                            val color = when (direction) {
                                SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
                                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                else -> Color.Transparent
                            }
                            val alignment = when (direction) {
                                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                else -> Alignment.Center
                            }
                            val icon = when (direction) {
                                SwipeToDismissBoxValue.StartToEnd -> if (record.checked) Icons.Default.VisibilityOff else Icons.Default.Visibility
                                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                                else -> null
                            }
                            val iconColor = when (direction) {
                                SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.onPrimaryContainer
                                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.onErrorContainer
                                else -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment
                            ) {
                                if (icon != null) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = iconColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        content = {
                            RecordCard(
                                record = record,
                                channel = state.channels.firstOrNull { it.remoteId == record.channelId },
                                expanded = expandedRecordId == record.id,
                                onExpandedChange = { expanded ->
                                    expandedRecordId = if (expanded) record.id else null
                                },
                                onMarkRead = { viewModel.markRecord(record.id, true) },
                                onMarkUnread = { viewModel.markRecord(record.id, false) },
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordCard(
    record: Record,
    channel: Channel?,
    expanded: Boolean? = null,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    onMarkRead: () -> Unit,
    onMarkUnread: () -> Unit,
) {
    var localExpanded by rememberSaveable(record.id) { mutableStateOf(false) }
    val isExpanded = expanded ?: localExpanded
    fun setExpanded(value: Boolean) {
        if (onExpandedChange == null) {
            localExpanded = value
        } else {
            onExpandedChange(value)
        }
    }
    val context = LocalContext.current
    AppCard(
        modifier = Modifier.clickable {
            setExpanded(!isExpanded)
            if (!record.checked) onMarkRead()
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                    .size(width = 28.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(channel?.let { parseColor(it.colorHex) } ?: MaterialTheme.colorScheme.tertiary),
            )
            Text(
                channel?.name ?: record.directKeyName ?: stringResource(R.string.record_fallback_title),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!record.checked) {
                Box(
                    Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            Text(formatTime(record.createdAt), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            record.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (record.checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
        )
        if (record.body.isNotBlank()) {
            Text(
                record.body,
                color = if (record.checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (isExpanded) {
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                record.requestLink?.takeIf { it.isNotBlank() }?.let { link ->
                    TextButton(onClick = { context.openUrl(link) }) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.record_source))
                    }
                }
                record.externalLink?.takeIf { it.isNotBlank() }?.let { link ->
                    TextButton(onClick = { context.openUrl(link) }) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.record_external))
                    }
                }
                TextButton(onClick = onMarkUnread, enabled = record.checked) {
                    Icon(Icons.Default.VisibilityOff, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.record_mark_unread))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChannelsScreen(state: AppUiState, viewModel: EchobellViewModel, navController: NavHostController) {
    val uriHandler = LocalUriHandler.current
    var showIntro by rememberSaveable { mutableStateOf(true) }

    ScreenScaffold(
        title = stringResource(R.string.nav_channels),
        actions = {
            IconButton(onClick = { uriHandler.openUri("https://echobell.one/en/docs/template") }) {
                Icon(Icons.AutoMirrored.Filled.Help, contentDescription = stringResource(R.string.channels_documentation))
            }
            IconButton(onClick = {
                if (state.canCreateOrSubscribeChannel) navController.navigate(Routes.ChannelNew) else navController.navigate(Routes.Paywall)
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.channels_new))
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (showIntro) {
                item {
                    AppCard(
                        modifier = Modifier.clickable { uriHandler.openUri("https://echobell.one/en/docs/template") },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Default.Link,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(R.string.nav_channels), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(stringResource(R.string.channels_intro_description), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            }
                            IconButton(onClick = { showIntro = false }) {
                                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_close), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
            }
            if (state.channels.isEmpty()) {
                item {
                    EmptyState(Icons.Default.Link, stringResource(R.string.channels_empty_title), stringResource(R.string.channels_empty_description))
                }
            }
            items(state.channels, key = { it.remoteId }) { channel ->
                ChannelCard(channel) { navController.navigate(Routes.channel(channel.remoteId)) }
            }
            if (!state.canCreateOrSubscribeChannel) {
                item {
                    LimitBanner(pluralStringResource(R.plurals.channels_limit_banner, FREE_USER_CHANNEL_LIMIT, FREE_USER_CHANNEL_LIMIT)) {
                        navController.navigate(Routes.Paywall)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelCard(channel: Channel, onClick: () -> Unit) {
    AppCard(Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .padding(top = 3.dp)
                    .size(width = 5.dp, height = 24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(parseColor(channel.colorHex)),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(channel.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text("#${channel.remoteId}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(channel.titleTemplate, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(channel.bodyTemplate, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        channel.lastTriggeredAt?.let { stringResource(R.string.channel_last_triggered_fmt, formatDateTime(it)) }
                            ?: stringResource(R.string.channel_never_triggered),
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Spacer(Modifier.weight(1f))
                    StatusChip(channel)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(channel: Channel) {
    val (label, containerColor, textColor) = when {
        channel.detached -> Triple(
            stringResource(R.string.channel_status_inactive),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        channel.isAdmin -> Triple(
            stringResource(R.string.channel_status_managed),
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        channel.subscribedAt != null -> Triple(
            stringResource(R.string.channel_status_subscribed),
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        else -> Triple(
            stringResource(R.string.channel_status_available),
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
    AssistChip(
        onClick = {},
        label = { Text(label) },
        border = null,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = textColor
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChannelDetailScreen(
    state: AppUiState,
    viewModel: EchobellViewModel,
    navController: NavHostController,
    channel: Channel,
) {
    val context = LocalContext.current
    val records = remember(state.records, channel.remoteId) {
        state.records.filter { it.channelId == channel.remoteId }.sortedByDescending { it.createdAt }
    }
    var deleteDialog by rememberSaveable { mutableStateOf(false) }
    var unsubscribeDialog by rememberSaveable { mutableStateOf(false) }

    ScreenScaffold(
        title = channel.name,
        navigationIcon = { BackOrEmpty(navController) },
        actions = {
            if (channel.isAdmin && !channel.detached) {
                IconButton(onClick = { navController.navigate(Routes.editChannel(channel.remoteId)) }) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.action_edit))
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(width = 6.dp, height = 28.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(parseColor(channel.colorHex)),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(channel.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("#${channel.remoteId}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusChip(channel)
                    }
                    channel.note?.takeIf { it.isNotBlank() }?.let {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                        Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (channel.isAdmin && !channel.detached) {
                item {
                    TokenPanel(
                        title = stringResource(R.string.channel_trigger),
                        token = channel.triggerToken.orEmpty(),
                        primaryActionLabel = stringResource(R.string.channel_webhook),
                        primaryValue = "${BuildConfig.HOOK_BASE_URL}/t/${channel.triggerToken.orEmpty()}",
                        secondaryActionLabel = stringResource(R.string.channel_email),
                        secondaryValue = "${channel.triggerToken.orEmpty()}@${BuildConfig.EMAIL_TRIGGER_DOMAIN}",
                        onReset = { viewModel.resetTriggerToken(channel.remoteId) },
                        onCopy = { label, value ->
                            context.copyToClipboard(label, value)
                            viewModel.showMessage(R.string.copied_fmt, label)
                        },
                    )
                }
                item {
                    TokenPanel(
                        title = stringResource(R.string.channel_subscription_link),
                        token = channel.subscriptionToken.orEmpty(),
                        primaryActionLabel = stringResource(R.string.channel_link),
                        primaryValue = "https://echobell.one/subscription/${channel.subscriptionToken.orEmpty()}",
                        secondaryActionLabel = null,
                        secondaryValue = null,
                        onReset = { viewModel.resetSubscriptionToken(channel.remoteId) },
                        onCopy = { label, value ->
                            context.copyToClipboard(label, value)
                            viewModel.showMessage(R.string.copied_fmt, label)
                        },
                    )
                }
            }

            if (!channel.detached) {
                item {
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.channel_notification_section), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            if (channel.subscribedAt != null) {
                                TextButton(onClick = { unsubscribeDialog = true }) {
                                    Text(stringResource(R.string.action_unsubscribe), color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        if (channel.subscribedAt != null) {
                            NotificationTypeSelector(
                                selected = channel.notificationType ?: NotificationType.Active,
                                premiumActive = state.premiumActive,
                                onPaywall = { navController.navigate(Routes.Paywall) },
                                onSelected = { viewModel.updateNotificationType(channel.remoteId, it) },
                            )
                        } else if (channel.subscriptionToken != null) {
                            Text(stringResource(R.string.channel_subscribe_prompt))
                            Spacer(Modifier.height(10.dp))
                            Button(onClick = { viewModel.subscribeToChannel(channel.subscriptionToken, NotificationType.Active) }) {
                                Text(stringResource(R.string.action_subscribe))
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle(stringResource(R.string.channel_templates_section))
                    AppCard {
                        Text(channel.titleTemplate, fontWeight = FontWeight.SemiBold)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                        Text(channel.bodyTemplate, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (channel.isAdmin && !channel.conditions.isNullOrBlank()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionTitle(stringResource(R.string.channel_conditions_section))
                        AppCard { Text(channel.conditions.orEmpty()) }
                    }
                }
            }
            if (channel.isAdmin && !channel.externalLinkTemplate.isNullOrBlank()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionTitle(stringResource(R.string.channel_link_template_section))
                        AppCard { Text(channel.externalLinkTemplate.orEmpty()) }
                    }
                }
            }
            if (records.isNotEmpty()) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp)
                    ) {
                        SectionTitle(stringResource(R.string.channel_recent_records), Modifier.weight(1f))
                        TextButton(onClick = { viewModel.clearRecordsForChannel(channel.remoteId) }) {
                            Text(stringResource(R.string.delete_all), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                items(records.take(5), key = { it.id }) { record ->
                    RecordCard(
                        record = record,
                        channel = channel,
                        onMarkRead = { viewModel.markRecord(record.id, true) },
                        onMarkUnread = { viewModel.markRecord(record.id, false) },
                    )
                }
            }
            if (channel.isAdmin && !channel.detached) {
                item {
                    OutlinedButton(
                        onClick = { navController.navigate(Routes.subscribers(channel.remoteId)) },
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.channel_manage_subscribers))
                    }
                }
            }
            if (channel.isAdmin || channel.detached) {
                item {
                    OutlinedButton(
                        onClick = { deleteDialog = true },
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.channel_delete_title), color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (deleteDialog) {
        ConfirmDialog(
            title = stringResource(R.string.channel_delete_title),
            text = stringResource(R.string.action_cannot_be_undone),
            confirm = stringResource(R.string.action_delete),
            onDismiss = { deleteDialog = false },
            onConfirm = {
                deleteDialog = false
                viewModel.deleteChannel(channel) { navController.popBackStack() }
            },
        )
    }
    if (unsubscribeDialog) {
        ConfirmDialog(
            title = stringResource(R.string.action_unsubscribe),
            text = stringResource(R.string.channel_unsubscribe_message),
            confirm = stringResource(R.string.action_unsubscribe),
            onDismiss = { unsubscribeDialog = false },
            onConfirm = {
                unsubscribeDialog = false
                viewModel.unsubscribe(channel) { if (!channel.isAdmin) navController.popBackStack() }
            },
        )
    }
}

@Composable
private fun TokenPanel(
    title: String,
    token: String,
    primaryActionLabel: String,
    primaryValue: String,
    secondaryActionLabel: String?,
    secondaryValue: String?,
    onReset: () -> Unit,
    onCopy: (String, String) -> Unit,
) {
    var visible by rememberSaveable(title, token) { mutableStateOf(false) }
    AppCard {
        Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .clickable { visible = !visible }
                .padding(vertical = 10.dp, horizontal = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (visible) token else token.hiddenToken(),
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) stringResource(R.string.action_hide) else stringResource(R.string.action_show),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onReset,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.action_reset),
                    modifier = Modifier.size(16.dp)
                )
            }
            Button(
                onClick = { onCopy(primaryActionLabel, primaryValue) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(primaryActionLabel, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
            }
            if (secondaryActionLabel != null && secondaryValue != null) {
                Button(
                    onClick = { onCopy(secondaryActionLabel, secondaryValue) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(secondaryActionLabel, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ChannelFormScreen(
    state: AppUiState,
    viewModel: EchobellViewModel,
    navController: NavHostController,
    channel: Channel?,
) {
    var name by rememberSaveable(channel?.remoteId) { mutableStateOf(channel?.name.orEmpty()) }
    var colorHex by rememberSaveable(channel?.remoteId) { mutableStateOf(channel?.colorHex ?: DEFAULT_COLORS.first()) }
    var titleTemplate by rememberSaveable(channel?.remoteId) { mutableStateOf(channel?.titleTemplate.orEmpty()) }
    var bodyTemplate by rememberSaveable(channel?.remoteId) { mutableStateOf(channel?.bodyTemplate.orEmpty()) }
    var conditions by rememberSaveable(channel?.remoteId) { mutableStateOf(channel?.conditions.orEmpty()) }
    var externalLinkTemplate by rememberSaveable(channel?.remoteId) { mutableStateOf(channel?.externalLinkTemplate.orEmpty()) }
    var note by rememberSaveable(channel?.remoteId) { mutableStateOf(channel?.note.orEmpty()) }
    var autoSubscribe by rememberSaveable { mutableStateOf(channel == null) }
    var notificationType by rememberSaveable { mutableStateOf(NotificationType.Active) }
    val valid = name.isNotBlank() && bodyTemplate.isNotBlank()
    val focusManager = LocalFocusManager.current
    var hasAttemptedSave by rememberSaveable { mutableStateOf(false) }

    ScreenScaffold(
        title = if (channel == null) stringResource(R.string.channel_form_new_title) else stringResource(R.string.channel_form_edit_title),
        navigationIcon = { BackOrEmpty(navController) },
        actions = {
            IconButton(
                onClick = {
                    hasAttemptedSave = true
                    if (valid) {
                        focusManager.clearFocus()
                        if (channel == null) {
                            viewModel.createChannel(
                                name = name,
                                colorHex = colorHex,
                                titleTemplate = titleTemplate,
                                bodyTemplate = bodyTemplate,
                                conditions = conditions,
                                externalLinkTemplate = externalLinkTemplate,
                                note = note,
                                notificationType = if (autoSubscribe) notificationType else null,
                            ) { navController.popBackStack() }
                        } else {
                            viewModel.updateChannel(
                                channelId = channel.remoteId,
                                name = name,
                                colorHex = colorHex,
                                titleTemplate = titleTemplate,
                                bodyTemplate = bodyTemplate,
                                conditions = conditions,
                                externalLinkTemplate = externalLinkTemplate,
                                note = note,
                            ) { navController.popBackStack() }
                        }
                    }
                },
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = stringResource(R.string.action_save),
                    tint = if (valid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    focusManager.clearFocus()
                }
                .imePadding(),
            contentPadding = padding.plus(PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 40.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AppTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = stringResource(R.string.channel_form_name_label),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        isError = hasAttemptedSave && name.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (hasAttemptedSave && name.isBlank()) {
                        Text(stringResource(R.string.channel_form_name_required), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle(stringResource(R.string.channel_form_color))
                    AppCard {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DEFAULT_COLORS.forEach { color ->
                                Box(
                                    Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(parseColor(color))
                                        .clickable { colorHex = color },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (colorHex == color) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle(stringResource(R.string.channel_templates_section))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AppTextField(
                            value = titleTemplate,
                            onValueChange = { titleTemplate = it },
                            label = stringResource(R.string.channel_form_title_template),
                            placeholder = stringResource(R.string.channel_form_title_placeholder),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AppTextField(
                            value = bodyTemplate,
                            onValueChange = { bodyTemplate = it },
                            label = stringResource(R.string.channel_form_body_template),
                            minLines = 3,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            isError = hasAttemptedSave && bodyTemplate.isBlank(),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        if (hasAttemptedSave && bodyTemplate.isBlank()) {
                            Text(stringResource(R.string.channel_form_body_required), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle(stringResource(R.string.channel_form_advanced))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AppTextField(
                            value = conditions,
                            onValueChange = { conditions = it },
                            label = stringResource(R.string.channel_form_conditions),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth()
                        )
                        AppTextField(
                            value = externalLinkTemplate,
                            onValueChange = { externalLinkTemplate = it },
                            label = stringResource(R.string.channel_form_link_template),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth()
                        )
                        AppTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = stringResource(R.string.channel_form_note),
                            minLines = 2,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            if (channel == null) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionTitle(stringResource(R.string.channel_form_subscription))
                        AppCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(stringResource(R.string.channel_form_auto_subscribe))
                                    Text(stringResource(R.string.channel_form_auto_subscribe_description), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(checked = autoSubscribe, onCheckedChange = { autoSubscribe = it })
                            }
                            if (autoSubscribe) {
                                Spacer(Modifier.height(12.dp))
                                NotificationTypeSelector(
                                    selected = notificationType,
                                    premiumActive = state.premiumActive,
                                    onPaywall = { navController.navigate(Routes.Paywall) },
                                    onSelected = { notificationType = it },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubscribeScreen(
    state: AppUiState,
    viewModel: EchobellViewModel,
    navController: NavHostController,
    initialToken: String,
) {
    var input by rememberSaveable(initialToken) { mutableStateOf(initialToken) }
    var channelInfo by remember { mutableStateOf<ApiChannel?>(null) }
    var notificationType by rememberSaveable { mutableStateOf(NotificationType.Active) }
    val token = remember(input) { extractSubscriptionToken(input) }

    LaunchedEffect(initialToken) {
        extractSubscriptionToken(initialToken)?.let { subscriptionToken ->
            viewModel.fetchChannelBySubscriptionToken(subscriptionToken) { channelInfo = it }
        }
    }

    ScreenScaffold(
        title = stringResource(R.string.subscribe_title),
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AppCard {
                    if (channelInfo == null) {
                        Text(stringResource(R.string.subscribe_prompt), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        val isInvalidToken = remember(input, token) { input.isNotEmpty() && token == null }
                        AppTextField(
                            value = input,
                            onValueChange = { input = it; channelInfo = null },
                            label = stringResource(R.string.subscribe_link_label),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    token?.let { viewModel.fetchChannelBySubscriptionToken(it) { channelInfo = it } }
                                }
                            ),
                            isError = isInvalidToken,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (isInvalidToken) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                stringResource(R.string.subscribe_invalid_token),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                token?.let { viewModel.fetchChannelBySubscriptionToken(it) { channelInfo = it } }
                            },
                            enabled = token != null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(R.string.subscribe_get_info))
                        }
                    } else {
                        val info = channelInfo!!
                        ChannelPreview(info)
                        Spacer(Modifier.height(12.dp))
                        NotificationTypeSelector(
                            selected = notificationType,
                            premiumActive = state.premiumActive,
                            onPaywall = { navController.navigate(Routes.Paywall) },
                            onSelected = { notificationType = it },
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            enabled = token != null,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (state.canCreateOrSubscribeChannel) {
                                    viewModel.subscribeToChannel(token.orEmpty(), notificationType) { navController.popBackStack() }
                                } else {
                                    navController.navigate(Routes.Paywall)
                                }
                            },
                        ) {
                            Text(stringResource(R.string.action_subscribe))
                        }
                    }
                }
            }
            if (!state.canCreateOrSubscribeChannel) {
                item {
                    LimitBanner(pluralStringResource(R.plurals.channels_limit_banner, FREE_USER_CHANNEL_LIMIT, FREE_USER_CHANNEL_LIMIT)) {
                        navController.navigate(Routes.Paywall)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelPreview(channel: ApiChannel) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .padding(top = 3.dp)
                .size(width = 5.dp, height = 24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(parseColor(channel.color)),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row {
                Text(
                    channel.name.ifBlank { stringResource(R.string.channel_number_fmt, channel.id) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text("#${channel.id}", style = MaterialTheme.typography.labelSmall)
            }
            Text(channel.titleTemplate, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(channel.bodyTemplate, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DirectKeysScreen(state: AppUiState, viewModel: EchobellViewModel, navController: NavHostController) {
    val uriHandler = LocalUriHandler.current
    var showIntro by rememberSaveable { mutableStateOf(true) }
    var newKeyDialog by rememberSaveable { mutableStateOf(false) }
    var keyName by rememberSaveable { mutableStateOf("") }

    ScreenScaffold(
        title = stringResource(R.string.nav_direct),
        actions = {
            IconButton(onClick = { uriHandler.openUri("https://echobell.one/en/docs/direct") }) {
                Icon(Icons.AutoMirrored.Filled.Help, contentDescription = stringResource(R.string.channels_documentation))
            }
            IconButton(onClick = {
                if (state.canCreateDirectKey) newKeyDialog = true else navController.navigate(Routes.Paywall)
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.direct_new_key))
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (showIntro) {
                item {
                    AppCard(
                        modifier = Modifier.clickable { uriHandler.openUri("https://echobell.one/en/docs/direct") },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Default.Key,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(R.string.direct_intro_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(stringResource(R.string.direct_intro_description), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            }
                            IconButton(onClick = { showIntro = false }) {
                                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_close), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
            }
            if (state.directKeys.isEmpty()) {
                item {
                    EmptyState(Icons.Default.Key, stringResource(R.string.direct_empty_title), stringResource(R.string.direct_empty_description))
                }
            }
            items(state.directKeys, key = { it.remoteId }) { directKey ->
                DirectKeyCard(directKey, viewModel)
            }
            if (!state.canCreateDirectKey) {
                item {
                    LimitBanner(pluralStringResource(R.plurals.direct_limit_banner, FREE_USER_DIRECT_KEY_LIMIT, FREE_USER_DIRECT_KEY_LIMIT)) {
                        navController.navigate(Routes.Paywall)
                    }
                }
            }
        }
    }

    if (newKeyDialog) {
        AlertDialog(
            onDismissRequest = { newKeyDialog = false },
            title = { Text(stringResource(R.string.direct_new_key_title)) },
            text = {
                AppTextField(
                    value = keyName,
                    onValueChange = { keyName = it },
                    label = stringResource(R.string.direct_key_name_label),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (keyName.isNotBlank()) {
                                viewModel.createDirectKey(keyName)
                                keyName = ""
                                newKeyDialog = false
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    enabled = keyName.isNotBlank(),
                    onClick = {
                        viewModel.createDirectKey(keyName)
                        keyName = ""
                        newKeyDialog = false
                    },
                ) {
                    Text(stringResource(R.string.action_create))
                }
            },
            dismissButton = {
                TextButton(onClick = { newKeyDialog = false }) { Text(stringResource(R.string.action_cancel)) }
            },
        )
    }
}

@Composable
private fun DirectKeyCard(directKey: DirectKey, viewModel: EchobellViewModel) {
    val context = LocalContext.current
    var visible by rememberSaveable(directKey.remoteId, directKey.token) { mutableStateOf(false) }
    var deleteDialog by rememberSaveable { mutableStateOf(false) }
    var resetDialog by rememberSaveable { mutableStateOf(false) }
    var clearDialog by rememberSaveable { mutableStateOf(false) }
    val webhook = "${BuildConfig.HOOK_BASE_URL}/d/${directKey.token}"
    val webhookLabel = stringResource(R.string.channel_webhook)

    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(width = 6.dp, height = 24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary),
            )
            Spacer(Modifier.width(12.dp))
            Text(directKey.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(stringResource(R.string.direct_key_label), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .clickable { visible = !visible }
                .padding(vertical = 10.dp, horizontal = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (visible) directKey.token else directKey.token.hiddenToken(),
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) stringResource(R.string.action_hide) else stringResource(R.string.action_show),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedIconButton(
                onClick = { resetDialog = true },
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.action_reset))
            }
            Button(
                onClick = {
                    context.copyToClipboard(webhookLabel, webhook)
                    viewModel.showMessage(R.string.copied_fmt, webhookLabel)
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(webhookLabel, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
            }
            OutlinedIconButton(
                onClick = { clearDialog = true },
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Icon(Icons.Default.VisibilityOff, contentDescription = stringResource(R.string.direct_clear_records))
            }
            OutlinedIconButton(
                onClick = { deleteDialog = true },
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
            }
        }
    }

    if (resetDialog) {
        ConfirmDialog(
            stringResource(R.string.direct_reset_token_title),
            stringResource(R.string.direct_reset_token_message),
            stringResource(R.string.action_reset),
            { resetDialog = false },
        ) {
            resetDialog = false
            viewModel.resetDirectKeyToken(directKey.remoteId)
        }
    }
    if (clearDialog) {
        ConfirmDialog(
            stringResource(R.string.direct_clear_records_title),
            stringResource(R.string.direct_clear_records_message),
            stringResource(R.string.direct_clear_records_title),
            { clearDialog = false },
        ) {
            clearDialog = false
            viewModel.clearRecordsForDirectKey(directKey.remoteId)
        }
    }
    if (deleteDialog) {
        ConfirmDialog(
            stringResource(R.string.direct_delete_title),
            stringResource(R.string.action_cannot_be_undone),
            stringResource(R.string.action_delete),
            { deleteDialog = false },
        ) {
            deleteDialog = false
            viewModel.deleteDirectKey(directKey.remoteId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    state: AppUiState,
    viewModel: EchobellViewModel,
    navController: NavHostController,
    requestNotificationPermission: () -> Unit,
    requestFullScreenIntentPermission: () -> Unit,
) {
    val context = LocalContext.current
    ScreenScaffold(
        title = stringResource(R.string.nav_settings),
        actions = {
            IconButton(onClick = { navController.navigate(Routes.Announcements) }) {
                Icon(Icons.Default.Campaign, contentDescription = stringResource(R.string.announcements_title))
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { SubscriptionBanner(state, navController) }
            if (!state.notificationAuthorized) {
                item { PermissionBanner(requestNotificationPermission) }
            } else if (!state.fullScreenIntentAuthorized) {
                item { FullScreenIntentBanner(requestFullScreenIntentPermission) }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle(stringResource(R.string.settings_account))
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.settings_hello_fmt, state.user?.name ?: stringResource(R.string.settings_default_user)),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(state.user?.email ?: "#${state.user?.id ?: 0}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { navController.navigate(Routes.User) }) {
                                Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_user_settings))
                            }
                        }
                        Text(stringResource(R.string.settings_data_note), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item {
                SettingsAction(Icons.Default.Info, stringResource(R.string.settings_user_guide)) {
                    context.openUrl("https://echobell.one/docs")
                }
            }
            item {
                SettingsAction(
                    Icons.Default.Person,
                    stringResource(R.string.settings_invite_friends),
                    trailing = if (state.user?.canSubmitInviteCode() == true) stringResource(R.string.settings_bonus_available) else null,
                ) {
                    navController.navigate(Routes.Invite)
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle(stringResource(R.string.settings_support))
                    AppCard {
                        SupportLink(stringResource(R.string.support_email), "mailto:echobell@weelone.com")
                        SupportLink(stringResource(R.string.support_x), "https://x.com/EchobellApp")
                        SupportLink(stringResource(R.string.support_discord), "https://discord.gg/s4JqfrgccJ")
                        SupportLink(stringResource(R.string.support_telegram), "https://t.me/EchobellApp")
                        SupportLink(stringResource(R.string.support_privacy), "https://echobell.one/privacy")
                        SupportLink(stringResource(R.string.support_terms), "https://echobell.one/terms")
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionBanner(state: AppUiState, navController: NavHostController) {
    BrandPanel(
        modifier = Modifier.clickable { navController.navigate(Routes.Paywall) },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.padding(top = 2.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                if (state.premiumActive) {
                    Text(stringResource(R.string.premium_active), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.premium_until_fmt, formatDate(state.user?.premiumExpiresAt ?: 0)))
                } else {
                    Text(stringResource(R.string.premium_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.premium_pitch))
                }
            }
        }
    }
}

@Composable
private fun SettingsAction(icon: ImageVector, title: String, trailing: String? = null, onClick: () -> Unit) {
    AppCard(Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(title, modifier = Modifier.weight(1f))
            if (trailing != null) {
                AssistChip(
                    onClick = {},
                    label = { Text(trailing) },
                    border = null,
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun SupportLink(label: String, url: String) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(url) }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserSettingsScreen(state: AppUiState, viewModel: EchobellViewModel, navController: NavHostController) {
    var renameDialog by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable(state.user?.name) { mutableStateOf(state.user?.name.orEmpty()) }
    var signOutDialog by rememberSaveable { mutableStateOf(false) }
    var deleteDialog by rememberSaveable { mutableStateOf(false) }
    val hasManagedChannels = state.channels.any { it.isAdmin && !it.detached }

    ScreenScaffold(
        title = stringResource(R.string.user_settings_title),
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AppCard {
                    Text(
                        stringResource(R.string.user_welcome_fmt, state.user?.name ?: stringResource(R.string.settings_default_user)),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(12.dp))
                    InfoRow(stringResource(R.string.user_id), "#${state.user?.id ?: 0}")
                    state.user?.email?.let { InfoRow(stringResource(R.string.user_email), it) }
                    state.user?.premiumExpiresAt?.takeIf { state.premiumActive }?.let { InfoRow(stringResource(R.string.user_premium_until), formatDate(it)) }
                }
            }
            item {
                OutlinedButton(
                    onClick = { renameDialog = true },
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.user_edit_name))
                }
            }
            item {
                OutlinedButton(
                    onClick = { signOutDialog = true },
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.user_sign_out), color = MaterialTheme.colorScheme.error)
                }
            }
            item {
                OutlinedButton(
                    enabled = !hasManagedChannels,
                    onClick = { deleteDialog = true },
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.error.copy(alpha = if (hasManagedChannels) 0.15f else 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.user_delete_account), color = MaterialTheme.colorScheme.error)
                }
                if (hasManagedChannels) {
                    Text(stringResource(R.string.user_delete_blocked), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (renameDialog) {
        AlertDialog(
            onDismissRequest = { renameDialog = false },
            title = { Text(stringResource(R.string.user_edit_name)) },
            text = { AppTextField(value = name, onValueChange = { name = it }, label = stringResource(R.string.user_new_name), modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(onClick = {
                    renameDialog = false
                    viewModel.rename(name)
                }) { Text(stringResource(R.string.action_save)) }
            },
            dismissButton = { TextButton(onClick = { renameDialog = false }) { Text(stringResource(R.string.action_cancel)) } },
        )
    }
    if (signOutDialog) {
        ConfirmDialog(
            stringResource(R.string.user_sign_out),
            stringResource(R.string.user_sign_out_message),
            stringResource(R.string.user_sign_out),
            { signOutDialog = false },
        ) {
            signOutDialog = false
            viewModel.signOut()
        }
    }
    if (deleteDialog) {
        ConfirmDialog(
            stringResource(R.string.user_delete_account),
            stringResource(R.string.user_delete_message),
            stringResource(R.string.user_request_deletion),
            { deleteDialog = false },
        ) {
            deleteDialog = false
            viewModel.requestAccountDeletion()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InviteScreen(state: AppUiState, viewModel: EchobellViewModel, navController: NavHostController) {
    var submitDialog by rememberSaveable { mutableStateOf(false) }
    var redeemDialog by rememberSaveable { mutableStateOf(false) }
    var code by rememberSaveable { mutableStateOf("") }
    val user = state.user
    val context = LocalContext.current

    ScreenScaffold(
        title = stringResource(R.string.settings_invite_friends),
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AppCard {
                    Text(stringResource(R.string.invite_points_balance), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(Modifier.width(8.dp))
                        Text("${user?.pointsBalance ?: 0}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.invite_points))
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { redeemDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.invite_redeem))
                    }
                }
            }
            item {
                AppCard {
                    Text(stringResource(R.string.invite_your_code), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (user?.inviteCode.isNullOrBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = { viewModel.generateInviteCode() }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.invite_generate))
                        }
                    } else {
                        val clipLabel = stringResource(R.string.invite_code_clip_label)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(user!!.inviteCode!!, style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                context.copyToClipboard(clipLabel, user.inviteCode)
                                viewModel.showMessage(R.string.invite_code_copied)
                            }) { Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.action_copy)) }
                        }
                        val shareMessage = stringResource(R.string.invite_share_message_fmt, user.inviteCode)
                        Button(onClick = { context.shareText(shareMessage) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.invite_share_code))
                        }
                    }
                }
            }
            if (user?.canSubmitInviteCode() == true) {
                item {
                    AppCard {
                        Text(stringResource(R.string.invite_have_code), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(stringResource(R.string.invite_enter_code_hint))
                        Spacer(Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { submitDialog = true },
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.invite_enter_code))
                        }
                    }
                }
            }
            item {
                AppCard {
                    Text(stringResource(R.string.invite_rewards), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    InfoRow(stringResource(R.string.invite_reward_signup), stringResource(R.string.invite_reward_signup_value))
                    InfoRow(stringResource(R.string.invite_reward_monthly), stringResource(R.string.invite_reward_monthly_value))
                    InfoRow(stringResource(R.string.invite_reward_annual), stringResource(R.string.invite_reward_annual_value))
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                    InfoRow(stringResource(R.string.invite_redeem_month), stringResource(R.string.invite_redeem_month_value))
                    InfoRow(stringResource(R.string.invite_redeem_year), stringResource(R.string.invite_redeem_year_value))
                }
            }
        }
    }

    if (submitDialog) {
        AlertDialog(
            onDismissRequest = { submitDialog = false },
            title = { Text(stringResource(R.string.invite_enter_code)) },
            text = { AppTextField(value = code, onValueChange = { code = it.uppercase(Locale.US) }, label = stringResource(R.string.invite_code_label), modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(enabled = code.isNotBlank(), onClick = {
                    submitDialog = false
                    viewModel.submitInviteCode(code)
                    code = ""
                }) { Text(stringResource(R.string.action_submit)) }
            },
            dismissButton = { TextButton(onClick = { submitDialog = false }) { Text(stringResource(R.string.action_cancel)) } },
        )
    }
    if (redeemDialog) {
        RedeemDialog(
            balance = user?.pointsBalance ?: 0,
            onDismiss = { redeemDialog = false },
            onRedeem = {
                redeemDialog = false
                viewModel.redeemPoints(it)
            },
        )
    }
}

@Composable
private fun RedeemDialog(balance: Int, onDismiss: () -> Unit, onRedeem: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.invite_redeem)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(pluralStringResource(R.plurals.redeem_balance, balance, balance))
                OutlinedButton(enabled = balance >= 200, onClick = { onRedeem("month") }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.redeem_month_option))
                }
                OutlinedButton(enabled = balance >= 2000, onClick = { onRedeem("year") }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.redeem_year_option))
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaywallScreen(state: AppUiState, viewModel: EchobellViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? Activity
    val obfuscatedAccountId = state.user?.id?.toString()
    val premiumActive by rememberUpdatedState(state.premiumActive)
    val billingManager = remember(obfuscatedAccountId) {
        GoogleBillingManager(
            context = context,
            obfuscatedAccountId = obfuscatedAccountId,
            onPurchaseReady = { productId, purchaseToken, acknowledge, finishProcessing ->
                viewModel.reportGoogleSubscription(
                    productId = productId,
                    purchaseToken = purchaseToken,
                    onVerified = {
                        acknowledge { acknowledged ->
                            finishProcessing(acknowledged)
                        }
                    },
                    onFailure = {
                        finishProcessing(false)
                    },
                    showSuccessMessage = !premiumActive,
                )
            },
            onMessage = viewModel::showMessage,
        )
    }
    val billingProducts by billingManager.products
    val billingLoading by billingManager.loading
    var selectedProduct by remember { mutableStateOf<BillingProduct?>(null) }

    LaunchedEffect(premiumActive) {
        if (!premiumActive) {
            billingManager.start()
        }
    }
    DisposableEffect(billingManager, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && !premiumActive) {
                billingManager.restorePurchases()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            billingManager.dispose()
        }
    }
    LaunchedEffect(billingProducts) {
        if (selectedProduct == null || billingProducts.none { it.productId == selectedProduct?.productId }) {
            selectedProduct = billingProducts.firstOrNull { it.productId.endsWith("annual") } ?: billingProducts.firstOrNull()
        }
    }

    ScreenScaffold(
        title = stringResource(R.string.premium_title),
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                BrandPanel {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(42.dp))
                    Spacer(Modifier.height(8.dp))
                    if (state.premiumActive) {
                        Text(stringResource(R.string.paywall_member_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.paywall_member_until_fmt, formatDate(state.user?.premiumExpiresAt ?: 0)))
                    } else {
                        Text(stringResource(R.string.paywall_unlock_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.paywall_unlock_description))
                    }
                }
            }
            item {
                AppCard {
                    FeatureRow(Icons.Default.Phone, stringResource(R.string.paywall_feature_calls_title), stringResource(R.string.paywall_feature_calls_description))
                    FeatureRow(Icons.Default.Notifications, stringResource(R.string.paywall_feature_channels_title), pluralStringResource(R.plurals.channels_limit_banner, FREE_USER_CHANNEL_LIMIT, FREE_USER_CHANNEL_LIMIT))
                    FeatureRow(Icons.Default.Key, stringResource(R.string.paywall_feature_keys_title), pluralStringResource(R.plurals.direct_limit_banner, FREE_USER_DIRECT_KEY_LIMIT, FREE_USER_DIRECT_KEY_LIMIT))
                    FeatureRow(Icons.Default.Star, stringResource(R.string.paywall_feature_support_title), stringResource(R.string.paywall_feature_support_description))
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle(stringResource(R.string.paywall_google_play))
                    AppCard {
                        when {
                            state.premiumActive -> Text(stringResource(R.string.paywall_premium_active))
                            billingLoading -> CircularProgressIndicator()
                            billingProducts.isEmpty() -> {
                                Text(stringResource(R.string.paywall_products_unavailable))
                                Spacer(Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = { billingManager.start() },
                                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.action_retry))
                                }
                            }
                            else -> {
                                billingProducts.forEach { product ->
                                    BillingProductRow(
                                        product = product,
                                        selected = selectedProduct?.productId == product.productId,
                                        onClick = { selectedProduct = product },
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                Button(
                                    enabled = activity != null && selectedProduct != null,
                                    onClick = { selectedProduct?.let { product -> activity?.let { billingManager.launchPurchase(it, product) } } },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        if (selectedProduct?.trialPeriod.isNullOrBlank()) stringResource(R.string.action_subscribe)
                                        else stringResource(R.string.paywall_start_trial)
                                    )
                                }
                                TextButton(onClick = { billingManager.restorePurchases(userInitiated = true) }, modifier = Modifier.fillMaxWidth()) {
                                    Text(stringResource(R.string.paywall_restore))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    TextButton(onClick = { context.openUrl("https://echobell.one/terms") }) {
                                        Text(stringResource(R.string.paywall_terms))
                                    }
                                    Text("|", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    TextButton(onClick = { context.openUrl("https://echobell.one/privacy") }) {
                                        Text(stringResource(R.string.paywall_privacy))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                Button(onClick = { navController.navigate(Routes.Invite) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.paywall_redeem_points))
                }
            }
        }
    }
}

@Composable
private fun BillingProductRow(product: BillingProduct, selected: Boolean, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier.padding(vertical = 4.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(product.title, fontWeight = FontWeight.SemiBold)
                Text(product.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (product.trialPeriod.isNotBlank()) {
                    Text(product.trialPeriod, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(product.price, fontWeight = FontWeight.SemiBold)
                if (product.period.isNotBlank()) {
                    Text(product.period, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, title: String, description: String) {
    Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnnouncementsScreen(state: AppUiState, viewModel: EchobellViewModel, navController: NavHostController) {
    ScreenScaffold(
        title = stringResource(R.string.announcements_title),
        navigationIcon = { BackOrEmpty(navController) },
        actions = {
            IconButton(onClick = { viewModel.refreshAnnouncements() }) {
                Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.action_refresh))
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (state.announcements.isEmpty()) {
                item { EmptyState(Icons.Default.Campaign, stringResource(R.string.announcements_empty_title), stringResource(R.string.announcements_empty_description)) }
            }
            items(state.announcements, key = { it.id }) { announcement ->
                AnnouncementRow(announcement, announcement.id in state.readAnnouncementIds) {
                    navController.navigate(Routes.announcement(announcement.id))
                }
            }
        }
    }
}

@Composable
private fun AnnouncementRow(announcement: Announcement, read: Boolean, onClick: () -> Unit) {
    val levelColors = announcement.level.colors(read = read)
    AppCard(Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(levelColors.accent),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    announcement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (read) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(announcement.level.label(), style = MaterialTheme.typography.labelSmall, color = levelColors.accent)
                    Text(formatDate(announcement.updatedAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnnouncementDetailScreen(announcement: Announcement, viewModel: EchobellViewModel, navController: NavHostController) {
    val context = LocalContext.current
    LaunchedEffect(announcement.id) {
        viewModel.markAnnouncementRead(announcement.id)
    }
    ScreenScaffold(
        title = stringResource(R.string.announcement_title),
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                val levelColors = announcement.level.colors()
                AppCard {
                    Text(
                        announcement.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = {},
                            label = { Text(announcement.level.label()) },
                            border = null,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                labelColor = levelColors.accent
                            )
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(formatDate(announcement.updatedAt)) },
                            border = null,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    announcement.imageUrl?.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(12.dp))
                        AsyncImage(model = it, contentDescription = null, modifier = Modifier.fillMaxWidth())
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(announcement.content)
                }
            }
            if (!announcement.ctaLabel.isNullOrBlank() && !announcement.ctaUrl.isNullOrBlank()) {
                item {
                    Button(onClick = { context.openUrl(announcement.ctaUrl) }, modifier = Modifier.fillMaxWidth()) {
                        Text(announcement.ctaLabel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubscribersScreen(viewModel: EchobellViewModel, navController: NavHostController, channel: Channel) {
    var subscribers by remember { mutableStateOf<List<ApiChannelSubscriber>?>(null) }
    LaunchedEffect(channel.remoteId) {
        viewModel.loadSubscribers(channel.remoteId) { subscribers = it }
    }
    ScreenScaffold(
        title = stringResource(R.string.subscribers_title),
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            when {
                subscribers == null -> item {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
                subscribers!!.isEmpty() -> item {
                    EmptyState(Icons.Default.Person, stringResource(R.string.subscribers_empty_title), stringResource(R.string.subscribers_empty_description))
                }
                else -> items(subscribers!!, key = { it.id }) { subscriber ->
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(subscriber.name, fontWeight = FontWeight.SemiBold)
                                Text("#${subscriber.id}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = {
                                viewModel.removeSubscriber(channel.remoteId, subscriber.id) {
                                    subscribers = subscribers?.filterNot { it.id == subscriber.id }
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_remove), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MissingScreen(message: String, navController: NavHostController) {
    ScreenScaffold(
        title = stringResource(R.string.missing_title),
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text(message)
        }
    }
}

@Composable
private fun ScreenScaffold(
    title: String,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            CompactTopBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
            )
        },
        content = content,
    )
}

@Composable
private fun CompactTopBar(
    title: String,
    navigationIcon: (@Composable () -> Unit)?,
    actions: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .heightIn(min = 52.dp)
            .padding(start = if (navigationIcon == null) 20.dp else 4.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (navigationIcon == null) {
            Icon(
                painter = painterResource(R.drawable.ic_echobell_mark),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp),
            )
            Spacer(Modifier.width(10.dp))
        } else {
            navigationIcon()
        }
        Text(
            title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            actions()
        }
    }
}

@Composable
private fun BackOrEmpty(navController: NavHostController) {
    IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
    }
}

@Composable
private fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    minLines: Int = 1,
    isError: Boolean = false,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { hint -> { Text(hint) } },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        minLines = if (singleLine) 1 else minLines,
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier,
    )
}

@Composable
private fun AppCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = colors,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun BrandPanel(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(Modifier.fillMaxWidth()) {
            Icon(
                painter = painterResource(R.drawable.ic_echobell_mark),
                contentDescription = null,
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = (-50).dp, y = 15.dp)
                    .graphicsLayer(alpha = 0.16f),
                tint = contentColor,
            )
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), content = content)
        }
    }
}

@Composable
private fun EmptyState(icon: ImageVector, title: String, description: String) {
    AppCard {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(42.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PermissionBanner(requestNotificationPermission: () -> Unit) {
    AppCard(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.notif_permission_title), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(stringResource(R.string.notif_permission_description), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = requestNotificationPermission,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(stringResource(R.string.notif_permission_allow))
        }
    }
}

@Composable
private fun FullScreenIntentBanner(requestFullScreenIntentPermission: () -> Unit) {
    AppCard(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                Icons.Default.Phone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.fsi_title), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text(stringResource(R.string.fsi_description), color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = requestFullScreenIntentPermission,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text(stringResource(R.string.fsi_enable))
        }
    }
}

@Composable
private fun LimitBanner(text: String, onUpgrade: () -> Unit) {
    AppCard(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onPrimaryContainer)
            TextButton(
                onClick = onUpgrade,
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.action_upgrade))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .padding(top = 10.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value)
    }
}

@StringRes
private fun NotificationType.labelRes(): Int = when (this) {
    NotificationType.Active -> R.string.notification_type_active
    NotificationType.TimeSensitive -> R.string.notification_type_time_sensitive
    NotificationType.Calling -> R.string.notification_type_calling
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NotificationTypeSelector(
    selected: NotificationType,
    premiumActive: Boolean,
    onPaywall: () -> Unit,
    onSelected: (NotificationType) -> Unit,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        NotificationType.entries.forEach { type ->
            FilterChip(
                selected = selected == type,
                onClick = {
                    if (type == NotificationType.Calling && !premiumActive) onPaywall() else onSelected(type)
                },
                label = { Text(stringResource(type.labelRes()), style = MaterialTheme.typography.labelMedium) },
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                leadingIcon = {
                    Icon(
                        imageVector = when (type) {
                            NotificationType.Active -> Icons.Default.Notifications
                            NotificationType.TimeSensitive -> Icons.Default.Info
                            NotificationType.Calling -> Icons.Default.Phone
                        },
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
            )
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    text: String,
    confirm: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirm, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
    )
}

@Composable
private fun AnnouncementPreview(announcement: Announcement, onClick: () -> Unit) {
    val levelColors = announcement.level.colors()
    AppCard(
        Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = levelColors.container,
            contentColor = levelColors.content,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Campaign, contentDescription = null, tint = levelColors.accent)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    announcement.title,
                    fontWeight = FontWeight.SemiBold,
                    color = levelColors.content,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(stringResource(R.string.announcement_tap_to_view), style = MaterialTheme.typography.bodySmall, color = levelColors.mutedContent)
            }
        }
    }
}

private val DEFAULT_COLORS = listOf("#F97316", "#F59E0B", "#2563EB", "#0F766E", "#BE123C", "#7C3AED", "#15803D")

private fun parseColor(hex: String): Color = runCatching {
    Color(hex.ifBlank { "#F97316" }.toColorInt())
}.getOrDefault(EchobellOrange)

private fun String.hiddenToken(): String =
    if (length <= 10) "*****" else "${take(5)}...${takeLast(5)}"

private fun String.urlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.toString())

@Composable
private fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(layoutDirection) + other.calculateStartPadding(layoutDirection),
        top = calculateTopPadding() + other.calculateTopPadding(),
        end = calculateEndPadding(layoutDirection) + other.calculateEndPadding(layoutDirection),
        bottom = calculateBottomPadding() + other.calculateBottomPadding(),
    )
}

private fun levelPriority(level: AnnouncementLevel): Int = when (level) {
    AnnouncementLevel.Critical -> 3
    AnnouncementLevel.Warning -> 2
    AnnouncementLevel.Info, AnnouncementLevel.Unknown -> 1
}

@Composable
private fun AnnouncementLevel.label(): String = when (this) {
    AnnouncementLevel.Info -> stringResource(R.string.level_info)
    AnnouncementLevel.Warning -> stringResource(R.string.level_warning)
    AnnouncementLevel.Critical -> stringResource(R.string.level_critical)
    AnnouncementLevel.Unknown -> stringResource(R.string.level_info)
}

private data class AnnouncementLevelColors(
    val container: Color,
    val content: Color,
    val mutedContent: Color,
    val accent: Color,
)

@Composable
private fun AnnouncementLevel.colors(read: Boolean = false): AnnouncementLevelColors {
    val dark = isSystemInDarkTheme()
    val colors = when (this) {
        AnnouncementLevel.Info,
        AnnouncementLevel.Unknown -> if (dark) {
            AnnouncementLevelColors(
                container = Color(0xFF16375F),
                content = Color(0xFFEAF2FF),
                mutedContent = Color(0xFFC4D8F7),
                accent = Color(0xFF93C5FD),
            )
        } else {
            AnnouncementLevelColors(
                container = Color(0xFFD7E8FF),
                content = Color(0xFF0F2A4D),
                mutedContent = Color(0xFF315A88),
                accent = Color(0xFF1D4ED8),
            )
        }
        AnnouncementLevel.Warning -> if (dark) {
            AnnouncementLevelColors(
                container = Color(0xFF4A2F09),
                content = Color(0xFFFFF0D1),
                mutedContent = Color(0xFFFFD991),
                accent = Color(0xFFFBBF24),
            )
        } else {
            AnnouncementLevelColors(
                container = Color(0xFFFFDF9A),
                content = Color(0xFF3B2500),
                mutedContent = Color(0xFF6D4A10),
                accent = Color(0xFFB45309),
            )
        }
        AnnouncementLevel.Critical -> if (dark) {
            AnnouncementLevelColors(
                container = Color(0xFF4D1717),
                content = Color(0xFFFFE7E7),
                mutedContent = Color(0xFFFFC5C5),
                accent = Color(0xFFFCA5A5),
            )
        } else {
            AnnouncementLevelColors(
                container = Color(0xFFFFCACA),
                content = Color(0xFF450A0A),
                mutedContent = Color(0xFF7F1D1D),
                accent = Color(0xFFB91C1C),
            )
        }
    }

    if (!read) return colors

    return colors.copy(
        container = colors.container.copy(alpha = if (dark) 0.58f else 0.68f),
        content = colors.content.copy(alpha = 0.78f),
        mutedContent = colors.mutedContent.copy(alpha = 0.76f),
        accent = colors.accent.copy(alpha = if (dark) 0.72f else 0.68f),
    )
}

private fun dayKey(epoch: Long): String =
    Instant.ofEpochSecond(epoch).atZone(ZoneId.systemDefault()).toLocalDate()
        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()))

private fun formatTime(epoch: Long): String =
    Instant.ofEpochSecond(epoch).atZone(ZoneId.systemDefault()).toLocalTime()
        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault()))

private fun formatDate(epoch: Long): String =
    Instant.ofEpochSecond(epoch).atZone(ZoneId.systemDefault()).toLocalDate()
        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()))

private fun formatDateTime(epoch: Long): String =
    Instant.ofEpochSecond(epoch).atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(Locale.getDefault()))

private fun Context.copyToClipboard(label: String, text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
}

private fun Context.shareText(text: String) {
    startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            getString(R.string.action_share),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}

private fun Context.openUrl(url: String?) {
    if (url.isNullOrBlank()) return
    startActivity(Intent(Intent.ACTION_VIEW, url.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}
