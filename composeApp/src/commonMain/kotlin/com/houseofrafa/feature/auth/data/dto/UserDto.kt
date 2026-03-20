package com.houseofrafa.feature.auth.data.dto

import com.houseofrafa.core.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id")      val userId: Int,
    @SerialName("name")    val name: String,
    @SerialName("token")   val token: String,
)

fun UserDto.toDomain() = User(userId = userId, name = name, token = token)
