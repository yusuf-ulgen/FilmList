@file:Suppress("DEPRECATION")

package com.example.filmlist.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.filmlist.data.local.AppDatabase
import com.example.filmlist.data.local.SessionManager
import com.example.filmlist.data.repository.AuthRepository
import com.example.filmlist.databinding.ActivityLogInBinding
import com.example.filmlist.ui.ViewModelFactory
import com.example.filmlist.ui.home.HomeScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setupObservers()

        binding.loginGirisId.setOnClickListener {
            val email = binding.loginMailId.text.toString()
            val password = binding.loginSifreId.text.toString()
            val rememberMe = binding.benihatirlaId.isChecked
            viewModel.login(email, password, rememberMe)
        }

        binding.loginSignUpId?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val sessionManager = SessionManager(this)
        val repository = AuthRepository(database.userDao(), sessionManager)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.loginResult.collectLatest { success ->
                if (success) {
                    Toast.makeText(this@LoginActivity, "Giriş Başarılı!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, HomeScreen::class.java))
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { errorMessage ->
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
