package com.barron.isspasstimes.models

/**
 * Created by shaunn on 1/3/2018.
 */
open class ISSAPIResponse {
    val message: String = ""
    val request: Map<String, Any> = emptyMap()
}

data class PassTimesResponse(
        val response: List<PassTime> = emptyList()
) : ISSAPIResponse()

data class PassTime(
        val risetime: Long = 0,
        val duration: Int = 0
)