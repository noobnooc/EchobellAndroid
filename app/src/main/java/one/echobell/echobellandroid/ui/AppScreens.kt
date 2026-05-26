package one.echobell.echobellandroid.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.app.Activity
import android.app.NotificationManager
import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchobellApp(
    state: AppUiState,
    viewModel: EchobellViewModel,
    pendingSubscribeToken: String?,
    onSubscribeTokenConsumed: (String) -> Unit,
    requestNotificationPermission: () -> Unit,
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { BottomNav(navController) },
        floatingActionButton = {
            val route = navController.currentBackStackEntryAsState().value?.destination?.route
            when (route) {
                Routes.Records -> FloatingActionButton(
                    onClick = { navController.navigate(Routes.Channels) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Channels")
                }
                Routes.Channels -> FloatingActionButton(
                    onClick = {
                        if (state.canCreateOrSubscribeChannel) navController.navigate(Routes.ChannelNew) else navController.navigate(Routes.Paywall)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New channel")
                }
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            NavHost(navController = navController, startDestination = Routes.Records) {
                composable(Routes.Records) {
                    RecordsScreen(state, viewModel, navController, requestNotificationPermission)
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
                    if (channel == null) MissingScreen("Channel not found", navController) else ChannelDetailScreen(state, viewModel, navController, channel)
                }
                composable(
                    Routes.ChannelEdit,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { entry ->
                    val channel = state.channels.firstOrNull { it.remoteId == entry.arguments?.getInt("id") }
                    if (channel == null) MissingScreen("Channel not found", navController) else ChannelFormScreen(state, viewModel, navController, channel)
                }
                composable(
                    Routes.Subscribers,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { entry ->
                    val channel = state.channels.firstOrNull { it.remoteId == entry.arguments?.getInt("id") }
                    if (channel == null) MissingScreen("Channel not found", navController) else SubscribersScreen(viewModel, navController, channel)
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
                    SettingsScreen(state, viewModel, navController, requestNotificationPermission)
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
                    if (announcement == null) MissingScreen("Announcement not found", navController) else AnnouncementDetailScreen(announcement, viewModel, navController)
                }
            }
        }
    }
}

@Composable
private fun AuthScreen(state: AppUiState, viewModel: EchobellViewModel, snackbarHostState: SnackbarHostState) {
    var email by rememberSaveable { mutableStateOf("") }
    var code by rememberSaveable { mutableStateOf("") }
    var codeSent by rememberSaveable { mutableStateOf(false) }
    val emailValid = remember(email) { Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$").matches(email.trim()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Spacer(Modifier.height(28.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_echobell_mark),
                            contentDescription = null,
                            modifier = Modifier.size(38.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Column {
                        Text("Echobell", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Text("Webhook alerts, direct keys, and call notifications.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item {
                AppCard {
                    if (!codeSent) {
                        Text("Continue with Email", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email address") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.sendVerificationCode(email) { codeSent = true } },
                            enabled = emailValid && !state.busy,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Send Code")
                        }
                    } else {
                        Text("Enter Verification Code", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Text("A 6-digit code was sent to $email.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it.filter(Char::isDigit).take(6) },
                            label = { Text("Verification code") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.signIn(email, code) },
                            enabled = code.length == 6 && !state.busy,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Sign In")
                        }
                        TextButton(onClick = { codeSent = false; code = "" }) {
                            Text("Use another email")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNav(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        listOf(
            Triple(Routes.Records, Icons.Default.Notifications, "Records"),
            Triple(Routes.Channels, Icons.Default.Link, "Channels"),
            Triple(Routes.Direct, Icons.Default.Key, "Direct"),
            Triple(Routes.Settings, Icons.Default.Settings, "Settings"),
        ).forEach { (route, icon, label) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(Routes.Records)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordsScreen(
    state: AppUiState,
    viewModel: EchobellViewModel,
    navController: NavHostController,
    requestNotificationPermission: () -> Unit,
) {
    ScreenScaffold(
        title = "Echobell",
        actions = {
            IconButton(onClick = { navController.navigate(Routes.Announcements) }) {
                Icon(Icons.Default.Campaign, contentDescription = "Announcements")
            }
            IconButton(onClick = { navController.navigate(Routes.Settings) }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        },
    ) { padding ->
        val grouped = state.records
            .sortedByDescending { it.createdAt }
            .groupBy { dayKey(it.createdAt) }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            val latestUnread = state.announcements
                .filterNot { it.id in state.readAnnouncementIds }
                .maxWithOrNull(compareBy<Announcement> { levelPriority(it.level) }.thenBy { maxOf(it.startsAt, it.updatedAt) })
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
            }
            if (state.records.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Notifications,
                        title = "No Records Yet",
                        description = "Notifications you receive through channels or direct webhooks will appear here.",
                    )
                }
            }
            grouped.forEach { (day, records) ->
                item {
                    Text(day, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
                items(records, key = { it.id }) { record ->
                    RecordCard(
                        record = record,
                        channel = state.channels.firstOrNull { it.remoteId == record.channelId },
                        onChannelClick = { channel -> navController.navigate(Routes.channel(channel.remoteId)) },
                        onToggleRead = { viewModel.markRecord(record.id, !record.checked) },
                        onDelete = { viewModel.deleteRecord(record.id) },
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
    onChannelClick: (Channel) -> Unit,
    onToggleRead: () -> Unit,
    onDelete: () -> Unit,
) {
    var expanded by rememberSaveable(record.id) { mutableStateOf(false) }
    val context = LocalContext.current
    AppCard(
        modifier = Modifier.clickable {
            expanded = !expanded
            if (!record.checked) onToggleRead()
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
                channel?.name ?: record.directKeyName ?: "Notification",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
            )
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
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (expanded) {
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (channel != null) {
                    TextButton(onClick = { onChannelClick(channel) }) {
                        Icon(Icons.Default.Link, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Channel")
                    }
                }
                record.externalLink?.takeIf { it.isNotBlank() }?.let { link ->
                    TextButton(onClick = { context.openUrl(link) }) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Open")
                    }
                }
                TextButton(onClick = { context.shareText("${record.title}\n\n${record.body}") }) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Share")
                }
                TextButton(onClick = onToggleRead) {
                    Icon(if (record.checked) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(if (record.checked) "Unread" else "Read")
                }
                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChannelsScreen(state: AppUiState, viewModel: EchobellViewModel, navController: NavHostController) {
    ScreenScaffold(
        title = "Channels",
        navigationIcon = { BackOrEmpty(navController) },
        actions = {
            IconButton(onClick = { viewModel.syncChannels() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Sync")
            }
            IconButton(onClick = {
                if (state.canCreateOrSubscribeChannel) navController.navigate(Routes.subscribe()) else navController.navigate(Routes.Paywall)
            }) {
                Icon(Icons.Default.Link, contentDescription = "Subscribe")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AppCard(
                    modifier = Modifier.clickable { navController.navigate(Routes.Direct) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Direct", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text("Send notifications via webhook without channel setup.")
                        }
                    }
                }
            }
            if (state.channels.isEmpty()) {
                item {
                    EmptyState(Icons.Default.Link, "No Channels", "Create or subscribe to a channel to start receiving notifications.")
                }
            }
            items(state.channels, key = { it.remoteId }) { channel ->
                ChannelCard(channel) { navController.navigate(Routes.channel(channel.remoteId)) }
            }
            if (!state.canCreateOrSubscribeChannel) {
                item {
                    LimitBanner("Free users can keep up to $FREE_USER_CHANNEL_LIMIT active channels.") {
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
                    Text(channel.lastTriggeredAt?.let { "Last: ${formatDateTime(it)}" } ?: "Never triggered", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.weight(1f))
                    StatusChip(channel)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(channel: Channel) {
    val label = when {
        channel.detached -> "Inactive"
        channel.isAdmin -> "Managed"
        channel.subscribedAt != null -> "Subscribed"
        else -> "Available"
    }
    AssistChip(onClick = {}, label = { Text(label) })
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
    val records = state.records.filter { it.channelId == channel.remoteId }.sortedByDescending { it.createdAt }
    var deleteDialog by rememberSaveable { mutableStateOf(false) }
    var unsubscribeDialog by rememberSaveable { mutableStateOf(false) }

    ScreenScaffold(
        title = channel.name,
        navigationIcon = { BackOrEmpty(navController) },
        actions = {
            if (channel.isAdmin && !channel.detached) {
                IconButton(onClick = { navController.navigate(Routes.editChannel(channel.remoteId)) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
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
                        HorizontalDivider(Modifier.padding(vertical = 12.dp))
                        Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (channel.isAdmin && !channel.detached) {
                item {
                    TokenPanel(
                        title = "Trigger",
                        token = channel.triggerToken.orEmpty(),
                        primaryActionLabel = "Webhook",
                        primaryValue = "${BuildConfig.HOOK_BASE_URL}/t/${channel.triggerToken.orEmpty()}",
                        secondaryActionLabel = "Email",
                        secondaryValue = "${channel.triggerToken.orEmpty()}@${BuildConfig.EMAIL_TRIGGER_DOMAIN}",
                        onReset = { viewModel.resetTriggerToken(channel.remoteId) },
                        onCopy = { label, value ->
                            context.copyToClipboard(label, value)
                            viewModel.showMessage("$label copied.")
                        },
                    )
                }
                item {
                    TokenPanel(
                        title = "Subscription Link",
                        token = channel.subscriptionToken.orEmpty(),
                        primaryActionLabel = "Link",
                        primaryValue = "https://echobell.one/subscription/${channel.subscriptionToken.orEmpty()}",
                        secondaryActionLabel = null,
                        secondaryValue = null,
                        onReset = { viewModel.resetSubscriptionToken(channel.remoteId) },
                        onCopy = { label, value ->
                            context.copyToClipboard(label, value)
                            viewModel.showMessage("$label copied.")
                        },
                    )
                }
            }

            if (!channel.detached) {
                item {
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Notification", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            if (channel.subscribedAt != null) {
                                TextButton(onClick = { unsubscribeDialog = true }) {
                                    Text("Unsubscribe", color = MaterialTheme.colorScheme.error)
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
                            Text("Subscribe to this channel on this Android device to receive notifications.")
                            Spacer(Modifier.height(10.dp))
                            Button(onClick = { viewModel.subscribeToChannel(channel.subscriptionToken, NotificationType.Active) }) {
                                Text("Subscribe")
                            }
                        }
                    }
                }
            }

            item {
                SectionTitle("Notification Templates")
                AppCard {
                    Text(channel.titleTemplate, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider(Modifier.padding(vertical = 10.dp))
                    Text(channel.bodyTemplate, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (channel.isAdmin && !channel.conditions.isNullOrBlank()) {
                item {
                    SectionTitle("Conditions")
                    AppCard { Text(channel.conditions.orEmpty()) }
                }
            }
            if (channel.isAdmin && !channel.externalLinkTemplate.isNullOrBlank()) {
                item {
                    SectionTitle("Link Template")
                    AppCard { Text(channel.externalLinkTemplate.orEmpty()) }
                }
            }
            if (records.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SectionTitle("Recent Records", Modifier.weight(1f))
                        TextButton(onClick = { viewModel.clearRecordsForChannel(channel.remoteId) }) {
                            Text("Delete All", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                items(records.take(5), key = { it.id }) { record ->
                    RecordCard(record = record, channel = channel, onChannelClick = {}, onToggleRead = {
                        viewModel.markRecord(record.id, !record.checked)
                    }, onDelete = {
                        viewModel.deleteRecord(record.id)
                    })
                }
            }
            if (channel.isAdmin && !channel.detached) {
                item {
                    OutlinedButton(onClick = { navController.navigate(Routes.subscribers(channel.remoteId)) }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Person, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Manage Subscribers")
                    }
                }
            }
            if (channel.isAdmin || channel.detached) {
                item {
                    OutlinedButton(onClick = { deleteDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(8.dp))
                        Text("Delete Channel", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (deleteDialog) {
        ConfirmDialog(
            title = "Delete Channel",
            text = "This action cannot be undone.",
            confirm = "Delete",
            onDismiss = { deleteDialog = false },
            onConfirm = {
                deleteDialog = false
                viewModel.deleteChannel(channel) { navController.popBackStack() }
            },
        )
    }
    if (unsubscribeDialog) {
        ConfirmDialog(
            title = "Unsubscribe",
            text = "You will no longer receive notifications from this channel.",
            confirm = "Unsubscribe",
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
        Text(
            if (visible) token else token.hiddenToken(),
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.clickable { visible = !visible },
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis,
        )
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onReset) {
                Icon(Icons.Default.Refresh, contentDescription = null)
            }
            Button(onClick = { onCopy(primaryActionLabel, primaryValue) }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(primaryActionLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (secondaryActionLabel != null && secondaryValue != null) {
                Button(onClick = { onCopy(secondaryActionLabel, secondaryValue) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(secondaryActionLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
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

    ScreenScaffold(
        title = if (channel == null) "New Channel" else "Edit Channel",
        navigationIcon = { BackOrEmpty(navController) },
        actions = {
            IconButton(
                enabled = valid,
                onClick = {
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
                },
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
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
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Channel name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    Text("Color", style = MaterialTheme.typography.labelLarge)
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
            item {
                SectionTitle("Notification Templates")
                AppCard {
                    OutlinedTextField(
                        value = titleTemplate,
                        onValueChange = { titleTemplate = it },
                        label = { Text("Title template") },
                        placeholder = { Text("Defaults to channel name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = bodyTemplate,
                        onValueChange = { bodyTemplate = it },
                        label = { Text("Body template") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            item {
                SectionTitle("Advanced Settings")
                AppCard {
                    OutlinedTextField(value = conditions, onValueChange = { conditions = it }, label = { Text("Conditions") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = externalLinkTemplate, onValueChange = { externalLinkTemplate = it }, label = { Text("Link template") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                }
            }
            if (channel == null) {
                item {
                    SectionTitle("Subscription")
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Subscribe after creation")
                                Text("This device will receive notifications immediately.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        title = "Subscribe Channel",
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
                        Text("Enter a subscription link or token.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(value = input, onValueChange = { input = it; channelInfo = null }, label = { Text("Subscription link") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {
                            token?.let { viewModel.fetchChannelBySubscriptionToken(it) { channelInfo = it } }
                        }, enabled = token != null, modifier = Modifier.fillMaxWidth()) {
                            Text("Get Information")
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
                            Text("Subscribe")
                        }
                    }
                }
            }
            if (!state.canCreateOrSubscribeChannel) {
                item {
                    LimitBanner("Free users can keep up to $FREE_USER_CHANNEL_LIMIT active channels.") {
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
                Text(channel.name.ifBlank { "Channel #${channel.id}" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
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
    var newKeyDialog by rememberSaveable { mutableStateOf(false) }
    var keyName by rememberSaveable { mutableStateOf("") }

    ScreenScaffold(
        title = "Direct",
        navigationIcon = { BackOrEmpty(navController) },
        actions = {
            IconButton(onClick = { viewModel.syncDirectKeys() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Sync")
            }
            IconButton(onClick = {
                if (state.canCreateDirectKey) newKeyDialog = true else navController.navigate(Routes.Paywall)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Create direct key")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AppCard(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Send notifications directly via webhook.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text("No channel setup is required.")
                        }
                    }
                }
            }
            if (state.directKeys.isEmpty()) {
                item {
                    EmptyState(Icons.Default.Key, "No Direct Keys", "Create a direct key to send notifications directly.")
                }
            }
            items(state.directKeys, key = { it.remoteId }) { directKey ->
                DirectKeyCard(directKey, viewModel)
            }
            if (!state.canCreateDirectKey) {
                item {
                    LimitBanner("Free users can create up to $FREE_USER_DIRECT_KEY_LIMIT direct keys.") {
                        navController.navigate(Routes.Paywall)
                    }
                }
            }
        }
    }

    if (newKeyDialog) {
        AlertDialog(
            onDismissRequest = { newKeyDialog = false },
            title = { Text("New Direct Key") },
            text = {
                OutlinedTextField(value = keyName, onValueChange = { keyName = it }, label = { Text("Key name") })
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
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { newKeyDialog = false }) { Text("Cancel") }
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

    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(directKey.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text("Key", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(Modifier.padding(vertical = 10.dp))
        Text(
            if (visible) directKey.token else directKey.token.hiddenToken(),
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable { visible = !visible },
        )
        HorizontalDivider(Modifier.padding(vertical = 10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = { resetDialog = true }) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset")
            }
            Button(onClick = {
                context.copyToClipboard("Webhook", webhook)
                viewModel.showMessage("Webhook copied.")
            }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Webhook", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = { clearDialog = true }) {
                Icon(Icons.Default.VisibilityOff, contentDescription = "Clear records")
            }
            IconButton(onClick = { deleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (resetDialog) {
        ConfirmDialog("Reset Token", "The current webhook URL will stop working.", "Reset", { resetDialog = false }) {
            resetDialog = false
            viewModel.resetDirectKeyToken(directKey.remoteId)
        }
    }
    if (clearDialog) {
        ConfirmDialog("Clear Records", "All records for this direct key will be removed.", "Clear", { clearDialog = false }) {
            clearDialog = false
            viewModel.clearRecordsForDirectKey(directKey.remoteId)
        }
    }
    if (deleteDialog) {
        ConfirmDialog("Delete Direct Key", "This action cannot be undone.", "Delete", { deleteDialog = false }) {
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
) {
    val context = LocalContext.current
    ScreenScaffold(
        title = "Settings",
        navigationIcon = { BackOrEmpty(navController) },
        actions = {
            IconButton(onClick = { navController.navigate(Routes.Announcements) }) {
                Icon(Icons.Default.Campaign, contentDescription = "Announcements")
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
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !context.canUseFullScreenIntent()) {
                item { FullScreenIntentBanner { context.openFullScreenIntentSettings() } }
            }
            item {
                SectionTitle("Account")
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Hello, ${state.user?.name ?: "User"}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(state.user?.email ?: "#${state.user?.id ?: 0}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { navController.navigate(Routes.User) }) {
                            Icon(Icons.Default.Settings, contentDescription = "User settings")
                        }
                    }
                    Text("Channel and subscription data are stored on Echobell servers. Notification records stay on this device.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item {
                SettingsAction(Icons.Default.Info, "User Guide") {
                    context.openUrl("https://echobell.one/docs")
                }
            }
            item {
                SettingsAction(Icons.Default.Person, "Invite Friends", trailing = if (state.user?.canSubmitInviteCode() == true) "Bonus Available" else null) {
                    navController.navigate(Routes.Invite)
                }
            }
            item {
                SectionTitle("Records")
                AppCard {
                    OutlinedButton(onClick = { viewModel.markAllRecordsRead() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Mark All as Read")
                    }
                    OutlinedButton(onClick = { viewModel.deleteAllRecords() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Delete All Notifications", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            item {
                SectionTitle("Support")
                AppCard {
                    SupportLink("Email Support", "mailto:echobell@weelone.com")
                    SupportLink("X (Twitter)", "https://x.com/EchobellApp")
                    SupportLink("Discord Group", "https://discord.gg/s4JqfrgccJ")
                    SupportLink("Telegram Group", "https://t.me/EchobellApp")
                    SupportLink("Privacy Policy", "https://echobell.one/privacy")
                    SupportLink("Terms of Service", "https://echobell.one/terms")
                }
            }
        }
    }
}

@Composable
private fun SubscriptionBanner(state: AppUiState, navController: NavHostController) {
    AppCard(
        modifier = Modifier.clickable { navController.navigate(Routes.Paywall) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                if (state.premiumActive) {
                    Text("Premium Active", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Until ${formatDate(state.user?.premiumExpiresAt ?: 0)}")
                } else {
                    Text("Echobell Premium", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Unlock unlimited channels, direct keys, and call notifications.")
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
            if (trailing != null) AssistChip(onClick = {}, label = { Text(trailing) })
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
        title = "User Settings",
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AppCard {
                    Text("Welcome, ${state.user?.name ?: "User"}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    InfoRow("User ID", "#${state.user?.id ?: 0}")
                    state.user?.email?.let { InfoRow("Email", it) }
                    state.user?.premiumExpiresAt?.takeIf { state.premiumActive }?.let { InfoRow("Premium until", formatDate(it)) }
                }
            }
            item {
                OutlinedButton(onClick = { renameDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Edit Name")
                }
            }
            item {
                OutlinedButton(onClick = { signOutDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out", color = MaterialTheme.colorScheme.error)
                }
            }
            item {
                OutlinedButton(enabled = !hasManagedChannels, onClick = { deleteDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete Account", color = MaterialTheme.colorScheme.error)
                }
                if (hasManagedChannels) {
                    Text("Delete managed channels before requesting account deletion.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (renameDialog) {
        AlertDialog(
            onDismissRequest = { renameDialog = false },
            title = { Text("Edit Name") },
            text = { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("New name") }) },
            confirmButton = {
                TextButton(onClick = {
                    renameDialog = false
                    viewModel.rename(name)
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { renameDialog = false }) { Text("Cancel") } },
        )
    }
    if (signOutDialog) {
        ConfirmDialog("Sign Out", "You will need to sign in again to access your account.", "Sign Out", { signOutDialog = false }) {
            signOutDialog = false
            viewModel.signOut()
        }
    }
    if (deleteDialog) {
        ConfirmDialog("Delete Account", "Echobell will email you to confirm deletion.", "Request Deletion", { deleteDialog = false }) {
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
        title = "Invite Friends",
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AppCard {
                    Text("Points Balance", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(Modifier.width(8.dp))
                        Text("${user?.pointsBalance ?: 0}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text("points")
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { redeemDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Redeem Points")
                    }
                }
            }
            item {
                AppCard {
                    Text("Your Invite Code", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (user?.inviteCode.isNullOrBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = { viewModel.generateInviteCode() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Generate Invite Code")
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(user!!.inviteCode!!, style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                context.copyToClipboard("Invite Code", user.inviteCode)
                                viewModel.showMessage("Invite code copied.")
                            }) { Icon(Icons.Default.ContentCopy, contentDescription = "Copy") }
                        }
                        Button(onClick = { context.shareText("Join me on Echobell! Use my invite code: ${user.inviteCode}") }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Share Invite Code")
                        }
                    }
                }
            }
            if (user?.canSubmitInviteCode() == true) {
                item {
                    AppCard {
                        Text("Have an invite code?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Enter a friend's code to earn bonus points.")
                        Spacer(Modifier.height(10.dp))
                        OutlinedButton(onClick = { submitDialog = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Enter Invite Code")
                        }
                    }
                }
            }
            item {
                AppCard {
                    Text("Rewards", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    InfoRow("Friend signs up with your code", "+100 points for them")
                    InfoRow("Friend subscribes monthly", "+20 points for you")
                    InfoRow("Friend subscribes annual", "+200 points for you")
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    InfoRow("Redeem 1 month premium", "200 points")
                    InfoRow("Redeem 1 year premium", "2000 points")
                }
            }
        }
    }

    if (submitDialog) {
        AlertDialog(
            onDismissRequest = { submitDialog = false },
            title = { Text("Enter Invite Code") },
            text = { OutlinedTextField(value = code, onValueChange = { code = it.uppercase(Locale.US) }, label = { Text("Invite code") }) },
            confirmButton = {
                TextButton(enabled = code.isNotBlank(), onClick = {
                    submitDialog = false
                    viewModel.submitInviteCode(code)
                    code = ""
                }) { Text("Submit") }
            },
            dismissButton = { TextButton(onClick = { submitDialog = false }) { Text("Cancel") } },
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
        title = { Text("Redeem Points") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Your balance: $balance points")
                OutlinedButton(enabled = balance >= 200, onClick = { onRedeem("month") }, modifier = Modifier.fillMaxWidth()) {
                    Text("1 Month Premium - 200 points")
                }
                OutlinedButton(enabled = balance >= 2000, onClick = { onRedeem("year") }, modifier = Modifier.fillMaxWidth()) {
                    Text("1 Year Premium - 2000 points")
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaywallScreen(state: AppUiState, viewModel: EchobellViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as? Activity
    val billingManager = remember {
        GoogleBillingManager(
            context = context,
            onPurchaseReady = { productId, purchaseToken ->
                viewModel.reportGoogleSubscription(productId, purchaseToken)
            },
            onMessage = viewModel::showMessage,
        )
    }
    val billingProducts by billingManager.products
    val billingLoading by billingManager.loading
    var selectedProduct by remember { mutableStateOf<BillingProduct?>(null) }

    LaunchedEffect(Unit) {
        billingManager.start()
    }
    DisposableEffect(billingManager) {
        onDispose { billingManager.dispose() }
    }
    LaunchedEffect(billingProducts) {
        if (selectedProduct == null) {
            selectedProduct = billingProducts.firstOrNull { it.productId.endsWith("annual") } ?: billingProducts.firstOrNull()
        }
    }

    ScreenScaffold(
        title = "Echobell Premium",
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AppCard(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(42.dp))
                    Spacer(Modifier.height(8.dp))
                    if (state.premiumActive) {
                        Text("You're a Premium Member", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Premium access until ${formatDate(state.user?.premiumExpiresAt ?: 0)}")
                    } else {
                        Text("Unlock Premium Features", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Subscribe with Google Play or use invite points to extend premium access.")
                    }
                }
            }
            item {
                AppCard {
                    FeatureRow(Icons.Default.Phone, "Call Notifications", "Get urgent notifications with high-priority Android delivery.")
                    FeatureRow(Icons.Default.Notifications, "Unlimited Channels", "Free users can keep up to $FREE_USER_CHANNEL_LIMIT active channels.")
                    FeatureRow(Icons.Default.Key, "Unlimited Direct Keys", "Free users can create up to $FREE_USER_DIRECT_KEY_LIMIT direct keys.")
                    FeatureRow(Icons.Default.Star, "Support Echobell", "Keep the service sustainable and fast.")
                }
            }
            item {
                SectionTitle("Google Play")
                AppCard {
                    when {
                        state.premiumActive -> Text("Premium is active on this account.")
                        billingLoading -> CircularProgressIndicator()
                        billingProducts.isEmpty() -> {
                            Text("Google Play products are not available in this build or on this device.")
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(onClick = { billingManager.start() }, modifier = Modifier.fillMaxWidth()) {
                                Text("Retry")
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
                                Text("Subscribe")
                            }
                            TextButton(onClick = { billingManager.restorePurchases() }, modifier = Modifier.fillMaxWidth()) {
                                Text("Restore Purchases")
                            }
                        }
                    }
                }
            }
            item {
                Button(onClick = { navController.navigate(Routes.Invite) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Redeem Invite Points")
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
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(product.title, fontWeight = FontWeight.SemiBold)
                Text(product.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(product.price, fontWeight = FontWeight.SemiBold)
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
        title = "Announcements",
        navigationIcon = { BackOrEmpty(navController) },
        actions = {
            IconButton(onClick = { viewModel.refreshAnnouncements() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (state.announcements.isEmpty()) {
                item { EmptyState(Icons.Default.Campaign, "No Announcements", "You're all caught up for now.") }
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
    AppCard(Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (read) MaterialTheme.colorScheme.outline else announcement.level.color()),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(announcement.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = if (read) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(announcement.level.label(), style = MaterialTheme.typography.labelSmall, color = announcement.level.color())
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
        title = "Announcement",
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AppCard {
                    Text(announcement.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(onClick = {}, label = { Text(announcement.level.label()) })
                        AssistChip(onClick = {}, label = { Text(formatDate(announcement.updatedAt)) })
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
        title = "Subscribers",
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
                    EmptyState(Icons.Default.Person, "No Subscribers", "This channel has no subscribers yet.")
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
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
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
        title = "Missing",
        navigationIcon = { BackOrEmpty(navController) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text(message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenScaffold(
    title: String,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = navigationIcon,
                actions = { actions() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
        content = content,
    )
}

@Composable
private fun BackOrEmpty(navController: NavHostController) {
    IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }
}

@Composable
private fun AppCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = colors,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.62f)),
    ) {
        Column(Modifier.padding(16.dp), content = content)
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
    AppCard(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Notifications, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Notification permission needed", fontWeight = FontWeight.SemiBold)
                Text("Allow notifications so channel events can be delivered.")
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = requestNotificationPermission, modifier = Modifier.fillMaxWidth()) {
            Text("Allow Notifications")
        }
    }
}

@Composable
private fun FullScreenIntentBanner(openSettings: () -> Unit) {
    AppCard(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Enable full-screen call alerts", fontWeight = FontWeight.SemiBold)
                Text("Allow Echobell to open urgent call notifications while the screen is locked.")
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = openSettings, modifier = Modifier.fillMaxWidth()) {
            Text("Open Settings")
        }
    }
}

@Composable
private fun LimitBanner(text: String, onUpgrade: () -> Unit) {
    AppCard(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text, modifier = Modifier.weight(1f))
            TextButton(onClick = onUpgrade) {
                Text("Upgrade")
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier = modifier.padding(horizontal = 4.dp), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value)
    }
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
                label = { Text(type.label) },
                leadingIcon = {
                    Icon(
                        when (type) {
                            NotificationType.Active -> Icons.Default.Notifications
                            NotificationType.TimeSensitive -> Icons.Default.Info
                            NotificationType.Calling -> Icons.Default.Phone
                        },
                        contentDescription = null,
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
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun AnnouncementPreview(announcement: Announcement, onClick: () -> Unit) {
    AppCard(Modifier.clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Campaign, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(announcement.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Tap to view details", style = MaterialTheme.typography.bodySmall)
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

private fun PaddingValues.plus(other: PaddingValues): PaddingValues = PaddingValues(
    start = calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr) + other.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
    top = calculateTopPadding() + other.calculateTopPadding(),
    end = calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr) + other.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
    bottom = calculateBottomPadding() + other.calculateBottomPadding(),
)

private fun levelPriority(level: AnnouncementLevel): Int = when (level) {
    AnnouncementLevel.Critical -> 3
    AnnouncementLevel.Warning -> 2
    AnnouncementLevel.Info, AnnouncementLevel.Unknown -> 1
}

private fun AnnouncementLevel.label(): String = when (this) {
    AnnouncementLevel.Info -> "Info"
    AnnouncementLevel.Warning -> "Warning"
    AnnouncementLevel.Critical -> "Critical"
    AnnouncementLevel.Unknown -> "Info"
}

@Composable
private fun AnnouncementLevel.color(): Color = when (this) {
    AnnouncementLevel.Info, AnnouncementLevel.Unknown -> MaterialTheme.colorScheme.primary
    AnnouncementLevel.Warning -> MaterialTheme.colorScheme.tertiary
    AnnouncementLevel.Critical -> MaterialTheme.colorScheme.error
}

private fun dayKey(epoch: Long): String =
    Instant.ofEpochSecond(epoch).atZone(ZoneId.systemDefault()).toLocalDate()
        .format(DateTimeFormatter.ofPattern("MMM d, yyyy"))

private fun formatTime(epoch: Long): String =
    Instant.ofEpochSecond(epoch).atZone(ZoneId.systemDefault()).toLocalTime()
        .format(DateTimeFormatter.ofPattern("HH:mm"))

private fun formatDate(epoch: Long): String =
    Instant.ofEpochSecond(epoch).atZone(ZoneId.systemDefault()).toLocalDate()
        .format(DateTimeFormatter.ofPattern("MMM d, yyyy"))

private fun formatDateTime(epoch: Long): String =
    Instant.ofEpochSecond(epoch).atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("MMM d, HH:mm"))

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
            "Share",
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}

private fun Context.openUrl(url: String?) {
    if (url.isNullOrBlank()) return
    startActivity(Intent(Intent.ACTION_VIEW, url.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

private fun Context.canUseFullScreenIntent(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
        getSystemService(NotificationManager::class.java)?.canUseFullScreenIntent() != false

@SuppressLint("InlinedApi")
private fun Context.openFullScreenIntentSettings() {
    val packageUri = "package:$packageName".toUri()
    val settingsIntent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT)
        .setData(packageUri)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData(packageUri)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(
        if (settingsIntent.resolveActivity(packageManager) != null) settingsIntent else fallbackIntent,
    )
}
