package com.elnfach.realms.gen.world.noice

import com.elnfach.realms.gen.world.noice.climate.HumidityNoiseGen
import com.elnfach.realms.gen.world.noice.climate.TemperatureNoiseGen
import com.elnfach.realms.gen.world.noice.model.Climate

class ClimateNoiseGen(seed: Long) {
    private val temperature = TemperatureNoiseGen(seed + 10, 1)
    private val humidity = HumidityNoiseGen(seed + 20, 2)

    fun gen(width: Int, height: Int, altitude: Array<DoubleArray>): Climate {
        val temperature = temperature.gen(width, height, altitude)
        return Climate(
            temperature,
            humidity.gen(width, height, altitude, temperature)
        )
    }
}