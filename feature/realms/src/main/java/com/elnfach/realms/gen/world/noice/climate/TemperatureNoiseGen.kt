package com.elnfach.realms.gen.world.noice.climate

import android.util.Log
import com.elnfach.realms.content.Altitude
import com.elnfach.realms.gen.world.PerlinNoise
import com.elnfach.realms.gen.world.noice.DomainWarpingNoiseGen
import com.elnfach.realms.gen.world.noice.normalized
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class TemperatureNoiseGen(seed: Long, delta: Int) {
    private val temperatureBase = PerlinNoise(seed + delta + 1)
    private val temperatureDetail = PerlinNoise(seed + delta + 2)
    private val climateVariation = PerlinNoise(seed + delta + 3)
    private lateinit var calc: GradientCalc
    private val warpNoise = DomainWarpingNoiseGen(seed + delta + 4, 10)

    fun gen(width: Int, height: Int, altitude: Array<DoubleArray>): Array<DoubleArray> {
        calc = GradientCalc(height)
        return Array(height) { y ->
            val latitude = calc.calc(y)
            DoubleArray(width) { x ->
                val elevation = altitude[y][x]
                val isOcean = elevation < Altitude.OCEAN_LEVEL

                // Базовые шумы
                val largeScale = temperatureBase.fractalNoise(x * 0.01, y * 0.01, 4, 0.5) 
                val smallScale = temperatureDetail.fractalNoise(x * 0.03, y * 0.03, 3, 0.4) * 0.25

                // Высотный эффект: только для суши, горы холоднее
                val heightEffect = if (!isOcean && elevation > 0.2) {
                    -elevation.pow(1.2) * 0.3
                } else {
                    0.0
                }

                // Океанский эффект: океан охлаждает, особенно в жарких регионах
                val oceanInfluence = calculateOceanInfluence(altitude, x, y, 3)
                val oceanEffect = if (oceanInfluence > 0.5) {
                    -abs(latitude) * 0.3 // Сильнее охлаждает на экваторе, слабее на полюсах
                } else {
                    0.0
                }

                // Морские течения (дополнительный эффект)
                val currentEffect = if (isOcean) {
                    temperatureDetail.fractalNoise(x * 0.005, y * 0.005, 2, 0.5) * 0.2
                } else {
                    0.0
                }

                var temperature = latitude + largeScale + smallScale + heightEffect + oceanEffect + currentEffect + 0.2

                temperature = temperature.coerceIn(-1.0, 1.0)

                temperature
            }
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
                    if (heightMap[ny][nx] < Altitude.OCEAN_LEVEL) {
                        waterCount++
                    }
                    totalCount++
                }
            }
        }

        return waterCount.toDouble() / totalCount
    }
}