package com.example.filmlist

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import android.widget.SeekBar
import android.widget.CheckBox
import android.widget.Button
import java.util.Calendar

class AddShowActivity : AppCompatActivity() {

    private lateinit var startDaySpinner: Spinner
    private lateinit var startMonthSpinner: Spinner
    private lateinit var startYearSpinner: Spinner
    private lateinit var endDaySpinner: Spinner
    private lateinit var endMonthSpinner: Spinner
    private lateinit var endYearSpinner: Spinner
    private lateinit var ratingBar: SeekBar
    private lateinit var commentBox: AppCompatEditText
    private lateinit var spoilerCheckbox: CheckBox
    private lateinit var showNameEditText: AppCompatEditText
    private lateinit var shareButton: Button

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private val maxYear = currentYear - 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_show)

        // Spinner'lar
        startDaySpinner = findViewById(R.id.start_day_spinner)
        startMonthSpinner = findViewById(R.id.start_month_spinner)
        startYearSpinner = findViewById(R.id.start_year_spinner)
        endDaySpinner = findViewById(R.id.end_day_spinner)
        endMonthSpinner = findViewById(R.id.end_month_spinner)
        endYearSpinner = findViewById(R.id.end_year_spinner)

        // Rating Bar
        ratingBar = findViewById(R.id.rating_bar)

        // Comment Box
        commentBox = findViewById(R.id.comment_box)

        // Spoiler Checkbox
        spoilerCheckbox = findViewById(R.id.spoiler_checkbox)

        // Show Name
        showNameEditText = findViewById(R.id.show_name)

        // Share Button
        shareButton = findViewById(R.id.share_button)

        // Spinner ayarları
        setupSpinners()

        // Paylaş butonuna tıklanma olayını ekleyelim
        shareButton.setOnClickListener {
            validateAndShare()
        }
    }

    private fun setupSpinners() {
        // Gün Spinner
        val days = (1..31).map { it.toString() }.toList()
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startDaySpinner.adapter = dayAdapter
        endDaySpinner.adapter = dayAdapter

        // Ay Spinner
        val months = listOf("Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startMonthSpinner.adapter = monthAdapter
        endMonthSpinner.adapter = monthAdapter

        // Yıl Spinner (Bugünden 30 yıl öncesine kadar)
        val years = (maxYear..currentYear).map { it.toString() }.toList()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startYearSpinner.adapter = yearAdapter
        endYearSpinner.adapter = yearAdapter
    }

    // Tarih kontrol fonksiyonu (Örneğin: 31 Şubat gibi yanlış tarihleri engellemek)
    private fun validateDate(day: Int, month: Int, year: Int): Boolean {
        // Ayların gün sayıları
        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31  // Ocak, Mart, Mayıs, Temmuz, Ağustos, Ekim, Aralık
            4, 6, 9, 11 -> 30            // Nisan, Haziran, Eylül, Kasım
            2 -> if (isLeapYear(year)) 29 else 28  // Şubat, artık yıl mı?
            else -> 0  // Geçersiz ay
        }

        // Geçersiz gün kontrolü
        if (day < 1 || day > daysInMonth) {
            // Hatalı tarih girildiğinde kullanıcıya mesaj göster
            Toast.makeText(this, "Geçersiz tarih! Bu ayda sadece $daysInMonth gün var.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Artık yıl kontrolü (Şubat için 29. günü doğru şekilde kontrol edebilmek için)
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    // Paylaşma işleminde geçerli verileri kontrol et
    private fun validateAndShare() {
        val showName = showNameEditText.text.toString().trim()
        if (showName.isEmpty()) {
            Toast.makeText(this, "Dizi adı boş olamaz!", Toast.LENGTH_SHORT).show()
            return
        }

        val startYear = startYearSpinner.selectedItem.toString().toInt()
        val startMonth = startMonthSpinner.selectedItemPosition + 1
        val startDay = startDaySpinner.selectedItem.toString().toInt()

        val endYear = endYearSpinner.selectedItem.toString().toInt()
        val endMonth = endMonthSpinner.selectedItemPosition + 1
        val endDay = endDaySpinner.selectedItem.toString().toInt()

        // Tarihler geçerli mi?
        if (!validateDate(startDay, startMonth, startYear) || !validateDate(endDay, endMonth, endYear)) {
            Toast.makeText(this, "Geçersiz tarih! Lütfen tarihleri kontrol edin.", Toast.LENGTH_SHORT).show()
            return
        }

        // Puanlama yapılmış mı?
        val rating = ratingBar.progress
        if (rating == 0) {
            Toast.makeText(this, "Puanlama yapın", Toast.LENGTH_SHORT).show()
            return
        }

        // Her şey yolunda ise paylaşımı yapabiliriz
        // Burada paylaşıma yönelik işlemler yapılabilir
        Toast.makeText(this, "Paylaşım başarılı!", Toast.LENGTH_SHORT).show()
    }
}
