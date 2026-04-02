package com.example.filmlist.ui.categories

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoriesViewModel : ViewModel() {

    private val _filmCategories = MutableStateFlow<List<Category>>(emptyList())
    val filmCategories = _filmCategories.asStateFlow()

    private val _diziCategories = MutableStateFlow<List<Category>>(emptyList())
    val diziCategories = _diziCategories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _filmCategories.value = listOf(
            Category("Aksiyon"), Category("Komedi"), Category("Dram"),
            Category("Macera"), Category("Bilim Kurgu"), Category("Gerilim"),
            Category("Romantik"), Category("Korku"), Category("Animasyon"),
            Category("Belgesel"), Category("Fantezi"), Category("Savaş"),
            Category("Müzikal"), Category("Biyografi"), Category("Suç")
        )

        _diziCategories.value = listOf(
            Category("Drama"), Category("Komedi"), Category("Aksiyon"),
            Category("Romantik"), Category("Suç"), Category("Bilim Kurgu"),
            Category("Gerilim"), Category("Macera"), Category("Korku"),
            Category("Fantastik"), Category("Yerli"), Category("Müzikal"),
            Category("Belgesel"), Category("Hikaye"), Category("Kısa Dizi")
        )
    }

    fun isSelectionValid(): Boolean {
        val selectedFilmCategories = _filmCategories.value.count { it.isSelected }
        val selectedDiziCategories = _diziCategories.value.count { it.isSelected }
        return selectedFilmCategories >= 3 && selectedDiziCategories >= 3
    }
}
