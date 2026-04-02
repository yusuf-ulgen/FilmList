@file:Suppress("DEPRECATION")

package com.example.filmlist.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.filmlist.data.local.AppDatabase
import com.example.filmlist.data.local.SessionManager
import com.example.filmlist.data.repository.AuthRepository
import com.example.filmlist.databinding.ActivitySignUpBinding
import com.example.filmlist.ui.ViewModelFactory
import com.example.filmlist.ui.profile.ProfilingActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setupObservers()

        val gender = arrayOf("Seçiniz", "Erkek", "Kadın", "Belirtmek istemiyorum")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gender)
        binding.genderSpinnerId.adapter = adapter

        binding.kaydetId.setOnClickListener {
            val mail = binding.mailId.text.toString()
            val sifre = binding.sifreId.text.toString()
            val selectedGender = binding.genderSpinnerId.selectedItem.toString()
            val dob = binding.dogumyiliId.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                Toast.makeText(this, "Lütfen geçerli bir e-posta adresi giriniz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateRegex = Regex("""^(\d{2})\.(\d{2})\.(\d{4})$""")
            val currentYear = 2025
            val minYear = currentYear - 100

            if (!dob.matches(dateRegex)) {
                Toast.makeText(this, "Doğum tarihi formatı geçerli değil. Lütfen 'dd.mm.yyyy' formatında giriniz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val (day, month, year) = dob.split(".").map { it.toInt() }

            if (day < 1 || day > 31 || month < 1 || month > 12 || year < minYear || year > currentYear) {
                Toast.makeText(this, "Geçerli bir tarih giriniz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDayForMonth(day, month, year)) {
                Toast.makeText(this, "Geçersiz bir gün girildi. Lütfen geçerli bir tarihi giriniz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedGender == "Seçiniz") {
                Toast.makeText(this, "Cinsiyet seçmeniz gerekmektedir!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.signUp(mail, sifre)
        }

        binding.dogumyiliId.addTextChangedListener(object : TextWatcher {
            var isUpdating = false
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true
                val text = s.toString()
                var cleanText = text.replace("[^\\d]".toRegex(), "")
                if (cleanText.length > 8) cleanText = cleanText.substring(0, 8)
                var formattedText = ""
                if (cleanText.length in 3..4) {
                    formattedText = cleanText.substring(0, 2) + "." + cleanText.substring(2)
                } else if (cleanText.length in 5..6) {
                    formattedText = cleanText.substring(0, 2) + "." + cleanText.substring(2, 4) + "." + cleanText.substring(4)
                } else if (cleanText.length >= 7) {
                    formattedText = cleanText.substring(0, 2) + "." + cleanText.substring(2, 4) + "." + cleanText.substring(4, Math.min(cleanText.length, 8))
                } else {
                    formattedText = cleanText
                }
                if (formattedText != text) {
                    binding.dogumyiliId.setText(formattedText)
                    binding.dogumyiliId.setSelection(formattedText.length)
                }
                isUpdating = false
            }
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupViewModel() {
        val factory = com.example.filmlist.util.RepositoryProvider.provideViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.signUpResult.collectLatest { success ->
                if (success) {
                    Toast.makeText(this@SignUpActivity, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignUpActivity, ProfilingActivity::class.java))
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { errorMessage ->
                Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidDayForMonth(day: Int, month: Int, year: Int): Boolean {
        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 0
        }
        return day <= daysInMonth
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}
