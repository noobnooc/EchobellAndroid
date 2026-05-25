package one.echobell.echobellandroid.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelsTest {
    @Test
    fun premiumIsActiveOnlyBeforeExpiry() {
        assertTrue(User(premiumExpiresAt = 2_000).isPremiumActive(nowEpochSeconds = 1_000))
        assertFalse(User(premiumExpiresAt = 1_000).isPremiumActive(nowEpochSeconds = 1_000))
        assertFalse(User(premiumExpiresAt = null).isPremiumActive(nowEpochSeconds = 1_000))
    }

    @Test
    fun inviteSubmissionUsesServerOverrideWhenPresent() {
        assertTrue(User(canSubmitInviteCode = true, invitedByUserId = 1).canSubmitInviteCode(nowEpochSeconds = 1_000))
        assertFalse(User(canSubmitInviteCode = false, createdAt = 1_000).canSubmitInviteCode(nowEpochSeconds = 1_000))
    }

    @Test
    fun inviteSubmissionFallsBackToThirtyDayWindow() {
        val createdAt = 1_000L
        assertTrue(User(createdAt = createdAt).canSubmitInviteCode(nowEpochSeconds = createdAt + 30L * 24L * 60L * 60L))
        assertFalse(User(createdAt = createdAt).canSubmitInviteCode(nowEpochSeconds = createdAt + 30L * 24L * 60L * 60L + 1L))
        assertFalse(User(createdAt = createdAt, invitedByUserId = 2).canSubmitInviteCode(nowEpochSeconds = createdAt + 1L))
    }

    @Test
    fun apiChannelPreservesLocalFallbackName() {
        val channel = ApiChannel(
            id = 42,
            name = "",
            color = "#1565C0",
            titleTemplate = "Title",
            bodyTemplate = "Body",
            triggerToken = "trigger",
            subscriptionToken = "subscription",
            conditions = null,
            externalLinkTemplate = null,
            note = null,
            updatedAt = 20,
            createdAt = 10,
            lastTriggeredAt = null,
            admin = true,
            subscribedAt = 30,
            notificationType = "calling",
        ).toChannel(existingName = "Existing")

        assertEquals("Existing", channel.name)
        assertEquals(NotificationType.Calling, channel.notificationType)
        assertFalse(channel.detached)
    }
}
