package one.echobell.echobellandroid.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

private val Context.echobellDataStore by preferencesDataStore(name = "echobell")

data class SavedAppState(
    val jwt: String? = null,
    val deviceToken: String? = null,
    val notificationToken: String? = null,
    val user: User? = null,
    val channels: List<Channel> = emptyList(),
    val directKeys: List<DirectKey> = emptyList(),
    val records: List<Record> = emptyList(),
    val announcements: List<Announcement> = emptyList(),
    val readAnnouncementIds: Set<Int> = emptySet(),
)

class LocalStore(
    context: Context,
    private val gson: Gson = Gson(),
) {
    private val dataStore = context.echobellDataStore

    suspend fun load(): SavedAppState {
        val preferences = dataStore.data.first()
        return SavedAppState(
            jwt = preferences[Keys.Jwt],
            deviceToken = preferences[Keys.DeviceToken],
            notificationToken = preferences[Keys.NotificationToken],
            user = preferences[Keys.User]?.let { runCatching { gson.fromJson(it, User::class.java) }.getOrNull() },
            channels = preferences[Keys.Channels].decodeList(Channel::class.java),
            directKeys = preferences[Keys.DirectKeys].decodeList(DirectKey::class.java),
            records = preferences[Keys.Records].decodeList(Record::class.java),
            announcements = preferences[Keys.Announcements].decodeList(Announcement::class.java),
            readAnnouncementIds = preferences[Keys.ReadAnnouncements]
                ?.split(",")
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: emptySet(),
        )
    }

    suspend fun saveAuth(jwt: String?, deviceToken: String?) {
        dataStore.edit {
            if (jwt == null) it.remove(Keys.Jwt) else it[Keys.Jwt] = jwt
            if (deviceToken == null) it.remove(Keys.DeviceToken) else it[Keys.DeviceToken] = deviceToken
        }
    }

    suspend fun saveNotificationToken(token: String?) {
        dataStore.edit {
            if (token == null) it.remove(Keys.NotificationToken) else it[Keys.NotificationToken] = token
        }
    }

    suspend fun saveUser(user: User?) {
        dataStore.edit {
            if (user == null) it.remove(Keys.User) else it[Keys.User] = gson.toJson(user)
        }
    }

    suspend fun saveChannels(channels: List<Channel>) = saveJson(Keys.Channels, channels)

    suspend fun saveDirectKeys(directKeys: List<DirectKey>) = saveJson(Keys.DirectKeys, directKeys)

    suspend fun saveRecords(records: List<Record>) = saveJson(Keys.Records, records.sortedByDescending { it.createdAt }.take(500))

    suspend fun saveAnnouncements(announcements: List<Announcement>) = saveJson(Keys.Announcements, announcements)

    suspend fun saveReadAnnouncementIds(ids: Set<Int>) {
        dataStore.edit { it[Keys.ReadAnnouncements] = ids.sorted().joinToString(",") }
    }

    suspend fun clearUserData() {
        dataStore.edit {
            it.remove(Keys.Jwt)
            it.remove(Keys.DeviceToken)
            it.remove(Keys.User)
            it.remove(Keys.Channels)
            it.remove(Keys.DirectKeys)
            it.remove(Keys.Records)
        }
    }

    private suspend fun saveJson(key: androidx.datastore.preferences.core.Preferences.Key<String>, value: Any) {
        dataStore.edit { it[key] = gson.toJson(value) }
    }

    private fun <T> String?.decodeList(itemType: Class<T>): List<T> =
        this?.let {
            runCatching {
                val listType = TypeToken.getParameterized(List::class.java, itemType).type
                gson.fromJson<List<T>>(it, listType) ?: emptyList()
            }.getOrDefault(emptyList())
        } ?: emptyList()

    private object Keys {
        val Jwt = stringPreferencesKey("user-auth-jwt")
        val DeviceToken = stringPreferencesKey("user-auth-device-token")
        val NotificationToken = stringPreferencesKey("notification-device-token")
        val User = stringPreferencesKey("user")
        val Channels = stringPreferencesKey("channels")
        val DirectKeys = stringPreferencesKey("direct-keys")
        val Records = stringPreferencesKey("records")
        val Announcements = stringPreferencesKey("announcements")
        val ReadAnnouncements = stringPreferencesKey("announcement-read-ids")
    }
}
