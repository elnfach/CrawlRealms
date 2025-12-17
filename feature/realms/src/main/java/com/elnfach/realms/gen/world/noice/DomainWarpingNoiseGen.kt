package com.elnfach.realms.gen.world.noice

import com.elnfach.realms.gen.world.PerlinNoise

class DomainWarpingNoiseGen(seed: Long, val delta: Int) {
    private val noice = PerlinNoise(seed + delta + 10)

    fun gen(x: Int, y: Int): Pair<Double, Double> {
        val warpX = x + noice.noise(x * 0.01, y * 0.01) * 50f
        val warpY = y + noice.noise(x * 0.01 + delta, y * 0.01) * 50f
        return Pair(warpX, warpY)
    }
}