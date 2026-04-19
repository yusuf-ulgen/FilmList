package com.yusufulgen.filmlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.yusufulgen.filmlist.data.local.SessionManager
import com.yusufulgen.filmlist.databinding.ActivityAuthLandingBinding
import com.yusufulgen.filmlist.ui.auth.LoginActivity
import com.yusufulgen.filmlist.ui.auth.SignUpActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthLandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Önce içeriği gizle, session kontrolü tamamlayınca göster
        binding.root.visibility = View.INVISIBLE

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
            val isLoggedIn = sessionManager.isLoggedIn.first()
            val rememberMe = sessionManager.rememberMe.first()
            val isActiveInProcess = sessionManager.isSessionActiveInProcess()

            if (isLoggedIn) {
                if (!rememberMe && !isActiveInProcess) {
                    // Beni Hatırla işaretli değil ve uygulama yeni açıldı -> Oturumu temizle
                    sessionManager.clearSession()
                    binding.root.visibility = View.VISIBLE
                } else {
                    // Oturum geçerli (Beni Hatırla açık veya mevcut oturum süreci devam ediyor)
                    sessionManager.setSessionActive()
                    val intent = Intent(this@AuthLandingActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            } else {
                // Oturum yok, landing sayfasını göster
                binding.root.visibility = View.VISIBLE
            }
        }
    }
}
