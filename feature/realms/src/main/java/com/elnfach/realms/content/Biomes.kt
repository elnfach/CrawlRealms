package com.elnfach.realms.content

import kotlinx.serialization.Serializable

@Serializable
data class Biome(
    val id: String,
    val name: Set<String>,
    val type: Type,
    val family: Family,
    val conditions: Conditions
)

@Serializable
enum class Type {
    PRIMARY,
    SECONDARY
}

@Serializable
data class Family(
    val type: String,
    val name: String,
)

@Serializable
data class Conditions(
    val altitude: Set<Altitude>,
    val humidity: Set<Humidity>,
    val temperature: Set<Temperature>
)

@Serializable
enum class Altitude {
    DEEP_OCEAN,
    OCEAN,
    COAST,
    BEACH,
    PLAINS,
    FOREST,
    HILLS,
    MOUNTAIN_BASE,
    MOUNTAIN,
    SNOW_PEAKS;

    companion object {
        const val DEEP_OCEAN_MAX = -0.7
        const val OCEAN_LEVEL = 0.0
        const val COASTAL_MAX = 0.01
        const val BEACH_MAX = 0.05
        const val PLAINS_MAX = 0.2
        const val FOREST_MAX = 0.6
        const val HILLS_MAX = 0.7
        const val MOUNTAIN_BASE_MAX = 0.8
        const val MOUNTAIN_MAX = 0.9

        fun cat(height: Double): Altitude = when {
            height < DEEP_OCEAN_MAX -> DEEP_OCEAN
            height < OCEAN_LEVEL -> OCEAN
            height < COASTAL_MAX -> COAST
            height < BEACH_MAX -> BEACH
            height < PLAINS_MAX -> PLAINS
            height < FOREST_MAX -> FOREST
            height < HILLS_MAX -> HILLS
            height < MOUNTAIN_BASE_MAX -> MOUNTAIN_BASE
            height < MOUNTAIN_MAX -> MOUNTAIN
            else -> SNOW_PEAKS
        }
    }
}

@Serializable
enum class Humidity {
    ARID,    // Пустыни, сухие степи
    DRY,     // Саванны, редкие леса
    MOIST,   // Леса, луга
    WET;     // Болота, джунгли, тундра

    companion object {
        const val ARID_LEVEL = 0.25
        const val DRY_LEVEL = 0.5
        const val MOIST_LEVEL = 0.75

        fun cat(moisture: Double): Humidity = when {
            moisture < ARID_LEVEL -> ARID
            moisture < DRY_LEVEL -> DRY
            moisture < MOIST_LEVEL -> MOIST
            else -> WET
        }
    }
}

@Serializable
enum class Temperature {
    FREEZING,  // < 0.2    - Ледники, снежные биомы
    COLD,      // 0.2-0.4  - Тайга, тундра
    TEMPERATE, // 0.4-0.6  - Умеренные леса, степи
    WARM,      // 0.6-0.8  - Лиственные леса, саванны
    HOT;        // > 0.8    - Пустыни, джунгли

    companion object {
        const val FREEZING_LEVEL = -0.3
        const val COLD_LEVEL = 0.0
        const val TEMPERATE_LEVEL = 0.6
        const val WARM_LEVEL = 0.8

        fun cat(temperature: Double): Temperature = when {
            temperature < FREEZING_LEVEL -> FREEZING
            temperature < COLD_LEVEL -> COLD
            temperature < TEMPERATE_LEVEL -> TEMPERATE
            temperature < WARM_LEVEL -> WARM
            else -> HOT
        }
    }
}