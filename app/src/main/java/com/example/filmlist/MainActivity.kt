package com.example.filmlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // VideoView ile video oynatma
        val videoView: VideoView = findViewById(R.id.videoView_id)
        val videoUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.videos)
        videoView.setVideoURI(videoUri)
        videoView.start()

        // Video döngüye alma (Bitince tekrar başlat)
        videoView.setOnCompletionListener {
            videoView.start()
        }

        // Sign Up butonuna tıklanması durumunda signUp'a yönlendirme
        val signUpButton: Button = findViewById(R.id.signup_button_id)
        signUpButton.setOnClickListener {
            val intent = Intent(this, signUp::class.java)
            startActivity(intent)
        }

        // Log In butonuna tıklanması durumunda logIn'e yönlendirme
        val loginButton: Button = findViewById(R.id.login_button_id)
        loginButton.setOnClickListener {
            val intent = Intent(this, logIn::class.java)
            startActivity(intent)
        }

        // Edge to Edge desteği ve pencere kenar boşluklarının uygulanması
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
