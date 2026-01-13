package com.elnfach.realms.gen.world.noice

import android.util.Log
import com.elnfach.realms.content.Altitude
import com.elnfach.realms.gen.world.PerlinNoise
import kotlin.math.abs
import kotlin.math.pow

val Double.normalized: Double
    get() = (this.coerceIn(-1.0, 1.0) + 1.0) / 2.0

class HeightNoiseGen(val seed: Long) {
    private val continentNoise = PerlinNoise(seed + 1)    // Форма континентов
    private val terrainNoise = PerlinNoise(seed + 2)      // Базовый рельеф
    private val mountainNoise = PerlinNoise(seed + 3)     // Горные зоны
    private val ridgeNoise = PerlinNoise(seed + 4)        // Хребты
    private val warpNoise = DomainWarpingNoiseGen(seed + 5, 10)

    fun gen(width: Int, height: Int): Array<DoubleArray> =
        Array(height) { y ->
            DoubleArray(width) { x ->
                val (warpedX, warpedY) = warpNoise.gen(x, y)

                val nx = warpedX / width;
                val ny = warpedY / height;

                val continent = continentNoise.fractalNoise(
                    nx,
                    ny,
                    8, 0.7
                )
                val terrain = terrainNoise.fractalNoise(warpedX * 0.1, warpedY * 0.1, 4, 0.6) * 0.2

                if (continent < 0) {
                    // ОКЕАНЫ: -0.8 до 0.0
                    val oceanDepth = (0.2 - continent)  // 0 до 1
                    return@DoubleArray - 0.8 + oceanDepth * 0.8 // -0.8 до 0.0
                }

                // СУША: плавные переходы через все уровни
                var heightValue = when {
                    continent < 0.1 -> {
                        // COAST/BEACH: 0.0 до 0.03
                        val progress = continent / 0.1 // 0 до 1
                        progress * Altitude.BEACH_MAX // 0.0 до 0.03
                    }
                    continent < 0.4 -> {
                        // PLAINS: 0.03 до 0.4
                        val progress = (continent - 0.1) / 0.3 // 0 до 1
                        Altitude.BEACH_MAX + progress * (Altitude.PLAINS_MAX - Altitude.BEACH_MAX) // 0.03 до 0.4
                    }
                    continent < 0.6 -> {
                        // FOREST: 0.4 до 0.6
                        val progress = (continent - 0.4) / 0.2 // 0 до 1
                        Altitude.PLAINS_MAX + progress * (Altitude.FOREST_MAX - Altitude.PLAINS_MAX) // 0.4 до 0.6
                    }
                    continent < 0.8 -> {
                        // HILLS: 0.6 до 0.7
                        val progress = (continent - 0.6) / 0.2 // 0 до 1
                        Altitude.FOREST_MAX + progress * (Altitude.HILLS_MAX - Altitude.FOREST_MAX) // 0.6 до 0.7
                    }
                    else -> {
                        // MOUNTAINS: 0.7 до 0.9+
                        val progress = (continent - 0.8) / 0.2 // 0 до 1
                        Altitude.HILLS_MAX + progress * (0.9 - Altitude.HILLS_MAX) // 0.7 до 0.9
                    }
                }

                // Добавляем шум (меньшей силы для сохранения переходов)
                heightValue += terrain

                // Для гор добавляем дополнительный шум
                if (continent > 0.7) {
                    val mountainDetail = mountainNoise.fractalNoise(warpedX * 0.3, warpedY * 0.3, 2, 0.5) * 0.15
                    heightValue += mountainDetail
                }

                heightValue.coerceIn(-1.0, 1.0)
            }
        }
}