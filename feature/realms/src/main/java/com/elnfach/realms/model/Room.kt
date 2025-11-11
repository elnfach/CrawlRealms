package com.elnfach.realms.model

data class Action(
    val id: String,
    val description: String,
    val nextSceneId: String? = null,
    val consequences: List<Consequence> = emptyList()
)

data class Consequence(
    val type: ConsequenceType,
    val target: String,
    val value: Int
)

enum class ConsequenceType {
    CHANGE_RELATIONSHIP, ADD_ITEM, REMOVE_ITEM, CHANGE_STATS
}

data class Condition(
    val type: ConditionType,
    val target: String,
    val requiredValue: Int
)

enum class ConditionType {
    MIN_RELATIONSHIP, HAS_ITEM, MIN_STAT
}

data class Scene(
    val id: String,
    val description: String,
    val availableActions: List<Action>,
    val conditions: List<Condition> = emptyList()
)