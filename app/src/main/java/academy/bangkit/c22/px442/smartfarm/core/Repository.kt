package academy.bangkit.c22.px442.smartfarm.core

import academy.bangkit.c22.px442.smartfarm.base.BaseRepository
import academy.bangkit.c22.px442.smartfarm.constant.EmptyValue
import academy.bangkit.c22.px442.smartfarm.core.source.remote.RemoteSource
import academy.bangkit.c22.px442.smartfarm.core.source.remote.network.ApiResult
import academy.bangkit.c22.px442.smartfarm.core.source.remote.response.ApiResponse
import academy.bangkit.c22.px442.smartfarm.core.source.remote.response.LoginResponse
import academy.bangkit.c22.px442.smartfarm.core.source.session.PrefDataStore
import kotlinx.coroutines.flow.*
import java.io.File

class Repository(
    private val remoteSource: RemoteSource,
    private val prefDataStore: PrefDataStore
) {

//    fun register(name: String, email: String, password: String): Flow<Resource<Message>> =
//        object : BaseRepository<Message, MessageResponse>() {
//            override suspend fun createCall(): Flow<ApiResult<MessageResponse>> =
//                remoteSource.register(name, email, password)
//
//            override fun mapData(data: MessageResponse): Flow<Message> =
//                flow { emit(data.mapToModel()) }
//        }.asFlow()
//
//    fun login(email: String, password: String): Flow<Resource<Login>> =
//        object : BaseRepository<Login, ApiResponse<LoginResponse>>() {
//            override suspend fun createCall(): Flow<ApiResult<ApiResponse<LoginResponse>>> =
//                remoteSource.login(email, password)
//
//            override fun mapData(data: ApiResponse<LoginResponse>): Flow<Login> =
//                flow {
//                    prefDataStore.setToken(data.data.token)
//                    prefDataStore.setName(data.data.name)
//                    emit(data.data.mapToModel())
//                }
//
//        }.asFlow()
//
//    fun checkAuth(): Flow<Resource<Boolean>> =
//        object : BaseRepository<Boolean, ApiResponse<List<StoryResponse>>>() {
//            override suspend fun createCall(): Flow<ApiResult<ApiResponse<List<StoryResponse>>>> =
//                remoteSource.allStories(prefDataStore.getToken.first(), 1, 1, 0)
//
//            override fun mapData(data: ApiResponse<List<StoryResponse>>): Flow<Boolean> = flow {
//                if (data.data.isNullOrEmpty()) {
//                    emit(false)
//                } else {
//                    emit(true)
//                }
//            }
//
//        }.asFlow()
//
//    fun clearAuth() = prefDataStore.clearAuth()
//
//    fun setLocal(local: String): Flow<String> = flow {
//        prefDataStore.setLocal(local)
//        emit(prefDataStore.getLocal.first())
//    }
//
//    fun getLocal(): Flow<String> = prefDataStore.getLocal

}