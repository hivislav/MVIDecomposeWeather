package com.example.mvidecomposeweather.data.network.dto

import kotlinx.serialization.SerialName

data class ConditionDto(
    @SerialName("text")
    val text: String?,
    @SerialName("icon")
    val iconUrl: String?
)
