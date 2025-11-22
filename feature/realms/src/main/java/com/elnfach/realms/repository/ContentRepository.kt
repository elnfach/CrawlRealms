package com.elnfach.realms.repository

import android.content.Context
import com.elnfach.realms.R
import com.elnfach.realms.content.Biome
import com.elnfach.realms.content.Traits
import kotlinx.serialization.json.Json

class ContentRepository(private val context: Context) {

    fun getBiomes() : List<Biome> {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.biomes)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            Json.decodeFromString<List<Biome>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getTraits() : Traits {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.traits)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            Json.decodeFromString<Traits>(jsonString)
        } catch (e: Exception) {
            Traits(emptyList())
        }
    }
}