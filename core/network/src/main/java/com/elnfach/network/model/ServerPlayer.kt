package com.elnfach.network.model

data class ServerPlayer(
    val id: String,
    val username: String,
    val elo: Int,
    val playtime: Long
)