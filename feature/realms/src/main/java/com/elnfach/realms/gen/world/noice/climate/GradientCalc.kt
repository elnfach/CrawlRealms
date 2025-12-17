package com.elnfach.realms.gen.world.noice.climate

class GradientCalc(val height: Int) {
    private val half = height ushr 1
    private val invHalf = 1.0 / half

    fun calc(y: Int): Double {
        val distance = if (y < half) y else height - y
        return distance * invHalf
    }
}