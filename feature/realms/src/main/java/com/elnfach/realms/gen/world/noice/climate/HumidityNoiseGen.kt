package com.elnfach.realms.gen.world.noice.climate

import com.elnfach.realms.content.Altitude
import com.elnfach.realms.gen.world.PerlinNoise
import kotlin.times

class HumidityNoiseGen(seed: Long, delta: Int) {
    private val humidity = PerlinNoise(seed + delta)

    fun gen(width: Int, height: Int, altitude: Array<DoubleArray>, temperature: Array<DoubleArray>): Array<DoubleArray> =
        Array(height) { y ->
            DoubleArray(width) { x ->
                val elevation = altitude[y][x]
                val isOcean = elevation < Altitude.OCEAN_LEVEL
                val temp = temperature[y][x]

                val baseMoisture = (humidity.fractalNoise(x * 0.4, y * 0.4, 3, 0.5) + 1.0) * 0.5

                // Температурный эффект
                val tempEffect = when {
                    temp > 0.5 -> 0.8  // Жарко - испаряется больше, но может быть суше
                    temp < -0.3 -> 0.6 // Холодно - меньше испарения
                    else -> 1.0        // Умеренная температура - нормальная влажность
                }

                val heightEffect = when {
                    isOcean -> 1.2
                    elevation > 0.7 -> 0.4
                    else -> 1.0
                }

                val rainShadow = calculateRainShadow(altitude, x, y, 3)

                var moisture = (baseMoisture * 0.6 + 0.4) * heightEffect * tempEffect - rainShadow * 0.2

                if (isOcean) moisture = maxOf(moisture, 0.8)

                moisture.coerceIn(0.0, 1.0)
            }
        }

    fun calculateRainShadow(altitude: Array<DoubleArray>, x: Int, y: Int, range: Int = 3): Double {
        var maxShadow = 0.0

        // Основное направление ветра (например, с запада)
        val mainWindDirection = listOf(-1 to 0)

        for ((dx, dy) in mainWindDirection) {
            for (distance in 1..range) {
                val checkX = x + dx * distance
                val checkY = y + dy * distance

                if (checkX !in altitude[0].indices || checkY !in altitude.indices) continue

                val mountainHeight = altitude[checkY][checkX]
                val currentHeight = altitude[y][x]

                // Только значительные горы создают тень дождя
                if (mountainHeight > 0.6 && mountainHeight > currentHeight + 0.1) {
                    val shadowStrength = (mountainHeight - 0.6) * // Только высота выше порога
                            (1.0 - distance.toDouble() / (range + 1))
                    maxShadow = maxOf(maxShadow, shadowStrength)
                }
            }
        }

        return maxShadow.coerceIn(0.0, 0.5) // Ограничиваем тень дождя
    }
}