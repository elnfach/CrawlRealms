package com.elnfach.realms.gen.characters

import com.elnfach.realms.model.Character
import com.elnfach.realms.model.Character.TraitType

fun <T> List<Pair<T, Int>>.weightedRandom(): T {
    val totalWeight = this.sumOf { it.second }
    var random = (0 until totalWeight).random()

    for ((value, weight) in this) {
        if (random < weight) return value
        random -= weight
    }

    return this.last().first
}

class CharacterGen
{
    private data class TraitPair(
        val positive: String,
        val negative: String,
        val type: TraitType,
    )

    private val traitsTemplate = listOf(
        TraitPair("Добрый", "Злой", TraitType.PERSONALITY),
        TraitPair("Сострадательный", "Безразличный", TraitType.PERSONALITY),
        TraitPair("Верный", "Предательский", TraitType.PERSONALITY),
        TraitPair("Щедрый", "Жадный", TraitType.PERSONALITY),
        TraitPair("Скромный", "Высокомерный", TraitType.PERSONALITY),

        TraitPair("Общительный", "Замкнутый", TraitType.SOCIAL),
        TraitPair("Харизматичный", "Неуклюжий", TraitType.SOCIAL),
        TraitPair("Дипломатичный", "Прямолинейный", TraitType.SOCIAL),
        TraitPair("Дружелюбный", "Враждебный", TraitType.SOCIAL),
        TraitPair("Тактичный", "Бестактный", TraitType.SOCIAL),

        TraitPair("Спокойный", "Вспыльчивый", TraitType.EMOTIONAL),
        TraitPair("Оптимистичный", "Пессимистичный", TraitType.EMOTIONAL),
        TraitPair("Сентиментальный", "Беспристрастный", TraitType.EMOTIONAL),
        TraitPair("Уравновешенный", "Импульсивный", TraitType.EMOTIONAL),
        TraitPair("Жизнерадостный", "Унылый", TraitType.EMOTIONAL),

        TraitPair("Умный", "Глупый", TraitType.INTELLECTUAL),
        TraitPair("Любознательный", "Равнодушный", TraitType.INTELLECTUAL),
        TraitPair("Креативный", "Шаблонный", TraitType.INTELLECTUAL),
        TraitPair("Аналитический", "Поверхностный", TraitType.INTELLECTUAL),
        TraitPair("Мудрый", "Незрелый", TraitType.INTELLECTUAL),

        TraitPair("Честный", "Лживый", TraitType.MORAL),
        TraitPair("Справедливый", "Несправедливый", TraitType.MORAL),
        TraitPair("Альтруистичный", "Эгоистичный", TraitType.MORAL),
        TraitPair("Принципиальный", "Беспринципный", TraitType.MORAL),
        TraitPair("Ответственный", "Безответственный", TraitType.MORAL)
    )

    private fun genDirection(): Boolean = (0..1).random() == 0
    private fun getTraitValue(direction: Boolean, template: TraitPair) = if (direction) template.positive else template.negative

    private fun generateRealisticTraits(): List<Character.Trait> {
        val direction = genDirection()
        val weights = listOf(
            1 to 2,     // 1%
            2 to 3,     // 3%
            3 to 7,     // 7%
            4 to 10,    // 10%
            5 to 20,    // 20%
            6 to 20,    // 20%
            7 to 10,    // 10%
            8 to 7,     // 7%
            9 to 3,     // 3%
            10 to 2     // 2%
        )
        return traitsTemplate
            .shuffled()
            .take(6)
            .map { template ->
                Character.Trait(
                    value = getTraitValue(direction, template),
                    direction = direction,
                    intensity = weights.weightedRandom()
                )
            }
    }

    private fun generateExtremeTraits(): List<Character.Trait> {
        val direction = genDirection()
        return traitsTemplate
            .shuffled()
            .take(6)
            .map { template ->
                Character.Trait(
                    value = getTraitValue(direction, template),
                    direction = direction,
                    intensity = (3..10).random()
                )
            }
    }

    private fun generateBalancedTraits(): List<Character.Trait> {
        val traitsByType = traitsTemplate.groupBy { it.type }
        val selectedTraits = mutableListOf<Character.Trait>()

        val direction = genDirection()

        traitsByType.values.forEach { traitsOfType ->
            if (traitsOfType.isNotEmpty()) {
                val template = traitsOfType.random()
                selectedTraits.add(
                    Character.Trait(
                        value = getTraitValue(direction, template),
                        direction = direction,
                        intensity = (4..7).random()
                    )
                )
            }
        }

        return selectedTraits.shuffled().take(6)
    }

    private fun generateNeeds(): Map<Character.NeedType, Character.Need> {
        return mapOf(
            Character.NeedType.HUNGER to Character.Need(
                Character.NeedType.HUNGER,
                (30..70).random(),
                (1..3).random()
            ),
        )
    }

    fun realistic(): Character
    {
        return Character(
            id = "S",
            name = "John",
            traits = generateRealisticTraits(),
            needs = generateNeeds(),
            status = Character.Status.ALIVE
        )
    }

    fun balanced(): Character {
        return Character(
            id = "S",
            name = "John",
            traits = generateBalancedTraits(),
            needs = generateNeeds(),
            status = Character.Status.ALIVE
        )
    }

    fun extreme(): Character {
        return Character(
            id = "S",
            name = "John",
            traits = generateExtremeTraits(),
            needs = generateNeeds(),
            status = Character.Status.ALIVE
        )
    }
}