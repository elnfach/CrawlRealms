package com.elnfach.realms.gen.world.noice.model

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