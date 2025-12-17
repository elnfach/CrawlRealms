package com.elnfach.realms.gen.world

import android.util.Log
import com.elnfach.realms.content.Biome
import com.elnfach.realms.content.Humidity
import com.elnfach.realms.content.Temperature
import com.elnfach.realms.gen.world.location.Location
import com.elnfach.realms.gen.world.noice.ClimateNoiseGen
import com.elnfach.realms.gen.world.noice.HeightNoiseGen

class WorldGen(
    val seed: Long = (Math.random() * 10000).toLong(),
    val biomes: List<Biome>
) {
    private val heightNoice = HeightNoiseGen(seed + 1)
    private val climateNoice = ClimateNoiseGen(seed + 2)
    private val biomeLookupTable = BiomeGen(biomes)

    fun gen(width: Int, height: Int): Array<Array<Location>> {
        val hmap = heightNoice.gen(width, height)
        val cmap = climateNoice.gen(width, height, hmap)

        val temperatureStats = cmap.temperature.flatMap { it.toList() }
        val humidityStats = cmap.humidity.flatMap { it.toList() }
        val minTemperature = temperatureStats.minOrNull() ?: 0.0
        val maxTemperature = temperatureStats.maxOrNull() ?: 0.0
        val avgTemperature = temperatureStats.average()

        val minHumidity = humidityStats.minOrNull() ?: 0.0
        val maxHumidity = humidityStats.maxOrNull() ?: 0.0
        val avgHumidity = humidityStats.average()

        Log.d("WORLD_GEN", "DEBUG TEMPERATURE: min=$minTemperature, max=$maxTemperature, avg=$avgTemperature")
        Log.d("WORLD_GEN", "DEBUG HUMIDITY: min=$minHumidity, max=$maxHumidity, avg=$avgHumidity")
        Log.d("WORLD_GEN", "DEBUG TEMPERATURE distribution:")
        val FREEZING_LEVEL = temperatureStats.count { it < Temperature.FREEZING_LEVEL }
        val COLD_LEVEL = temperatureStats.count { it in Temperature.FREEZING_LEVEL..Temperature.COLD_LEVEL }
        val TEMPERATE_LEVEL = temperatureStats.count { it in Temperature.COLD_LEVEL..Temperature.TEMPERATE_LEVEL }
        val WARM_LEVEL = temperatureStats.count { it in Temperature.TEMPERATE_LEVEL..Temperature.WARM_LEVEL }
        val HOT_MAX = temperatureStats.count { it > Temperature.WARM_LEVEL }
        Log.d("WORLD_GEN", "FREEZING_LEVEL (<${Temperature.FREEZING_LEVEL}): $FREEZING_LEVEL")
        Log.d("WORLD_GEN", "COLD_LEVEL (${Temperature.FREEZING_LEVEL}-${Temperature.COLD_LEVEL}): $COLD_LEVEL")
        Log.d("WORLD_GEN", "TEMPERATE_LEVEL (${Temperature.COLD_LEVEL}-${Temperature.TEMPERATE_LEVEL}): $TEMPERATE_LEVEL")
        Log.d("WORLD_GEN", "WARM_LEVEL (${Temperature.TEMPERATE_LEVEL}-${Temperature.WARM_LEVEL}): $WARM_LEVEL")
        Log.d("WORLD_GEN", "HOT_MAX (>${Temperature.WARM_LEVEL}): $HOT_MAX")

        Log.d("WORLD_GEN", "DEBUG HUMIDITY distribution:")
        val ARID = humidityStats.count { it < Humidity.ARID_LEVEL }
        val DRY_LEVEL = humidityStats.count { it in Humidity.ARID_LEVEL..Humidity.DRY_LEVEL }
        val MOIST_LEVEL = humidityStats.count { it in Humidity.DRY_LEVEL..Humidity.MOIST_LEVEL }
        val WET_MAX = humidityStats.count { it > Humidity.MOIST_LEVEL }
        Log.d("WORLD_GEN", "FREEZING_LEVEL (<${Humidity.ARID_LEVEL}): $ARID")
        Log.d("WORLD_GEN", "COLD_LEVEL (${Humidity.ARID_LEVEL}-${Humidity.DRY_LEVEL}): $DRY_LEVEL")
        Log.d("WORLD_GEN", "MOIST_LEVEL (${Humidity.DRY_LEVEL}-${Humidity.MOIST_LEVEL}): $MOIST_LEVEL")
        Log.d("WORLD_GEN", "WET_MAX (>${Humidity.MOIST_LEVEL}): $WET_MAX")
        return Array(height) { y ->
            Array(width) { x ->
                val worldX = x - width / 2
                val worldY = y - height / 2
                val h = hmap[y][x]
                val m = cmap.humidity[y][x]
                val t = cmap.temperature[y][x]

                val biome = biomeLookupTable.gen(h, m, t)

                Location(worldX, worldY, biome)
            }
        }
    }
}