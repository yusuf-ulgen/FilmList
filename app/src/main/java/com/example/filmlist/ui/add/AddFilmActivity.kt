package com.example.filmlist.ui.add

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.filmlist.R
import com.example.filmlist.util.RepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddFilmActivity : AppCompatActivity() {

    private lateinit var daySpinner: Spinner
    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner
    private lateinit var ratingBar: SeekBar
    private lateinit var commentBox: AppCompatEditText
    private lateinit var spoilerCheckbox: CheckBox
    private lateinit var shareButton: Button
    private lateinit var viewModel: AddContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_film)

        setupViewModel()
        setupObservers()

        daySpinner = findViewById(R.id.day_spinner)
        monthSpinner = findViewById(R.id.month_spinner)
        yearSpinner = findViewById(R.id.year_spinner)
        ratingBar = findViewById(R.id.rating_bar)
        commentBox = findViewById(R.id.comment_box)
        spoilerCheckbox = findViewById(R.id.spoiler_checkbox)
        shareButton = findViewById(R.id.share_button)

        setupSpinners()

        shareButton.setOnClickListener {
            if (validateInputs()) {
                val filmName = findViewById<AppCompatEditText>(R.id.search_bar).text.toString().trim()
                val date = "${daySpinner.selectedItem}.${monthSpinner.selectedItem}.${yearSpinner.selectedItem}"
                viewModel.saveContent(
                    title = filmName,
                    type = "FILM",
                    date = date,
                    rating = ratingBar.progress,
                    comment = commentBox.text.toString(),
                    isSpoiler = spoilerCheckbox.isChecked
                )
            }
        }
    }

    private fun setupViewModel() {
        val factory = RepositoryProvider.provideViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AddContentViewModel::class.java]
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.contentSaved.collectLatest { success ->
                if (success) {
                    Toast.makeText(this@AddFilmActivity, "Film başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                Toast.makeText(this@AddFilmActivity, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinners() {
        val days = (1..31).map { it.toString() }.toList()
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = dayAdapter

        val months = listOf("Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter

        val years = (1995..2025).map { it.toString() }.toList()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter
    }

    private fun validateDate(day: Int, month: Int, year: Int): Boolean {
        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 0
        }
        if (day < 1 || day > daysInMonth) {
            Toast.makeText(this, "Geçersiz tarih! Bu ayda sadece $daysInMonth gün var.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun validateInputs(): Boolean {
        val filmName = findViewById<AppCompatEditText>(R.id.search_bar).text.toString().trim()
        val day = daySpinner.selectedItemPosition + 1
        val month = monthSpinner.selectedItemPosition + 1
        val year = yearSpinner.selectedItemPosition + 1995
        val rating = ratingBar.progress

        if (filmName.isEmpty()) {
            Toast.makeText(this, "Film adı girilmelidir!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!validateDate(day, month, year)) return false

        if (rating == 0) {
            Toast.makeText(this, "Puanlama yapın!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
