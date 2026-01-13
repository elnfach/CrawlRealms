package com.elnfach.realms.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elnfach.realms.content.Biome
import com.elnfach.realms.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RealmViewModel(
    contentRepository: ContentRepository
) : ViewModel() {

    private val _biomes = MutableStateFlow<List<Biome>?>(null)
    val biomes: StateFlow<List<Biome>?> = _biomes

    init {
        viewModelScope.launch {
            _biomes.value = contentRepository.getBiomes()
        }
    }
}