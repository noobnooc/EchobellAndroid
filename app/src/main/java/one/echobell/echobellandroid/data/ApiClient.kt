package one.echobell.echobellandroid.data

import android.os.Build
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import one.echobell.echobellandroid.BuildConfig
import java.util.Locale
import java.util.concurrent.TimeUnit

class ApiClient(
    private val gson: Gson = Gson(),
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build(),
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun sendVerificationCode(email: String) {
        post(
            "/v1/send-verification-code",
            mapOf("email" to email, "locale" to Locale.getDefault().toLanguageTag()),
            EmptyResponse::class.java,
        )
    }

    suspend fun signInWithVerificationCode(
        email: String,
        code: String,
        notificationToken: String?,
    ): SignInResponse = post(
        "/v1/sign-in-with-verification-code",
        mapOf(
            "email" to email,
            "code" to code,
            "deviceName" to deviceName(),
            "platform" to "Android",
            "notificationToken" to notificationToken,
            "locale" to Locale.getDefault().toLanguageTag(),
        ),
        SignInResponse::class.java,
    )

    suspend fun refresh(deviceToken: String, notificationToken: String?): RefreshResponse = post(
        "/v1/refresh",
        mapOf(
            "deviceToken" to deviceToken,
            "notificationToken" to notificationToken,
        ),
        RefreshResponse::class.java,
    )

    suspend fun signOut(jwt: String, deviceToken: String) {
        postWithToken("/v1/sign-out", mapOf("deviceToken" to deviceToken), jwt, EmptyResponse::class.java)
    }

    suspend fun rename(jwt: String, name: String) {
        postWithToken("/v1/user/rename", mapOf("name" to name), jwt, EmptyResponse::class.java)
    }

    suspend fun requestAccountDeletion(jwt: String) {
        postWithToken("/v1/user/request-account-deletion", emptyMap<String, String>(), jwt, EmptyResponse::class.java)
    }

    suspend fun fetchChannels(jwt: String): List<ApiChannel> =
        getWithToken("/v1/channels", jwt, ChannelsResponse::class.java).channels

    suspend fun createChannel(
        jwt: String,
        name: String,
        color: String,
        titleTemplate: String,
        bodyTemplate: String,
        conditions: String?,
        externalLinkTemplate: String?,
        note: String?,
        notificationType: NotificationType?,
    ): ApiChannel = postWithToken(
        "/v1/channels/create",
        mapOf(
            "name" to name,
            "color" to color,
            "titleTemplate" to titleTemplate.ifBlank { name },
            "bodyTemplate" to bodyTemplate,
            "conditions" to conditions.blankToNull(),
            "externalLinkTemplate" to externalLinkTemplate.blankToNull(),
            "note" to note.blankToNull(),
            "notificationType" to notificationType?.value,
        ),
        jwt,
        ChannelCreateResponse::class.java,
    ).channel

    suspend fun updateChannel(
        jwt: String,
        channelId: Int,
        name: String,
        color: String,
        titleTemplate: String,
        bodyTemplate: String,
        conditions: String?,
        externalLinkTemplate: String?,
        note: String?,
    ) {
        postWithToken(
            "/v1/channels/update",
            mapOf(
                "channelId" to channelId,
                "name" to name,
                "color" to color,
                "titleTemplate" to titleTemplate.ifBlank { name },
                "bodyTemplate" to bodyTemplate,
                "conditions" to conditions.blankToNull(),
                "externalLinkTemplate" to externalLinkTemplate.blankToNull(),
                "note" to note.blankToNull(),
            ),
            jwt,
            EmptyResponse::class.java,
        )
    }

    suspend fun deleteChannel(jwt: String, channelId: Int) {
        postWithToken("/v1/channels/delete", mapOf("channelId" to channelId), jwt, EmptyResponse::class.java)
    }

    suspend fun unsubscribe(jwt: String, channelId: Int) {
        postWithToken("/v1/channels/unsubscribe", mapOf("channelId" to channelId), jwt, EmptyResponse::class.java)
    }

    suspend fun subscribe(jwt: String, subscriptionToken: String, notificationType: NotificationType) {
        postWithToken(
            "/v1/channels/subscribe",
            mapOf("subscriptionToken" to subscriptionToken, "notificationType" to notificationType.value),
            jwt,
            EmptyResponse::class.java,
        )
    }

    suspend fun updateSubscriptionNotificationType(jwt: String, channelId: Int, notificationType: NotificationType) {
        postWithToken(
            "/v1/channels/update-subscription-notification-type",
            mapOf("channelId" to channelId, "notificationType" to notificationType.value),
            jwt,
            EmptyResponse::class.java,
        )
    }

    suspend fun fetchChannelBySubscriptionToken(jwt: String, subscriptionToken: String): ApiChannel =
        postWithToken(
            "/v1/channels/get-channel-by-subscription-token",
            mapOf("subscriptionToken" to subscriptionToken),
            jwt,
            ChannelBySubscriptionTokenResponse::class.java,
        ).channel

    suspend fun resetChannelTriggerToken(jwt: String, channelId: Int): String =
        postWithToken(
            "/v1/channels/reset-trigger-token",
            mapOf("channelId" to channelId),
            jwt,
            ResetTokenResponse::class.java,
        ).newToken

    suspend fun resetChannelSubscriptionToken(jwt: String, channelId: Int): String =
        postWithToken(
            "/v1/channels/reset-subscription-token",
            mapOf("channelId" to channelId),
            jwt,
            ResetTokenResponse::class.java,
        ).newToken

    suspend fun getChannelSubscribers(jwt: String, channelId: Int): List<ApiChannelSubscriber> =
        postWithToken(
            "/v1/channels/get-channel-subscribers",
            mapOf("channelId" to channelId),
            jwt,
            ChannelSubscribersResponse::class.java,
        ).subscribers

    suspend fun removeChannelSubscriber(jwt: String, channelId: Int, subscriberId: Int) {
        postWithToken(
            "/v1/channels/remove-subscriber",
            mapOf("channelId" to channelId, "subscriberId" to subscriberId),
            jwt,
            EmptyResponse::class.java,
        )
    }

    suspend fun fetchDirectKeys(jwt: String): List<ApiDirectKey> =
        getWithToken("/v1/direct", jwt, DirectKeysResponse::class.java).directKeys

    suspend fun createDirectKey(jwt: String, name: String): ApiDirectKey =
        postWithToken("/v1/direct/create", mapOf("name" to name), jwt, DirectKeyResponse::class.java).directKey

    suspend fun deleteDirectKey(jwt: String, directKeyId: Int) {
        postWithToken("/v1/direct/delete", mapOf("directKeyId" to directKeyId), jwt, EmptyResponse::class.java)
    }

    suspend fun resetDirectKeyToken(jwt: String, directKeyId: Int): String =
        postWithToken(
            "/v1/direct/reset-token",
            mapOf("directKeyId" to directKeyId),
            jwt,
            ResetTokenResponse::class.java,
        ).newToken

    suspend fun fetchAnnouncements(): List<Announcement> {
        val url = (BuildConfig.API_BASE_URL + "/v1/announcements")
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("audience", "android")
            .addQueryParameter("locale", Locale.getDefault().toLanguageTag())
            .build()
        val response = request(Request.Builder().url(url).get().build(), AnnouncementsResponse::class.java)
        return response.announcements.map {
            Announcement(
                id = it.id,
                title = it.title,
                content = it.content,
                level = AnnouncementLevel.fromApi(it.level),
                startsAt = it.startsAt,
                endsAt = it.endsAt,
                audience = it.audience,
                isBlocking = it.isBlocking,
                ctaLabel = it.ctaLabel,
                ctaUrl = it.ctaUrl,
                imageUrl = it.imageUrl,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        }
    }

    suspend fun generateInviteCode(jwt: String): InviteGenerateCodeResponse =
        postWithToken("/v1/invite/generate-code", emptyMap<String, String>(), jwt, InviteGenerateCodeResponse::class.java)

    suspend fun submitInviteCode(jwt: String, code: String): InviteSubmitCodeResponse =
        postWithToken("/v1/invite/submit-code", mapOf("code" to code), jwt, InviteSubmitCodeResponse::class.java)

    suspend fun fetchInviteStatus(jwt: String): InviteStatusResponse =
        getWithToken("/v1/invite/status", jwt, InviteStatusResponse::class.java)

    suspend fun redeemPoints(jwt: String, tier: String): RedeemPointsResponse =
        postWithToken("/v1/invite/redeem", mapOf("tier" to tier), jwt, RedeemPointsResponse::class.java)

    suspend fun reportGoogleSubscription(jwt: String, productId: String, purchaseToken: String) {
        postWithToken(
            "/v1/subscription/report-google-subscription",
            mapOf("productId" to productId, "purchaseToken" to purchaseToken),
            jwt,
            EmptyResponse::class.java,
        )
    }

    private suspend fun <T> getWithToken(path: String, token: String, type: Class<T>): T {
        val request = Request.Builder()
            .url(BuildConfig.API_BASE_URL + path)
            .header("Authorization", "Bearer $token")
            .get()
            .build()
        return request(request, type)
    }

    private suspend fun <T> post(path: String, body: Any, type: Class<T>): T {
        val request = Request.Builder()
            .url(BuildConfig.API_BASE_URL + path)
            .post(toJsonBody(body))
            .build()
        return request(request, type)
    }

    private suspend fun <T> postWithToken(path: String, body: Any, token: String, type: Class<T>): T {
        val request = Request.Builder()
            .url(BuildConfig.API_BASE_URL + path)
            .header("Authorization", "Bearer $token")
            .post(toJsonBody(body))
            .build()
        return request(request, type)
    }

    private suspend fun <T> request(request: Request, type: Class<T>): T = withContext(Dispatchers.IO) {
        client.newCall(request).execute().use { response ->
            val raw = response.body.string()
            if (!response.isSuccessful) {
                val serverError = runCatching { gson.fromJson(raw, ServerError::class.java) }.getOrNull()
                throw ApiException(
                    response.code,
                    serverError?.code,
                    serverError?.message?.takeIf { it.isNotBlank() }
                        ?: serverError?.code
                        ?: raw.takeIf { it.isNotBlank() }
                        ?: response.message,
                )
            }
            gson.fromJson(raw.ifBlank { "{}" }, type)
        }
    }

    private fun toJsonBody(body: Any) = cleanJson(gson.toJsonTree(body).asJsonObject)
        .toString()
        .toRequestBody(jsonMediaType)

    private fun cleanJson(json: JsonObject): JsonObject {
        val result = JsonObject()
        json.entrySet().forEach { (key, value) ->
            if (!value.isJsonNull) result.add(key, value)
        }
        return result
    }

    private fun deviceName(): String = listOfNotNull(
        Build.MANUFACTURER.replaceFirstChar { it.titlecase() },
        Build.MODEL,
    ).joinToString(" ")
}

private fun String?.blankToNull(): String? = this?.trim()?.takeIf { it.isNotEmpty() }

data class EmptyResponse(val success: Boolean? = null)
data class SignInResponse(val user: ApiUser, val deviceToken: String, val jwt: String)
data class RefreshResponse(val user: ApiUser, val jwt: String)
data class ChannelsResponse(val channels: List<ApiChannel>)
data class ChannelCreateResponse(val channel: ApiChannel)
data class ChannelBySubscriptionTokenResponse(val channel: ApiChannel)
data class ResetTokenResponse(val newToken: String)
data class ChannelSubscribersResponse(val subscribers: List<ApiChannelSubscriber>)
data class DirectKeysResponse(val directKeys: List<ApiDirectKey>)
data class DirectKeyResponse(val directKey: ApiDirectKey)
data class InviteGenerateCodeResponse(val inviteCode: String, val pointsBalance: Int, val user: ApiUser)
data class InviteSubmitCodeResponse(val invitedByUserId: Int, val invitedAt: Long, val pointsBalance: Int, val user: ApiUser)
data class InviteStatusResponse(val user: ApiUser)
data class RedeemPointsResponse(val premiumExpiresAt: Long, val pointsBalance: Int, val user: ApiUser)

private data class ApiAnnouncement(
    val id: Int,
    val title: String,
    val content: String,
    val level: String?,
    val startsAt: Long,
    val endsAt: Long?,
    val audience: String?,
    val isBlocking: Boolean,
    val ctaLabel: String?,
    val ctaUrl: String?,
    val imageUrl: String?,
    val createdAt: Long,
    val updatedAt: Long,
)

private data class AnnouncementsResponse(val announcements: List<ApiAnnouncement>)

inline fun <reified T> Gson.fromJsonList(json: String): List<T> =
    fromJson(json, object : TypeToken<List<T>>() {}.type) ?: emptyList()
