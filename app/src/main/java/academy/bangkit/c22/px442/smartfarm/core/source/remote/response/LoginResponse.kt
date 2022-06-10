package academy.bangkit.c22.px442.smartfarm.core.source.remote.response

import academy.bangkit.c22.px442.smartfarm.constant.EmptyValue
//import com.amary.sisosmed.domain.model.Login

data class LoginResponse(
    val name: String =  EmptyValue.STRING,
    val userId: String = EmptyValue.STRING,
    val token: String = EmptyValue.STRING
) {
//    fun mapToModel() = Login(
//        name = name,
//        userId = userId,
//        token = token
//    )
}