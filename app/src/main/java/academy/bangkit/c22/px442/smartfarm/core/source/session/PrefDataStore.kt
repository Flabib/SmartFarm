package academy.bangkit.c22.px442.smartfarm.core.source.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import academy.bangkit.c22.px442.smartfarm.constant.DataStore
import academy.bangkit.c22.px442.smartfarm.constant.EmptyValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*


private val Context.dataStore by preferencesDataStore(name = DataStore.STORE_NAME)

class PrefDataStore(context: Context, private val dispatcher: CoroutineDispatcher) {
    private val dataStore = context.dataStore

    suspend fun setName(name: String){
        dataStore.edit { it[DataStore.KEY_NAME] = name }
    }

    val getName: Flow<String> = dataStore.data
        .map { it[DataStore.KEY_NAME] ?: EmptyValue.STRING }

    suspend fun setToken(token: String){
        dataStore.edit { it[DataStore.KEY_TOKEN] = token }
    }

    val getToken: Flow<String> = dataStore.data
        .map { it[DataStore.KEY_TOKEN] ?: EmptyValue.STRING }

    fun clearAuth(): Flow<Boolean> = flow {
        dataStore.edit { it.remove(DataStore.KEY_TOKEN) }
        getToken.collect {
            if (it != EmptyValue.STRING) emit(true) else emit(false)
        }
    }.flowOn(dispatcher)

    suspend fun setLocal(local: String){
        dataStore.edit { it[DataStore.KEY_LOCAL] = local }
    }

    val getLocal: Flow<String> = dataStore.data
        .map {
            (if (it[DataStore.KEY_LOCAL] != null){
                it[DataStore.KEY_LOCAL]
            } else {
                val default = EmptyValue.STRING
                setLocal(default)
                default
            }).toString()
        }
}