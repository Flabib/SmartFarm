package academy.bangkit.c22.px442.smartfarm.constant

import androidx.datastore.preferences.core.stringPreferencesKey

object DataStore {
    const val STORE_NAME = "com.amary.sisosmed"
    val KEY_NAME = stringPreferencesKey("key_name")
    val KEY_TOKEN = stringPreferencesKey("key_token")
    val KEY_LOCAL = stringPreferencesKey("key_local")
}