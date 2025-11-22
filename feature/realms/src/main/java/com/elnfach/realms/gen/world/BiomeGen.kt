package com.elnfach.realms.gen.world

import com.elnfach.realms.content.Altitude
import com.elnfach.realms.content.Biome
import com.elnfach.realms.content.Humidity
import com.elnfach.realms.content.Temperature
import com.elnfach.realms.content.Type

class BiomeGen(private val biomes: List<Biome>) {
    private val primary = biomes.filter { it.type == Type.PRIMARY }
    private val secondary = biomes.filter { it.type == Type.SECONDARY }

    fun gen(altitude: Double, humidity: Double, temperature: Double): Biome {
        val alt = Altitude.cat(altitude)
        val hum = Humidity.cat(humidity)
        val temp = Temperature.cat(temperature)

        val matchingPrimary = getBiomes(alt, hum, temp, primary)

        if (matchingPrimary.isNotEmpty()) {
            return matchingPrimary.first()
        }

        val matchingSecondary = getBiomes(alt, hum, temp, secondary)

        if (matchingSecondary.isNotEmpty()) {
            return matchingSecondary.first()
        }


        return getFallbackBiome(altitude, humidity, temperature)
    }

    fun getBiomes(
        altitude: Altitude,
        humidity: Humidity,
        temperature: Temperature,
        target: List<Biome>
    ): List<Biome> {
        return target.filter { biome ->
            altitude in biome.conditions.altitude &&
                    humidity in biome.conditions.humidity &&
                    temperature in biome.conditions.temperature
        }
    }

    private fun getFallbackBiome(altitude: Double,
                                 humidity: Double,
                                 temperature: Double,): Biome {
        return biomes.firstOrNull { biome ->
            Altitude.cat(altitude) in biome.conditions.altitude &&
                    Humidity.cat(humidity) in biome.conditions.humidity &&
                    Temperature.cat(temperature) in biome.conditions.temperature
        } ?: biomes.first()
    }
}