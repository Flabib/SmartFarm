package academy.bangkit.c22.px442.smartfarm.core.source.remote.response

import academy.bangkit.c22.px442.smartfarm.constant.EmptyValue

data class ApiResponse<T>(
	val error: Boolean = EmptyValue.BOOLEAN,
	val message: String = EmptyValue.STRING,
	var data: T
)