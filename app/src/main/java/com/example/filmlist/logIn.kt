@file:Suppress("DEPRECATION")

package com.example.filmlist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class logIn : AppCompatActivity() {
    private lateinit var mailEditText: EditText
    private lateinit var sifreEditText: EditText
    private lateinit var girisButton: Button
    private lateinit var benihatirlaCheckBox: CheckBox

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        mailEditText = findViewById(R.id.login_mail_id)
        sifreEditText = findViewById(R.id.login_sifre_id)
        girisButton = findViewById(R.id.login_giris_id)
        benihatirlaCheckBox = findViewById(R.id.benihatirla_id)

        // Kullanıcı zaten giriş yaptıysa, doğrudan profiling sayfasına yönlendir
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean("is_logged_in", false)) {
            val intent = Intent(this, profiling::class.java)
            startActivity(intent)
            finish()
        }

        girisButton.setOnClickListener {
            val mail = mailEditText.text.toString()
            val sifre = sifreEditText.text.toString()

            if (mail.isNotEmpty() && sifre.isNotEmpty()) {
                // Kullanıcı kayıtlı mı kontrol et
                val savedMail = prefs.getString("user_email", "")
                val savedPassword = prefs.getString("user_password", "")

                if (mail == savedMail && sifre == savedPassword) {
                    // Başarıyla giriş yapıldı
                    prefs.edit().putBoolean("is_logged_in", true).apply()

                    // Eğer "Beni Hatırla" checkbox'ı işaretliyse, durumu kaydet
                    if (benihatirlaCheckBox.isChecked) {
                        prefs.edit().putBoolean("remember_me", true).apply()
                    } else {
                        prefs.edit().putBoolean("remember_me", false).apply()
                    }

                    val intent = Intent(this, profiling::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Kayıtlı değil, lütfen kaydolun.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Lütfen e-posta ve şifreyi girin.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
