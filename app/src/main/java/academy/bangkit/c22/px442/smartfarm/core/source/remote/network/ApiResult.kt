package academy.bangkit.c22.px442.smartfarm.core.source.remote.network

sealed class ApiResult<out R> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val codeError: Int, val errorMessage: String) : ApiResult<Nothing>()
}