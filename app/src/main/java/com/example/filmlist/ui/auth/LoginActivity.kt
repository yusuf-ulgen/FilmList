@file:Suppress("DEPRECATION")

package com.example.filmlist.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.filmlist.MainActivity
import com.example.filmlist.databinding.ActivityLogInBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var viewModel: LoginViewModel
    private var navigated = false

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
        val factory = com.example.filmlist.util.RepositoryProvider.provideViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.loginResult.collectLatest { success ->
                if (success && !navigated) {
                    navigated = true
                    Toast.makeText(this@LoginActivity, "Giriş Başarılı!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
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
