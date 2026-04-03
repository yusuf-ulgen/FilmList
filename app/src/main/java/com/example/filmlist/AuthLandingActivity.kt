package com.example.filmlist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.filmlist.data.local.SessionManager
import com.example.filmlist.databinding.ActivityAuthLandingBinding
import com.example.filmlist.ui.auth.LoginActivity
import com.example.filmlist.ui.auth.SignUpActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthLandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        checkSessionAndNavigate()

        binding.loginButtonId.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signupButtonId.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun checkSessionAndNavigate() {
        val sessionManager = SessionManager(this)
        lifecycleScope.launch {
            if (sessionManager.isLoggedIn.first()) {
                startActivity(Intent(this@AuthLandingActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}
