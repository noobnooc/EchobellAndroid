package one.echobell.echobellandroid.data

import java.util.UUID

const val FREE_USER_CHANNEL_LIMIT = 3
const val FREE_USER_DIRECT_KEY_LIMIT = 3

enum class NotificationType(val value: String, val label: String) {
    Active("active", "Standard"),
    TimeSensitive("time-sensitive", "Time-Sensitive"),
    Calling("calling", "Call");

    companion object {
        fun fromValue(value: String?): NotificationType? = entries.firstOrNull { it.value == value }
    }
}

data class User(
    val id: Int = 0,
    val name: String = "",
    val email: String? = null,
    val createdAt: Long = 0,
    val premiumExpiresAt: Long? = null,
    val inviteCode: String? = null,
    val invitedByUserId: Int? = null,
    val invitedAt: Long? = null,
    val pointsBalance: Int = 0,
    val canSubmitInviteCode: Boolean? = null,
) {
    fun isPremiumActive(nowEpochSeconds: Long = System.currentTimeMillis() / 1000): Boolean =
        premiumExpiresAt?.let { nowEpochSeconds < it } == true

    fun canSubmitInviteCode(nowEpochSeconds: Long = System.currentTimeMillis() / 1000): Boolean {
        canSubmitInviteCode?.let { return it }
        if (invitedByUserId != null) return false
        if (createdAt <= 0) return false
        return nowEpochSeconds <= createdAt + 30L * 24L * 60L * 60L
    }
}

data class Channel(
    val remoteId: Int,
    val name: String,
    val colorHex: String,
    val titleTemplate: String,
    val bodyTemplate: String,
    val triggerToken: String? = null,
    val subscriptionToken: String? = null,
    val conditions: String? = null,
    val externalLinkTemplate: String? = null,
    val note: String? = null,
    val updatedAt: Long = nowEpoch(),
    val createdAt: Long = nowEpoch(),
    val lastTriggeredAt: Long? = null,
    val isAdmin: Boolean = false,
    val subscribedAt: Long? = null,
    val notificationType: NotificationType? = null,
    val detached: Boolean = false,
)

data class DirectKey(
    val remoteId: Int,
    val name: String,
    val token: String,
    val updatedAt: Long = nowEpoch(),
    val createdAt: Long = nowEpoch(),
)

data class Record(
    val id: String = UUID.randomUUID().toString(),
    val channelId: Int? = null,
    val directKeyId: Int? = null,
    val directKeyName: String? = null,
    val title: String = "",
    val body: String = "",
    val checked: Boolean = false,
    val requestLink: String? = null,
    val externalLink: String? = null,
    val createdAt: Long = nowEpoch(),
)

data class Announcement(
    val id: Int,
    val title: String,
    val content: String,
    val level: AnnouncementLevel = AnnouncementLevel.Info,
    val startsAt: Long,
    val endsAt: Long? = null,
    val audience: String? = null,
    val isBlocking: Boolean = false,
    val ctaLabel: String? = null,
    val ctaUrl: String? = null,
    val imageUrl: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

enum class AnnouncementLevel {
    Info,
    Warning,
    Critical,
    Unknown;

    companion object {
        fun fromApi(value: String?): AnnouncementLevel = when (value?.lowercase()) {
            "info" -> Info
            "warning" -> Warning
            "critical" -> Critical
            else -> Unknown
        }
    }
}

data class ApiUser(
    val id: Int,
    val name: String,
    val email: String?,
    val createdAt: Long,
    val premiumExpiresAt: Long?,
    val inviteCode: String?,
    val invitedByUserId: Int?,
    val invitedAt: Long?,
    val pointsBalance: Int?,
    val canSubmitInviteCode: Boolean?,
) {
    fun toUser() = User(
        id = id,
        name = name,
        email = email,
        createdAt = createdAt,
        premiumExpiresAt = premiumExpiresAt,
        inviteCode = inviteCode,
        invitedByUserId = invitedByUserId,
        invitedAt = invitedAt,
        pointsBalance = pointsBalance ?: 0,
        canSubmitInviteCode = canSubmitInviteCode,
    )
}

data class ApiChannel(
    val id: Int,
    val name: String,
    val color: String,
    val titleTemplate: String,
    val bodyTemplate: String,
    val triggerToken: String?,
    val subscriptionToken: String?,
    val conditions: String?,
    val externalLinkTemplate: String?,
    val note: String?,
    val updatedAt: Long,
    val createdAt: Long,
    val lastTriggeredAt: Long?,
    val admin: Boolean,
    val subscribedAt: Long?,
    val notificationType: String?,
) {
    fun toChannel(existingName: String? = null) = Channel(
        remoteId = id,
        name = name.ifBlank { existingName ?: "Channel #$id" },
        colorHex = color,
        titleTemplate = titleTemplate,
        bodyTemplate = bodyTemplate,
        triggerToken = triggerToken,
        subscriptionToken = subscriptionToken,
        conditions = conditions,
        externalLinkTemplate = externalLinkTemplate,
        note = note,
        updatedAt = updatedAt,
        createdAt = createdAt,
        lastTriggeredAt = lastTriggeredAt,
        isAdmin = admin,
        subscribedAt = subscribedAt,
        notificationType = NotificationType.fromValue(notificationType),
        detached = false,
    )
}

data class ApiDirectKey(
    val id: Int,
    val name: String,
    val token: String,
    val updatedAt: Long,
    val createdAt: Long,
) {
    fun toDirectKey() = DirectKey(remoteId = id, name = name, token = token, updatedAt = updatedAt, createdAt = createdAt)
}

data class ApiChannelSubscriber(val id: Int, val name: String)

data class ServerError(
    val code: String? = null,
    val message: String? = null,
)

class ApiException(
    val statusCode: Int,
    val serverCode: String?,
    override val message: String,
) : Exception(message)

fun nowEpoch(): Long = System.currentTimeMillis() / 1000
