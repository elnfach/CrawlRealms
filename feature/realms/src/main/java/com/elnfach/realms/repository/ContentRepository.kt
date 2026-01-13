package com.elnfach.realms.repository

import android.content.Context
import com.elnfach.realms.R
import com.elnfach.realms.content.Biome
import com.elnfach.realms.content.Traits
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json

class ContentRepository(private val context: Context) {

    suspend fun getBiomes() : List<Biome> {
        return try {
            val storage = Firebase.storage
            val ref = storage.reference.child("crawl_realm/biomes.json") // путь в Storage

            val bytes = ref.getBytes(Long.MAX_VALUE).await()
            val jsonString = bytes.toString(Charsets.UTF_8)

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