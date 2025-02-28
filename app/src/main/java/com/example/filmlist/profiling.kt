package com.example.filmlist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView

class profiling : AppCompatActivity() {
    private lateinit var avatarImage: ShapeableImageView
    private lateinit var nicknameEditText: EditText
    private lateinit var devamEtButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiling)

        avatarImage = findViewById(R.id.avatar_image)
        nicknameEditText = findViewById(R.id.nickname_edit_text)
        devamEtButton = findViewById(R.id.devamEt_button)

        // Devam Et butonuna tıklanırsa
        devamEtButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()

            if (nickname.isNotEmpty()) {
                // Nickname'i kaydediyoruz
                val prefs = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                prefs.edit().putString("user_nickname", nickname).apply()

                // Profiling'den CategoriesActivity'ye geçiş
                val intent = Intent(this, categories::class.java)
                startActivity(intent)
                finish()
            } else {
                // Nickname boşsa hata mesajı göster
                nicknameEditText.error = "Lütfen nickname girin."
            }
        }

        // Avatar'a tıklanarak bir görsel seçme işlemi eklenebilir (isteğe bağlı)
        avatarImage.setOnClickListener {
            // Avatar seçimi yapılabilir
        }
    }
}
