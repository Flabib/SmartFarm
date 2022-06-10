package academy.bangkit.c22.px442.smartfarm.core

sealed class Resource<T>(val data: T? = null, val codeMessage: String? = null, val message: String? = null) {
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Success<T>(data: T) : Resource<T>(data)
    class Failed<T>(message: String): Resource<T>(data = null, message = message)
    class Unauthorized<T>(message: String): Resource<T>(data = null, message =  message)
    class ServerError<T>(message: String): Resource<T>(data = null, message =  message)
}
