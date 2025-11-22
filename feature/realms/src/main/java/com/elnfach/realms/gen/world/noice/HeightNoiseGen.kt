package com.elnfach.realms.gen.world.noice

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
    private val detailNoise = PerlinNoise(seed + 5)       // Детали

    fun gen(width: Int, height: Int): Array<DoubleArray> =
        Array(height) { y ->
            DoubleArray(width) { x ->
                // БАЗОВЫЕ МАСКИ (все от -1 до 1)
                val continent = continentNoise.fractalNoise(x * 0.005, y * 0.005, 2, 0.8)
                val terrain = terrainNoise.fractalNoise(x * 0.1, y * 0.1, 4, 0.6)
                val mountainZone = mountainNoise.fractalNoise(x * 0.05, y * 0.05, 3, 0.7)

                // МАСКА ОКЕАНОВ (глубокий океан -> мелководье)
                val oceanMask = (-continent).coerceAtLeast(0.0) // continent < 0 -> океан
                val deepOceanMask = (oceanMask - 0.5).coerceAtLeast(0.0) * 2.0 // глубокий океан
                val shallowOceanMask = (oceanMask - 0.3).coerceIn(0.0, 0.2) * 5.0 // мелководье

                // МАСКА СУШИ (равнины -> холмы -> горы)
                val landMask = continent.coerceAtLeast(0.0) // continent > 0 -> суша
                val plainsMask = (landMask - 0.2).coerceIn(0.0, 0.3) * 3.3 // равнины
                val hillsMask = (landMask - 0.5).coerceIn(0.0, 0.3) * 3.3 // холмы
                val mountainBaseMask = (landMask - 0.8).coerceIn(0.0, 0.2) * 5.0 // предгорья

                // ГОРНЫЕ ДЕТАЛИ (только в горных зонах)
                val ridgeValue = ridgeNoise.fractalNoise(x * 0.15, y * 0.15, 3)
                val ridgeMask = (1 - abs(ridgeValue)).pow(2.0)
                val mountainDetail = mountainNoise.fractalNoise(x * 0.8, y * 0.8, 5, 0.6)
                val mountainsMask = (mountainZone - 0.3).coerceAtLeast(0.0) * ridgeMask

                // СБОРКА ВЫСОТЫ ЧЕРЕЗ МАСКИ
                var heightValue = 0.0

                // ОКЕАНЫ: от глубоких до мелких
                heightValue += deepOceanMask * (-0.8 + terrain * 0.2)    // [-1.0, -0.6]
                heightValue += shallowOceanMask * (-0.4 + terrain * 0.2) // [-0.4, -0.2]

                // СУША: от равнин к горам
                heightValue += plainsMask * (0.1 + terrain * 0.3)        // [0.1, 0.4]
                heightValue += hillsMask * (0.4 + terrain * 0.3)         // [0.4, 0.7]
                heightValue += mountainBaseMask * (0.6 + terrain * 0.2)  // [0.6, 0.8]

                // ВЕРШИНЫ ГОР (поверх всего)
                heightValue += mountainsMask * mountainDetail * 0.5      // [0.0, +0.5]

                heightValue.normalized
            }
        }
}