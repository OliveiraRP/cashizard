package com.houseofrafa.feature.auth.data.dto

import com.houseofrafa.core.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id")   val id: String,
    @SerialName("name") val name: String,
)

fun UserDto.toDomain() = User(userId = id, name = name)
