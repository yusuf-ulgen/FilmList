package com.example.filmlist

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.AppCompatEditText
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar

class AddFilmActivity : AppCompatActivity() {

    private lateinit var daySpinner: Spinner
    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner
    private lateinit var ratingBar: SeekBar
    private lateinit var commentBox: AppCompatEditText
    private lateinit var spoilerCheckbox: CheckBox
    private lateinit var shareButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_film)

        // Spinner'lar
        daySpinner = findViewById(R.id.day_spinner)
        monthSpinner = findViewById(R.id.month_spinner)
        yearSpinner = findViewById(R.id.year_spinner)

        // Rating Bar
        ratingBar = findViewById(R.id.rating_bar)

        // Comment Box
        commentBox = findViewById(R.id.comment_box)

        // Spoiler Checkbox
        spoilerCheckbox = findViewById(R.id.spoiler_checkbox)

        // Share Button
        shareButton = findViewById(R.id.share_button)

        // Spinner ayarları
        setupSpinners()

        // Puan barına listener ekleyelim
        ratingBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Puan değeri burada işlenebilir
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Paylaş butonuna tıklanma işlevi ekleyelim
        shareButton.setOnClickListener {
            if (validateInputs()) {
                // Paylaşım işlemi yapılacak
                Toast.makeText(this, "Film başarıyla paylaşıldı!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinners() {
        // Gün Spinner
        val days = (1..31).map { it.toString() }.toList()
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = dayAdapter

        // Ay Spinner
        val months = listOf("Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter

        // Yıl Spinner (Bugünden 30 yıl öncesine kadar)
        val years = (1995..2025).map { it.toString() }.toList()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter
    }

    // Tarih kontrol fonksiyonu
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

    // Gerekli kontrolleri yapan fonksiyon
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

        if (!validateDate(day, month, year)) {
            Toast.makeText(this, "Geçersiz tarih!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (rating == 0) {
            Toast.makeText(this, "Puanlama yapın!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}

