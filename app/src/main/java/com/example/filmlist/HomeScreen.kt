package com.example.filmlist

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        // Keşfet Butonuna Tıklama İşlevi
        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        homeButton.setOnClickListener {
            // Keşfet sayfasını yenile
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewExplore)
            recyclerView.layoutManager = LinearLayoutManager(this)
            // RecyclerView içeriğini güncelle
        }

        // AI Butonuna Tıklama İşlevi
        val aiButton = findViewById<ImageButton>(R.id.aiButton)
        aiButton.setOnClickListener {
            // AI sohbet sayfasına git
            val intent = Intent(this, AiChatActivity::class.java)
            startActivity(intent)
        }

        // Ekleme Butonuna Tıklama İşlevi
        val addButton = findViewById<ImageButton>(R.id.addButton)
        addButton.setOnClickListener {
            // Dizi/Film ekleme sayfasına git
            val intent = Intent(this, AddContentActivity::class.java)
            startActivity(intent)
        }

        // Liste Butonuna Tıklama İşlevi
        val listButton = findViewById<ImageButton>(R.id.listButton)
        listButton.setOnClickListener {
            // Kullanıcı listelerine git
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        // Profil Butonuna Tıklama İşlevi
        val profileButton = findViewById<ImageButton>(R.id.profileButton)
        profileButton.setOnClickListener {
            // Kullanıcı profil sayfasına git
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
