package academy.bangkit.c22.px442.smartfarm.core.source.remote

import academy.bangkit.c22.px442.smartfarm.base.BaseRemoteSource
import academy.bangkit.c22.px442.smartfarm.core.source.remote.network.ApiResult
import academy.bangkit.c22.px442.smartfarm.core.source.remote.network.ApiService
import academy.bangkit.c22.px442.smartfarm.core.source.remote.response.ApiResponse
import academy.bangkit.c22.px442.smartfarm.core.source.remote.response.LoginResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import java.io.File

class RemoteSource(
    private val apiService: ApiService,
    dispatcher: CoroutineDispatcher
) : BaseRemoteSource(dispatcher) {
//    suspend fun register(name: String, email: String, password: String) : Flow<ApiResult<MessageResponse>> =
//        getResult { apiService.register(name, email, password) }
//
//    suspend fun login(email: String, password: String): Flow<ApiResult<ApiResponse<LoginResponse>>> =
//        getResult { apiService.login(email, password) }
}