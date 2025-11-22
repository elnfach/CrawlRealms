package com.elnfach.realms.content

import kotlinx.serialization.Serializable

@Serializable
data class Traits(
    val personality: List<Trait> = emptyList()
)

@Serializable
data class Trait(
    val name: String,
    val opposite: String,
    val description: String,
)