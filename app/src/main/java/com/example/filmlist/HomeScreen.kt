package com.example.filmlist

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmlist.databinding.ActivityHomeScreenBinding

class HomeScreen : AppCompatActivity() {
    private lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Keşfet Butonuna Tıklama İşlevi
        binding.homeButton.setOnClickListener {
            // Keşfet sayfasını yenile
            binding.recyclerViewExplore.layoutManager = LinearLayoutManager(this)
            // RecyclerView içeriğini güncelle
        }

        // AI Butonuna Tıklama İşlevi
        binding.aiButton.setOnClickListener {
            // AI sohbet sayfasına git
            val intent = Intent(this, AiChatActivity::class.java)
            startActivity(intent)
        }

        // Ekleme Butonuna Tıklama İşlevi
        binding.addButton.setOnClickListener {
            // Dizi/Film ekleme sayfasına git
            val intent = Intent(this, AddContentActivity::class.java)
            startActivity(intent)
        }

        // Liste Butonuna Tıklama İşlevi
        binding.listButton.setOnClickListener {
            // Kullanıcı listelerine git
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        // Profil Butonuna Tıklama İşlevi
        binding.profileButton.setOnClickListener {
            // Kullanıcı profil sayfasına git
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
