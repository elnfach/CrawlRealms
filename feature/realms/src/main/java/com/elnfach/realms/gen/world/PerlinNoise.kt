package com.elnfach.realms.gen.world

class PerlinNoise(seed: Long = 0) {
    private val permutation = IntArray(512)

    init {
        val p = IntArray(256)
        for (i in 0 until 256) p[i] = i

        val random = java.util.Random(seed)
        for (i in 255 downTo 1) {
            val j = random.nextInt(i + 1)
            val temp = p[i]
            p[i] = p[j]
            p[j] = temp
        }

        for (i in 0 until 512) {
            permutation[i] = p[i and 255]
        }
    }

    private fun fade(t: Double): Double = t * t * t * (t * (t * 6 - 15) + 10)

    private fun lerp(t: Double, a: Double, b: Double): Double = a + t * (b - a)

    private fun grad(hash: Int, x: Double, y: Double): Double {
        val h = hash and 15
        val u = if (h < 8) x else y
        val v = when {
            h < 4 -> y
            h == 12 || h == 14 -> x
            else -> 0.0
        }
        return (if ((h and 1) == 0) u else -u) + (if ((h and 2) == 0) v else -v)
    }

    fun noise(x: Double, y: Double): Double {
        val xi = floor(x).toInt() and 255
        val yi = floor(y).toInt() and 255

        val xf = x - floor(x)
        val yf = y - floor(y)

        val u = fade(xf)
        val v = fade(yf)

        val aa = permutation[permutation[xi] + yi]
        val ab = permutation[permutation[xi] + inc(yi)]
        val ba = permutation[permutation[inc(xi)] + yi]
        val bb = permutation[permutation[inc(xi)] + inc(yi)]

        val x1 = lerp(u, grad(aa, xf, yf), grad(ba, xf - 1, yf))
        val x2 = lerp(u, grad(ab, xf, yf - 1), grad(bb, xf - 1, yf - 1))

        return lerp(v, x1, x2)
    }

    fun fractalNoise(x: Double, y: Double, octaves: Int = 4, persistence: Double = 0.5): Double {
        require(octaves > 0) { "Octaves must be positive" }
        require(persistence in 0.0..1.0) { "Persistence must be between 0 and 1" }

        var result = 0.0
        var amplitude = 1.0
        var frequency = 1.0

        repeat(octaves) {
            result += noise(x * frequency, y * frequency) * amplitude
            amplitude *= persistence
            frequency *= 2
        }

        return result
    }

    private fun inc(num: Int): Int = (num + 1) and 255
    private fun floor(value: Double): Double = kotlin.math.floor(value)
}