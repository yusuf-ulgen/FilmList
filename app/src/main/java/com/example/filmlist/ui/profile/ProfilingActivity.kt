package com.example.filmlist.ui.profile

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.filmlist.data.local.AppDatabase
import com.example.filmlist.data.local.SessionManager
import com.example.filmlist.databinding.ActivityProfilingBinding
import com.example.filmlist.ui.auth.LoginActivity
import com.example.filmlist.ui.auth.ViewModelFactory
import com.example.filmlist.ui.categories.CategoriesActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfilingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfilingBinding
    private lateinit var viewModel: ProfilingViewModel

    private val PERMISSION_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setupObservers()

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }

        binding.devamEtButton.setOnClickListener {
            val nickname = binding.nicknameEditText.text.toString()
            if (nickname.isNotEmpty()) {
                viewModel.saveProfile(nickname)
            } else {
                binding.nicknameEditText.error = "Lütfen nickname girin."
            }
        }

        binding.avatarImage.setOnClickListener {
            openGallery()
        }
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val sessionManager = SessionManager(this)
        val factory = ViewModelFactory(AuthRepository(database.userDao(), sessionManager))
        // Note: Using AuthRepository for simple UserDao access here for simplicity, or create a specific ProfileRepository
        viewModel = ViewModelProvider(this, factory)[ProfilingViewModel::class.java]
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.profileSaved.collectLatest { success ->
                if (success) {
                    startActivity(Intent(this@ProfilingActivity, CategoriesActivity::class.java))
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                Toast.makeText(this@ProfilingActivity, error, Toast.LENGTH_SHORT).show()
                if (error.contains("Oturum")) {
                    startActivity(Intent(this@ProfilingActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            binding.avatarImage.setImageURI(imageUri)
        }
    }
}
