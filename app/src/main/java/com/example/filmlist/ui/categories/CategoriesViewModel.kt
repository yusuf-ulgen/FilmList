package com.example.filmlist.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CategoriesViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _filmCategories = MutableStateFlow<List<Category>>(emptyList())
    val filmCategories = _filmCategories.asStateFlow()

    private val _diziCategories = MutableStateFlow<List<Category>>(emptyList())
    val diziCategories = _diziCategories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val savedFilmCats = sessionManager.selectedFilmCategories.first()
            val savedDiziCats = sessionManager.selectedDiziCategories.first()

            _filmCategories.value = listOf(
                "Aksiyon", "Komedi", "Dram", "Macera", "Bilim Kurgu", 
                "Gerilim", "Romantik", "Korku", "Animasyon", "Belgesel", 
                "Fantezi", "Savaş", "Müzikal", "Biyografi", "Suç"
            ).map { Category(it, savedFilmCats.contains(it)) }

            _diziCategories.value = listOf(
                "Drama", "Komedi", "Aksiyon", "Romantik", "Suç", 
                "Bilim Kurgu", "Gerilim", "Macera", "Korku", "Fantastik", 
                "Yerli", "Müzikal", "Belgesel", "Hikaye", "Kısa Dizi"
            ).map { Category(it, savedDiziCats.contains(it)) }
        }
    }

    fun saveSelections() {
        viewModelScope.launch {
            val selectedFilm = _filmCategories.value.filter { it.isSelected }.map { it.name }.toSet()
            val selectedDizi = _diziCategories.value.filter { it.isSelected }.map { it.name }.toSet()
            sessionManager.saveCategories(selectedFilm, selectedDizi)
        }
    }

    fun isSelectionValid(): Boolean {
        val selectedFilmCategories = _filmCategories.value.count { it.isSelected }
        val selectedDiziCategories = _diziCategories.value.count { it.isSelected }
        return selectedFilmCategories >= 3 && selectedDiziCategories >= 3
    }
}
