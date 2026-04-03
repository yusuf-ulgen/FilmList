package com.example.filmlist.ui.add

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import android.widget.SeekBar
import android.widget.CheckBox
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.filmlist.R
import com.example.filmlist.util.RepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    private lateinit var viewModel: AddContentViewModel

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private val maxYear = currentYear - 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_show)

        setupViewModel()
        setupObservers()

        startDaySpinner = findViewById(R.id.start_day_spinner)
        startMonthSpinner = findViewById(R.id.start_month_spinner)
        startYearSpinner = findViewById(R.id.start_year_spinner)
        endDaySpinner = findViewById(R.id.end_day_spinner)
        endMonthSpinner = findViewById(R.id.end_month_spinner)
        endYearSpinner = findViewById(R.id.end_year_spinner)
        ratingBar = findViewById(R.id.rating_bar)
        commentBox = findViewById(R.id.comment_box)
        spoilerCheckbox = findViewById(R.id.spoiler_checkbox)
        showNameEditText = findViewById(R.id.show_name)
        shareButton = findViewById(R.id.share_button)

        setupSpinners()

        shareButton.setOnClickListener {
            validateAndShare()
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
                    Toast.makeText(this@AddShowActivity, "Dizi başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                Toast.makeText(this@AddShowActivity, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinners() {
        val days = (1..31).map { it.toString() }.toList()
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startDaySpinner.adapter = dayAdapter
        endDaySpinner.adapter = dayAdapter

        val months = listOf("Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startMonthSpinner.adapter = monthAdapter
        endMonthSpinner.adapter = monthAdapter

        val years = (maxYear..currentYear).map { it.toString() }.toList()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startYearSpinner.adapter = yearAdapter
        endYearSpinner.adapter = yearAdapter
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

        if (!validateDate(startDay, startMonth, startYear) || !validateDate(endDay, endMonth, endYear)) {
            return
        }

        val rating = ratingBar.progress
        if (rating == 0) {
            Toast.makeText(this, "Puanlama yapın", Toast.LENGTH_SHORT).show()
            return
        }

        val dateRange = "$startDay.$startMonth.$startYear - $endDay.$endMonth.$endYear"
        viewModel.saveContent(
            title = showName,
            type = "SHOW",
            date = dateRange,
            rating = rating,
            comment = commentBox.text.toString(),
            isSpoiler = spoilerCheckbox.isChecked
        )
    }
}
