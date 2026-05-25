package one.echobell.echobellandroid

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SubscriptionDeepLinkTest {
    @Test
    fun extractsRawSubscriptionToken() {
        assertEquals(TestToken, extractSubscriptionToken(TestToken))
    }

    @Test
    fun extractsCustomSchemeSubscriptionToken() {
        assertEquals(TestToken, extractSubscriptionToken("echobell://subscribe/$TestToken"))
    }

    @Test
    fun extractsHttpsSubscriptionToken() {
        assertEquals(TestToken, extractSubscriptionToken("https://echobell.one/subscription/$TestToken?source=test"))
    }

    @Test
    fun rejectsMalformedSubscriptionLinks() {
        assertNull(extractSubscriptionToken("https://echobell.one/subscription/short"))
        assertNull(extractSubscriptionToken("https://example.com/subscription/$TestToken"))
        assertNull(extractSubscriptionToken("echobell://open/$TestToken"))
    }

    private companion object {
        const val TestToken = "abc123abc123abc123ab"
    }
}
