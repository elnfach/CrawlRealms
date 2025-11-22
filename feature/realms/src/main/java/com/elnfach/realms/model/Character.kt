package com.elnfach.realms.model

import kotlin.random.Random

data class Character(
    val id: String,
    val name: String,
    val traits: List<Trait>,
    val needs: Map<NeedType, Need>,
    var status: Status = Status.ALIVE
) {
    enum class Status {
        ALIVE, DEAD, CAPTURED
    }
    enum class TraitType {
        PERSONALITY, SOCIAL, EMOTIONAL, INTELLECTUAL, MORAL,
        VOLITIONAL, RISK, HUMOR, POWER, WORK
    }

    data class Trait(
        val value: String,
        val direction: Boolean = Random.nextBoolean(),
        val intensity: Int = (3..10).random()
    )

    enum class NeedType {
        HUNGER,
        SAFETY,
        SOCIAL,
        ESTEEM,
        KNOWLEDGE,
        SELF_ACTUALIZATION
    }

    data class Need(
        val type: NeedType,
        var intensity: Int, // 0 - 100
        val decayRate: Int = 2
    ) {
        fun update() {
            intensity = (intensity + decayRate).coerceAtMost(100)
        }

        fun isCritical(): Boolean = intensity > 80
        fun isSatisfied(): Boolean = intensity < 20
    }

/*
    data class Skills(

    )

    data class Memory(

    )
*/

    sealed class Goal {
        abstract val priority: Int
        abstract val targetNeed: NeedType
        abstract fun isCompleted(world: World): Boolean
    }

    data class FindFoodGoal(override val priority: Int) : Goal() {
        override val targetNeed = NeedType.HUNGER
        override fun isCompleted(world: World): Boolean {
            return false
        }
    }

    data class SeekSafetyGoal(override val priority: Int) : Goal() {
        override val targetNeed = NeedType.SAFETY
        override fun isCompleted(world: World): Boolean {
            return false
        }
    }

    data class SocializeGoal(val targetCharacterId: String, override val priority: Int) : Goal() {
        override val targetNeed = NeedType.SOCIAL
        override fun isCompleted(world: World): Boolean {
            return false
            //worldState.character.socialNeed < 30
        }
    }

    data class GainRespectGoal(override val priority: Int) : Goal() {
        override val targetNeed = NeedType.ESTEEM
        override fun isCompleted(world: World): Boolean {
            return false
        }
    }

    data class LearnSecretGoal(override val priority: Int) : Goal() {
        override val targetNeed = NeedType.KNOWLEDGE
        override fun isCompleted(world: World): Boolean {
            return false
        }
    }

    data class AchieveDreamGoal(override val priority: Int) : Goal() {
        override val targetNeed = NeedType.SELF_ACTUALIZATION
        override fun isCompleted(world: World): Boolean {
            return false
        }
    }

    fun updateNeeds() {
        needs.values.forEach { it.update() }
    }

    fun generateGoalsFromNeeds(): List<Goal> {
        val urgentNeeds = needs.values.filter { it.isCritical() }

        return urgentNeeds.map { need ->
            when (need.type) {
                NeedType.HUNGER -> FindFoodGoal(priority = need.intensity)
                NeedType.SAFETY -> SeekSafetyGoal(priority = need.intensity)
                NeedType.SOCIAL -> SocializeGoal(
                    targetCharacterId = "TEST",
                    priority = need.intensity
                )
                NeedType.ESTEEM -> GainRespectGoal(priority = need.intensity)
                NeedType.KNOWLEDGE -> LearnSecretGoal(priority = need.intensity)
                NeedType.SELF_ACTUALIZATION -> AchieveDreamGoal(priority = need.intensity)
            }
        }
    }

    fun makeDecision(): String {2
        updateNeeds()

        val currentGoals = generateGoalsFromNeeds()

        val topGoal = currentGoals.maxByOrNull { it.priority }

        return when (topGoal) {
            is FindFoodGoal -> "ищет еду"
            is SeekSafetyGoal -> "ищет дом"
            else -> "ничего не делает" // Все потребности удовлетворены
        }
    }
}