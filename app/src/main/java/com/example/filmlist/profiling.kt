package com.example.filmlist

import android.content.Intent
import android.provider.Settings
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.google.android.material.imageview.ShapeableImageView
import android.content.SharedPreferences
import android.net.Uri
import android.provider.MediaStore

class profiling : AppCompatActivity() {

    private lateinit var avatarImage: ShapeableImageView
    private lateinit var nicknameEditText: EditText
    private lateinit var devamEtButton: Button

    private val PREFS_NAME = "UserPreferences"
    private val KEY_PERMISSION_GRANTED = "permission_granted" // İzin durumu
    private val PERMISSION_REQUEST_CODE = 1002 // İzin istemek için kullanılacak kod


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_profiling)

        avatarImage = findViewById(R.id.avatar_image)
        nicknameEditText = findViewById(R.id.nickname_edit_text)
        devamEtButton = findViewById(R.id.devamEt_button)

        // İzinleri kontrol et ve kullanıcıdan iste
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        } else {
            // Eğer izin verilmişse işlemi yap
        }


        // Devam Et butonuna tıklanırsa
        devamEtButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()

            if (nickname.isNotEmpty()) {
                // Nickname'i kaydediyoruz
                val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                prefs.edit().putString("user_nickname", nickname).apply()

                // Avatar seçilmemişse default avatarı ekliyoruz
                if (avatarImage.drawable == null) {
                    avatarImage.setImageResource(R.drawable.ic_profile)  // Default avatar
                }

                // Profiling'den CategoriesActivity'ye geçiş
                val intent = Intent(this, categories::class.java)
                startActivity(intent)
                finish()
            } else {
                // Nickname boşsa hata mesajı göster
                nicknameEditText.error = "Lütfen nickname girin."
            }
        }

        // Avatar'a tıklanarak bir görsel seçme işlemi
        avatarImage.setOnClickListener {
            // Eğer kullanıcı izni her zaman vermişse, doğrudan galeriyi açıyoruz
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            val isPermissionGranted = prefs.getBoolean(KEY_PERMISSION_GRANTED, false)

            if (isPermissionGranted) {
                openGallery()
            } else {
                // Eğer izin verilmediyse, kullanıcıdan izin istiyoruz
                requestGalleryPermission()
            }
        }
    }

    // Galeriye erişim iznini kontrol etme ve izin isteme
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, 1001)
    }

    // İzin istemek için seçenekler sunma
    private fun requestGalleryPermission() {
        // İzin isteyerek kullanıcıya seçenek sunuyoruz
        val permissionDialog = PermissionDialog()
        permissionDialog.setPermissionListener(object : PermissionDialog.PermissionListener {
            override fun onPermissionGranted(chooseAlways: Boolean) {
                // Eğer kullanıcı her zaman izin verirse, durumu kaydediyoruz
                val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                val editor = prefs.edit()

                if (chooseAlways) {
                    // Her zaman izin verilecekse, izni kalıcı olarak kaydediyoruz
                    editor.putBoolean(KEY_PERMISSION_GRANTED, true)
                }
                editor.apply()

                openGallery()
            }

            override fun onPermissionDenied() {
                Toast.makeText(this@profiling, "İzin verilmedi", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionChangeRequested() {
                TODO("Not yet implemented")
            }

        })
        permissionDialog.show(supportFragmentManager, "permission_dialog")
    }

    // Uygulama izinleri sayfasına yönlendirme
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + packageName)
        startActivity(intent) // Uygulama izinleri sayfasını açıyoruz
    }
}
