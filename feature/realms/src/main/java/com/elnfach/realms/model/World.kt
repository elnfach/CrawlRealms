package com.elnfach.realms.model

data class World(
    val characters: List<Character>,
) {
    fun update(): List<Pair<Character, String>>  {
        return characters.map { it to it.makeDecision() }
    }
}
