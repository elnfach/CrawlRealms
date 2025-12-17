package com.elnfach.realms.gen.civilization.culture.model

data class Culture(
    val type: Type,
    val economy: String,
    val way: String,
    val values: String,
    val relations: String,
    val architecture: String,
) {

}

enum class Type {
    NORDEST
}