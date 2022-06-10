package academy.bangkit.c22.px442.smartfarm.base

import academy.bangkit.c22.px442.smartfarm.core.source.remote.network.ApiResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseRemoteSource(private val dispatcher: CoroutineDispatcher) {
//    protected suspend fun <T> getResult(call: suspend () -> Response<T>
//    ) : Flow<ApiResult<T>> = flow {
//        try {
//            val responseCall = call()
//            if (responseCall.isSuccessful && responseCall.body() != null){
//            } else {
//            }
//        } catch (t: Throwable){
//        }
//    }.flowOn(dispatcher)
}