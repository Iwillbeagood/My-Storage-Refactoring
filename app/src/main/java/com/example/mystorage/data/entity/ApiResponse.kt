package com.example.mystorage.data.entity

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)