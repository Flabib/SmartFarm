package academy.bangkit.c22.px442.smartfarm.base

import academy.bangkit.c22.px442.smartfarm.core.Resource
import academy.bangkit.c22.px442.smartfarm.core.source.remote.network.ApiResult
import kotlinx.coroutines.flow.*

abstract class BaseRepository<ResultType, RequestType> {

    private var result: Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        when(val result = createCall().first()){
            is ApiResult.Success -> {
                emitAll(mapData(result.data).map { Resource.Success(it) })
            }
            is ApiResult.Error -> {
                when(result.codeError){
                    401 -> emit(Resource.Unauthorized(result.errorMessage))
                    500, 502 -> emit(Resource.ServerError(result.errorMessage))
                    else -> emit(Resource.Failed(result.errorMessage))
                }
            }
        }
    }

    protected abstract suspend fun createCall(): Flow<ApiResult<RequestType>>

    protected abstract fun mapData(data: RequestType): Flow<ResultType>

    fun asFlow(): Flow<Resource<ResultType>> = result
}