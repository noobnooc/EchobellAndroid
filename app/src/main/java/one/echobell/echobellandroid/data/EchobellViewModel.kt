package one.echobell.echobellandroid.data

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import one.echobell.echobellandroid.push.FirebaseBootstrap
import one.echobell.echobellandroid.push.NotificationHelper

data class UiMessage(val id: Long = System.nanoTime(), val text: String)

data class AppUiState(
    val initialized: Boolean = false,
    val busy: Boolean = false,
    val jwt: String? = null,
    val deviceToken: String? = null,
    val notificationToken: String? = null,
    val user: User? = null,
    val channels: List<Channel> = emptyList(),
    val directKeys: List<DirectKey> = emptyList(),
    val records: List<Record> = emptyList(),
    val announcements: List<Announcement> = emptyList(),
    val readAnnouncementIds: Set<Int> = emptySet(),
    val notificationAuthorized: Boolean = false,
    val messages: List<UiMessage> = emptyList(),
) {
    val authenticated: Boolean get() = deviceToken != null
    val premiumActive: Boolean get() = user?.isPremiumActive() == true
    val activeChannelsCount: Int get() = channels.count { !it.detached }
    val canCreateOrSubscribeChannel: Boolean get() = premiumActive || activeChannelsCount < FREE_USER_CHANNEL_LIMIT
    val canCreateDirectKey: Boolean get() = premiumActive || directKeys.size < FREE_USER_DIRECT_KEY_LIMIT
}

class EchobellViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val api = ApiClient()
    private val store = LocalStore(appContext)
    private val _state = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = _state

    init {
        NotificationHelper.ensureChannel(appContext)
        viewModelScope.launch {
            val saved = store.load()
            _state.value = AppUiState(
                initialized = true,
                jwt = saved.jwt,
                deviceToken = saved.deviceToken,
                notificationToken = saved.notificationToken,
                user = saved.user,
                channels = saved.channels.sortedByDescending { it.updatedAt },
                directKeys = saved.directKeys.sortedByDescending { it.createdAt },
                records = saved.records.sortedByDescending { it.createdAt },
                announcements = saved.announcements.sortedByDescending { it.startsAt },
                readAnnouncementIds = saved.readAnnouncementIds,
                notificationAuthorized = isNotificationAuthorized(),
            )

            val notificationToken = refreshFirebaseTokenInternal()
            if (notificationToken != null && notificationToken != _state.value.notificationToken) {
                store.saveNotificationToken(notificationToken)
                _state.update { it.copy(notificationToken = notificationToken) }
            }

            if (_state.value.deviceToken != null) {
                runCatching {
                    val jwt = refreshSessionInternal()
                    syncChannelsInternal(jwt)
                    syncDirectKeysInternal(jwt)
                }.onFailure { error ->
                    if (error is ApiException && error.statusCode == 401) {
                        clearSignedInState()
                    }
                }
            }
            runCatching { refreshAnnouncementsInternal() }
        }
    }

    fun sendVerificationCode(email: String, onSent: () -> Unit) {
        runAction {
            api.sendVerificationCode(email.trim())
            showMessage("Verification code sent.")
            onSent()
        }
    }

    fun signIn(email: String, code: String) {
        runAction {
            val notificationToken = _state.value.notificationToken
                ?: FirebaseBootstrap.notificationToken(appContext).also { token ->
                    _state.update { it.copy(notificationToken = token) }
                    store.saveNotificationToken(token)
                }
            val response = api.signInWithVerificationCode(email.trim(), code.trim(), notificationToken)
            store.saveAuth(response.jwt, response.deviceToken)
            store.saveUser(response.user.toUser())
            _state.update {
                it.copy(
                    jwt = response.jwt,
                    deviceToken = response.deviceToken,
                    user = response.user.toUser(),
                )
            }
            syncChannelsInternal(response.jwt)
            syncDirectKeysInternal(response.jwt)
            refreshAnnouncementsInternal()
        }
    }

    fun refreshSession(silent: Boolean = false) {
        runAction(silent = silent) {
            refreshSessionInternal()
        }
    }

    fun signOut() {
        runAction {
            val current = _state.value
            if (current.jwt != null && current.deviceToken != null) {
                runCatching { api.signOut(current.jwt, current.deviceToken) }
            }
            clearSignedInState()
        }
    }

    fun requestAccountDeletion() {
        runWithFreshJwt { jwt ->
            api.requestAccountDeletion(jwt)
            signOut()
            showMessage("Deletion confirmation email sent.")
        }
    }

    fun rename(name: String) {
        runWithFreshJwt { jwt ->
            api.rename(jwt, name.trim())
            refreshSession(silent = true)
        }
    }

    fun syncAll(silent: Boolean = false) {
        runWithFreshJwt(silent = silent) { jwt ->
            syncChannelsInternal(jwt)
            syncDirectKeysInternal(jwt)
        }
    }

    fun syncChannels() {
        runWithFreshJwt { jwt -> syncChannelsInternal(jwt) }
    }

    fun createChannel(
        name: String,
        colorHex: String,
        titleTemplate: String,
        bodyTemplate: String,
        conditions: String?,
        externalLinkTemplate: String?,
        note: String?,
        notificationType: NotificationType?,
        onDone: () -> Unit = {},
    ) {
        runWithFreshJwt { jwt ->
            api.createChannel(jwt, name.trim(), colorHex, titleTemplate, bodyTemplate, conditions, externalLinkTemplate, note, notificationType)
            syncChannelsInternal(jwt)
            onDone()
        }
    }

    fun updateChannel(
        channelId: Int,
        name: String,
        colorHex: String,
        titleTemplate: String,
        bodyTemplate: String,
        conditions: String?,
        externalLinkTemplate: String?,
        note: String?,
        onDone: () -> Unit = {},
    ) {
        runWithFreshJwt { jwt ->
            api.updateChannel(jwt, channelId, name.trim(), colorHex, titleTemplate, bodyTemplate, conditions, externalLinkTemplate, note)
            syncChannelsInternal(jwt)
            onDone()
        }
    }

    fun deleteChannel(channel: Channel, onDone: () -> Unit = {}) {
        runWithFreshJwt { jwt ->
            if (channel.isAdmin && !channel.detached) {
                api.deleteChannel(jwt, channel.remoteId)
            }
            val channels = _state.value.channels.filterNot { it.remoteId == channel.remoteId }
            val records = _state.value.records.filterNot { it.channelId == channel.remoteId }
            store.saveChannels(channels)
            store.saveRecords(records)
            _state.update { it.copy(channels = channels, records = records) }
            onDone()
        }
    }

    fun unsubscribe(channel: Channel, onDone: () -> Unit = {}) {
        runWithFreshJwt { jwt ->
            api.unsubscribe(jwt, channel.remoteId)
            if (channel.isAdmin) {
                syncChannelsInternal(jwt)
            } else {
                val channels = _state.value.channels.filterNot { it.remoteId == channel.remoteId }
                store.saveChannels(channels)
                _state.update { it.copy(channels = channels) }
            }
            onDone()
        }
    }

    fun subscribeToChannel(subscriptionToken: String, notificationType: NotificationType, onDone: () -> Unit = {}) {
        runWithFreshJwt { jwt ->
            api.subscribe(jwt, subscriptionToken, notificationType)
            syncChannelsInternal(jwt)
            onDone()
        }
    }

    fun updateNotificationType(channelId: Int, notificationType: NotificationType) {
        runWithFreshJwt { jwt ->
            api.updateSubscriptionNotificationType(jwt, channelId, notificationType)
            val channels = _state.value.channels.map {
                if (it.remoteId == channelId) it.copy(notificationType = notificationType) else it
            }
            store.saveChannels(channels)
            _state.update { it.copy(channels = channels) }
        }
    }

    fun fetchChannelBySubscriptionToken(token: String, onResult: (ApiChannel) -> Unit) {
        runWithFreshJwt { jwt -> onResult(api.fetchChannelBySubscriptionToken(jwt, token)) }
    }

    fun resetTriggerToken(channelId: Int) {
        runWithFreshJwt { jwt ->
            val newToken = api.resetChannelTriggerToken(jwt, channelId)
            updateChannelLocal(channelId) { it.copy(triggerToken = newToken) }
            showMessage("Trigger token reset.")
        }
    }

    fun resetSubscriptionToken(channelId: Int) {
        runWithFreshJwt { jwt ->
            val newToken = api.resetChannelSubscriptionToken(jwt, channelId)
            updateChannelLocal(channelId) { it.copy(subscriptionToken = newToken) }
            showMessage("Subscription token reset.")
        }
    }

    fun loadSubscribers(channelId: Int, onResult: (List<ApiChannelSubscriber>) -> Unit) {
        runWithFreshJwt { jwt -> onResult(api.getChannelSubscribers(jwt, channelId)) }
    }

    fun removeSubscriber(channelId: Int, subscriberId: Int, onDone: () -> Unit = {}) {
        runWithFreshJwt { jwt ->
            api.removeChannelSubscriber(jwt, channelId, subscriberId)
            onDone()
        }
    }

    fun syncDirectKeys() {
        runWithFreshJwt { jwt -> syncDirectKeysInternal(jwt) }
    }

    fun createDirectKey(name: String) {
        runWithFreshJwt { jwt ->
            api.createDirectKey(jwt, name.trim())
            syncDirectKeysInternal(jwt)
        }
    }

    fun deleteDirectKey(directKeyId: Int) {
        runWithFreshJwt { jwt ->
            api.deleteDirectKey(jwt, directKeyId)
            syncDirectKeysInternal(jwt)
            clearRecordsForDirectKey(directKeyId)
        }
    }

    fun resetDirectKeyToken(directKeyId: Int) {
        runWithFreshJwt { jwt ->
            val newToken = api.resetDirectKeyToken(jwt, directKeyId)
            val directKeys = _state.value.directKeys.map {
                if (it.remoteId == directKeyId) it.copy(token = newToken) else it
            }
            store.saveDirectKeys(directKeys)
            _state.update { it.copy(directKeys = directKeys) }
            showMessage("Direct key token reset.")
        }
    }

    fun markRecord(recordId: String, checked: Boolean) {
        viewModelScope.launch {
            val records = _state.value.records.map { if (it.id == recordId) it.copy(checked = checked) else it }
            store.saveRecords(records)
            _state.update { it.copy(records = records) }
        }
    }

    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            val records = _state.value.records.filterNot { it.id == recordId }
            store.saveRecords(records)
            _state.update { it.copy(records = records) }
        }
    }

    fun markAllRecordsRead() {
        viewModelScope.launch {
            val records = _state.value.records.map { it.copy(checked = true) }
            store.saveRecords(records)
            _state.update { it.copy(records = records) }
        }
    }

    fun deleteAllRecords() {
        viewModelScope.launch {
            store.saveRecords(emptyList())
            _state.update { it.copy(records = emptyList()) }
        }
    }

    fun clearRecordsForChannel(channelId: Int) {
        viewModelScope.launch {
            val records = _state.value.records.filterNot { it.channelId == channelId }
            store.saveRecords(records)
            _state.update { it.copy(records = records) }
        }
    }

    fun clearRecordsForDirectKey(directKeyId: Int) {
        viewModelScope.launch {
            val records = _state.value.records.filterNot { it.directKeyId == directKeyId }
            store.saveRecords(records)
            _state.update { it.copy(records = records) }
        }
    }

    fun refreshAnnouncements(silent: Boolean = false) {
        runAction(silent = silent) {
            refreshAnnouncementsInternal()
        }
    }

    fun markAnnouncementRead(id: Int) {
        viewModelScope.launch {
            val ids = _state.value.readAnnouncementIds + id
            store.saveReadAnnouncementIds(ids)
            _state.update { it.copy(readAnnouncementIds = ids) }
        }
    }

    fun generateInviteCode() {
        runWithFreshJwt { jwt ->
            val response = api.generateInviteCode(jwt)
            store.saveUser(response.user.toUser())
            _state.update { it.copy(user = response.user.toUser()) }
        }
    }

    fun submitInviteCode(code: String) {
        runWithFreshJwt { jwt ->
            val response = api.submitInviteCode(jwt, code.trim().uppercase())
            store.saveUser(response.user.toUser())
            _state.update { it.copy(user = response.user.toUser()) }
            showMessage("Invite code applied.")
        }
    }

    fun redeemPoints(tier: String) {
        runWithFreshJwt { jwt ->
            val response = api.redeemPoints(jwt, tier)
            store.saveUser(response.user.toUser())
            _state.update { it.copy(user = response.user.toUser()) }
            showMessage("Premium extended.")
        }
    }

    fun reportGoogleSubscription(
        productId: String,
        purchaseToken: String,
        onVerified: () -> Unit = {},
        onFailure: () -> Unit = {},
        showSuccessMessage: Boolean = true,
    ) {
        viewModelScope.launch {
            _state.update { it.copy(busy = true) }
            try {
                val jwt = _state.value.jwt ?: throw ApiException(401, null, "Not signed in")
                try {
                    api.reportGoogleSubscription(jwt, productId, purchaseToken)
                } catch (error: ApiException) {
                    if (error.statusCode == 401) {
                        api.reportGoogleSubscription(refreshSessionInternal(), productId, purchaseToken)
                    } else {
                        throw error
                    }
                }
                onVerified()
                refreshSessionInternal()
                if (showSuccessMessage) {
                    showMessage("Subscription updated.")
                }
            } catch (error: Exception) {
                onFailure()
                showMessage(error.readableMessage())
            } finally {
                _state.update { it.copy(busy = false) }
            }
        }
    }

    fun syncNotificationPermission() {
        _state.update { it.copy(notificationAuthorized = isNotificationAuthorized()) }
        refreshFirebaseToken()
    }

    fun consumeMessage(id: Long) {
        _state.update { current -> current.copy(messages = current.messages.filterNot { it.id == id }) }
    }

    fun showMessage(text: String) {
        _state.update { it.copy(messages = it.messages + UiMessage(text = text)) }
    }

    private suspend fun syncChannelsInternal(jwt: String) {
        val apiChannels = api.fetchChannels(jwt)
        val localById = _state.value.channels.associateBy { it.remoteId }
        val serverIds = apiChannels.map { it.id }.toSet()
        val synced = apiChannels
            .map { it.toChannel(localById[it.id]?.name) } +
            _state.value.channels
                .filter { it.remoteId !in serverIds && !it.detached }
                .map {
                    it.copy(
                        detached = true,
                        triggerToken = null,
                        subscriptionToken = null,
                        isAdmin = false,
                        subscribedAt = null,
                        updatedAt = nowEpoch(),
                    )
                }
        val channels = synced.distinctBy { it.remoteId }.sortedByDescending { it.updatedAt }
        store.saveChannels(channels)
        _state.update { it.copy(channels = channels) }
    }

    private suspend fun syncDirectKeysInternal(jwt: String) {
        val directKeys = api.fetchDirectKeys(jwt)
            .map { it.toDirectKey() }
            .sortedByDescending { it.createdAt }
        store.saveDirectKeys(directKeys)
        _state.update { it.copy(directKeys = directKeys) }
    }

    private suspend fun refreshAnnouncementsInternal() {
        val announcements = api.fetchAnnouncements().sortedWith(
            compareByDescending<Announcement> { levelPriority(it.level) }
                .thenByDescending { maxOf(it.updatedAt, it.startsAt) },
        )
        store.saveAnnouncements(announcements)
        _state.update { it.copy(announcements = announcements) }
    }

    private suspend fun refreshSessionInternal(): String {
        val deviceToken = _state.value.deviceToken ?: throw ApiException(401, null, "Not signed in")
        val response = api.refresh(deviceToken, _state.value.notificationToken)
        store.saveAuth(response.jwt, deviceToken)
        store.saveUser(response.user.toUser())
        _state.update { it.copy(jwt = response.jwt, user = response.user.toUser()) }
        return response.jwt
    }

    private fun runWithFreshJwt(silent: Boolean = false, block: suspend (String) -> Unit) {
        runAction(silent = silent) {
            val jwt = _state.value.jwt ?: throw ApiException(401, null, "Not signed in")
            try {
                block(jwt)
            } catch (error: ApiException) {
                if (error.statusCode == 401) {
                    block(refreshSessionInternal())
                } else {
                    throw error
                }
            }
        }
    }

    private fun runAction(silent: Boolean = false, block: suspend () -> Unit) {
        viewModelScope.launch {
            if (!silent) _state.update { it.copy(busy = true) }
            try {
                block()
            } catch (error: Exception) {
                if (!silent) showMessage(error.readableMessage())
            } finally {
                if (!silent) _state.update { it.copy(busy = false) }
            }
        }
    }

    private fun updateChannelLocal(channelId: Int, transform: (Channel) -> Channel) {
        viewModelScope.launch {
            val channels = _state.value.channels.map { if (it.remoteId == channelId) transform(it) else it }
            store.saveChannels(channels)
            _state.update { it.copy(channels = channels) }
        }
    }

    private fun refreshFirebaseToken() {
        viewModelScope.launch {
            val token = refreshFirebaseTokenInternal() ?: return@launch
            if (token == _state.value.notificationToken) return@launch
            store.saveNotificationToken(token)
            _state.update { it.copy(notificationToken = token) }
            _state.value.deviceToken?.let { refreshSession(silent = true) }
        }
    }

    private suspend fun refreshFirebaseTokenInternal(): String? =
        FirebaseBootstrap.notificationToken(appContext)

    private suspend fun clearSignedInState() {
        store.clearUserData()
        _state.update {
            it.copy(
                jwt = null,
                deviceToken = null,
                user = null,
                channels = emptyList(),
                directKeys = emptyList(),
                records = emptyList(),
            )
        }
    }

    private fun isNotificationAuthorized(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun levelPriority(level: AnnouncementLevel): Int = when (level) {
        AnnouncementLevel.Critical -> 3
        AnnouncementLevel.Warning -> 2
        AnnouncementLevel.Info, AnnouncementLevel.Unknown -> 1
    }
}

private fun Throwable.readableMessage(): String = when (this) {
    is ApiException -> message
    else -> localizedMessage ?: "Unknown error"
}
