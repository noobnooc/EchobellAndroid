package one.echobell.echobellandroid

import java.net.URI

private const val SubscriptionTokenLength = 20
private val SubscriptionTokenPattern = Regex("^[A-Za-z0-9]{$SubscriptionTokenLength}$")

fun extractSubscriptionToken(raw: String?): String? {
    val value = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    if (value.isSubscriptionToken()) return value

    val uri = runCatching { URI(value) }.getOrNull() ?: return null
    val token = when {
        uri.scheme.equals("echobell", ignoreCase = true) && uri.host.equals("subscribe", ignoreCase = true) ->
            uri.path.tokenPathSegment()

        uri.scheme.equals("https", ignoreCase = true) &&
            uri.host.equals("echobell.one", ignoreCase = true) &&
            uri.path.startsWith("/subscription/") ->
            uri.path.removePrefix("/subscription/").substringBefore("/")

        else -> null
    }

    return token?.takeIf { it.isSubscriptionToken() }
}

private fun String?.tokenPathSegment(): String? =
    this?.trim('/')?.substringBefore("/")?.takeIf { it.isNotBlank() }

private fun String.isSubscriptionToken(): Boolean =
    SubscriptionTokenPattern.matches(this)
