package com.elnfach.core.model

data class Player(
    val id: String,
    val username: String,
    val elo: Int,
    val playtime: Long,
)