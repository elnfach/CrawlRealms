package com.elnfach.realms.gen.world.noice

import com.elnfach.realms.gen.world.PerlinNoise
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

data class Climate(
    val temperature: Array<DoubleArray>,
    val humidity: Array<DoubleArray>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Climate

        if (!temperature.contentDeepEquals(other.temperature)) return false
        if (!humidity.contentDeepEquals(other.humidity)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = temperature.contentDeepHashCode()
        result = 31 * result + humidity.contentDeepHashCode()
        return result
    }
}

class ClimateNoiseGen(seed: Long) {
    private val temperatureBase = PerlinNoise(seed + 10)
    private val temperatureDetail = PerlinNoise(seed + 11)
    private val humidityBase = PerlinNoise(seed + 20)
    private val humidityDetail = PerlinNoise(seed + 21)
    private val climateVariation = PerlinNoise(seed + 30)

    fun gen(width: Int, height: Int, heightMap: Array<DoubleArray>): Climate =
        Climate(
            genTemperature(width, height, heightMap),
            genHumidity(width, height, heightMap)
        )

    private fun genTemperature(width: Int, height: Int, heightMap: Array<DoubleArray>): Array<DoubleArray> =
        Array(height) { y ->
            // УВЕЛИЧИВАЕМ широтный градиент значительно
            val latitude = 1.0 - (2.0 * y / height - 1.0).absoluteValue
            val baseFromLatitude = latitude * 0.8  // экватор = 0.8, полюса = 0.0

            DoubleArray(width) { x ->
                val elevation = heightMap[y][x]

                // 1. Крупномасштабный шум с БОЛЬШЕЙ амплитудой
                val largeScale = temperatureBase.fractalNoise(
                    x * 0.01, y * 0.01,
                    octaves = 4, persistence = 0.5
                ) * 0.5  // УВЕЛИЧЕНО с 0.4

                // 2. Мелкомасштабный детальный шум
                val smallScale = temperatureDetail.fractalNoise(
                    x * 0.03, y * 0.03,
                    octaves = 3, persistence = 0.4
                ) * 0.25  // УВЕЛИЧЕНО с 0.2

                // 3. УМЕНЬШАЕМ высотный эффект
                val heightEffect = -elevation.pow(1.5) * 0.3  // УМЕНЬШЕНО с 0.5

                // 4. Влияние океана (смягчает температуру)
                val oceanEffect = calculateOceanInfluence(heightMap, x, y, 3) * 0.1

                // 5. БАЗОВЫЙ СДВИГ ВВЕРХ - самый важный!
                val baseShift = 0.3  // Добавляем постоянное смещение

                // Комбинируем все факторы
                var temperature = baseFromLatitude + largeScale + smallScale + heightEffect + oceanEffect + baseShift

                // Добавляем случайные вариации
                val randomVariation = climateVariation.fractalNoise(
                    x * 0.05, y * 0.05, octaves = 2, persistence = 0.3
                ) * 0.15
                temperature += randomVariation

                temperature.coerceIn(0.0, 1.0)
            }
        }

    private fun genHumidity(width: Int, height: Int, heightMap: Array<DoubleArray>): Array<DoubleArray> =
        Array(height) { y ->
            val latitude = 1.0 - (2.0 * y / height - 1.0).absoluteValue
            val baseFromLatitude = latitude * 0.3

            DoubleArray(width) { x ->
                val elevation = heightMap[y][x]

                // 1. Крупномасштабный шум влажности
                val largeScaleHumidity = humidityBase.fractalNoise(
                    x * 0.008, y * 0.008,
                    octaves = 4, persistence = 0.5
                )
                val largeScale = (largeScaleHumidity * 0.5 + 0.5) * 0.4

                // 2. Мелкомасштабные детали
                val smallScale = humidityDetail.fractalNoise(
                    x * 0.02, y * 0.02,
                    octaves = 4, persistence = 0.3
                ) * 0.2

                // 3. Влияние океана
                val oceanInfluence = calculateOceanInfluence(heightMap, x, y, 5)
                val oceanEffect = oceanInfluence * 0.5

                // 4. Высотный эффект
                val heightEffect = when {
                    elevation < 0.3 -> 0.15
                    elevation > 0.7 -> -0.2
                    else -> 0.0
                }

                // 5. Дождевая тень
                val rainShadow = calculateRainShadow(heightMap, x, y, 4) * 0.3

                // Комбинируем
                var humidity = baseFromLatitude + largeScale + smallScale + oceanEffect + heightEffect - rainShadow

                // Случайные влажные зоны
                val randomWet = climateVariation.fractalNoise(
                    x * 0.04, y * 0.04, octaves = 2, persistence = 0.4
                )
                if (randomWet > 0.5) humidity += 0.1

                humidity.coerceIn(0.0, 1.0)
            }
        }

    private fun calculateOceanInfluence(heightMap: Array<DoubleArray>, x: Int, y: Int, range: Int): Double {
        var waterCount = 0
        var totalCount = 0

        for (dy in -range..range) {
            for (dx in -range..range) {
                val nx = x + dx
                val ny = y + dy
                if (nx in heightMap[0].indices && ny in heightMap.indices) {
                    if (heightMap[ny][nx] < 0.3) {
                        waterCount++
                    }
                    totalCount++
                }
            }
        }

        return waterCount.toDouble() / totalCount
    }

    private fun calculateRainShadow(heightMap: Array<DoubleArray>, x: Int, y: Int, range: Int): Double {
        var maxShadow = 0.0
        val windDirectionX = -1

        for (distance in 1..range) {
            val checkX = x + windDirectionX * distance
            if (checkX !in heightMap[0].indices) continue

            for (dy in -1..1) {
                val checkY = y + dy
                if (checkY !in heightMap.indices) continue

                val mountainHeight = heightMap[checkY][checkX]
                val currentHeight = heightMap[y][x]

                if (mountainHeight > currentHeight + 0.1 && mountainHeight > 0.6) {
                    val shadowStrength = (mountainHeight - 0.6) * (1.0 - distance.toDouble() / (range + 1))
                    maxShadow = maxOf(maxShadow, shadowStrength)
                }
            }
        }

        return maxShadow.coerceIn(0.0, 0.5)
    }
}