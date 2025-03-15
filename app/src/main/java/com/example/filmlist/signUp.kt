@file:Suppress("DEPRECATION")

package com.example.filmlist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class signUp : AppCompatActivity() {
    private lateinit var mailEditText: EditText
    private lateinit var sifreEditText: EditText
    private lateinit var dogumtarihieditText: EditText
    private lateinit var kaydetButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_up)

        mailEditText = findViewById(R.id.mail_id)
        sifreEditText = findViewById(R.id.sifre_id)
        dogumtarihieditText = findViewById(R.id.dogumyili_id)
        kaydetButton = findViewById(R.id.kaydet_id)

        val genderSpinner: Spinner = findViewById(R.id.gender_spinner_id)
        val gender = arrayOf("Seçiniz", "Erkek", "Kadın", "Belirtmek istemiyorum")

        // ArrayAdapter kullanarak Spinner'ı cinsiyet seçenekleriyle dolduruyoruz
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gender)
        genderSpinner.adapter = adapter

        // Kullanıcı zaten giriş yaptıysa, doğrudan profiling sayfasına yönlendir
        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean("is_logged_in", false)) {
            val intent = Intent(this, profiling::class.java)
            startActivity(intent)
            finish()
        }

        kaydetButton.setOnClickListener {
            val mail = mailEditText.text.toString()
            val sifre = sifreEditText.text.toString()
            val selectedGender = genderSpinner.selectedItem.toString()
            val dob = dogumtarihieditText.text.toString()

            // E-mail formatı kontrolü
            if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                Toast.makeText(this, "Lütfen geçerli bir e-posta adresi giriniz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Doğum tarihi formatı ve geçerlilik kontrolü
            val dateRegex = Regex("""^(\d{2})\.(\d{2})\.(\d{4})$""")
            val currentYear = 2025
            val minYear = currentYear - 100

            if (!dob.matches(dateRegex)) {
                Toast.makeText(this, "Doğum tarihi formatı geçerli değil. Lütfen 'dd.mm.yyyy' formatında giriniz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gün, ay ve yılın mantıklı olup olmadığını kontrol et
            val (day, month, year) = dob.split(".").map { it.toInt() }

            // Geçersiz gün, ay veya yıl kontrolü
            if (day < 1 || day > 31 || month < 1 || month > 12 || year < minYear || year > currentYear) {
                Toast.makeText(this, "Geçerli bir tarih giriniz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ay ve gün doğrulaması
            if (!isValidDayForMonth(day, month, year)) {
                Toast.makeText(this, "Geçersiz bir gün girildi. Lütfen geçerli bir tarihi giriniz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cinsiyet seçilmediğinde hata mesajı
            if (selectedGender == "Seçiniz") {
                Toast.makeText(this, "Cinsiyet seçmeniz gerekmektedir!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // E-mail, şifre ve cinsiyet seçimi doğruysa, kullanıcıyı kaydedip profil sayfasına yönlendir
            if (mail.isNotEmpty() && sifre.isNotEmpty() && selectedGender != "Seçiniz") {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                prefs.edit().putString("user_email", mail).putString("user_password", sifre).apply()

                val intent = Intent(this, profiling::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
            }
        }

        // Doğum tarihi için TextWatcher ekleyelim
        dogumtarihieditText.addTextChangedListener(object : TextWatcher {
            var isUpdating = false  // Bu değişken, döngüyü engellemek için kullanılıyor.

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return  // Eğer metin güncelleniyorsa, geri çık.
                isUpdating = true  // Metni güncellemeye başladık.

                val text = s.toString()

                // Sadece rakamları al
                var cleanText = text.replace("[^\\d]".toRegex(), "")

                // Eğer 8 karakterden fazla yazılmışsa, ilk 8 karakteri al
                if (cleanText.length > 8) {
                    cleanText = cleanText.substring(0, 8)
                }

                // Format: dd.mm.yyyy
                var formattedText = ""

                if (cleanText.length in 3..4) {
                    formattedText = cleanText.substring(0, 2) + "." + cleanText.substring(2)
                } else if (cleanText.length in 5..6) {
                    // Eğer 6'dan az uzunluktaysa, substring(4) hata verebilir
                    formattedText = cleanText.substring(0, 2) + "." + cleanText.substring(2, Math.min(cleanText.length, 4)) + "." + cleanText.substring(4)
                } else if (cleanText.length >= 7) {
                    // Eğer 7'den büyükse, doğru şekilde substring alınacak
                    formattedText = cleanText.substring(0, 2) + "." + cleanText.substring(2, 4) + "." + cleanText.substring(4, Math.min(cleanText.length, 8))
                } else {
                    formattedText = cleanText
                }

                // Eğer formatlandıysa text'i güncelle
                if (formattedText != text) {
                    dogumtarihieditText.setText(formattedText)
                    // Cursor'u sona yerleştir
                    dogumtarihieditText.setSelection(formattedText.length)
                }

                isUpdating = false  // Güncelleme tamamlandı.
            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    // Ayın geçerli gün sayısını kontrol eden fonksiyon
    private fun isValidDayForMonth(day: Int, month: Int, year: Int): Boolean {
        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 0
        }
        return day <= daysInMonth
    }

    // Artık yıl kontrolü
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}
