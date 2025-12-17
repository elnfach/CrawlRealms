package com.elnfach.realms.model

import com.elnfach.realms.gen.characters.model.Character

data class World(
    val characters: List<Character>,
) {
    fun update(): List<Pair<Character, String>>  {
        return characters.map { it to it.makeDecision() }
    }
}
