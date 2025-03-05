package com.example.filmlist

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class categories : AppCompatActivity() {

    private lateinit var filmRecyclerView: RecyclerView
    private lateinit var diziRecyclerView: RecyclerView
    private lateinit var filmCategoryAdapter: CategoryAdapter
    private lateinit var diziCategoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categories)

        // Edge-to-edge padding handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Film RecyclerView initialization
        filmRecyclerView = findViewById(R.id.recyclerView)
        filmRecyclerView.layoutManager = LinearLayoutManager(this)

        // Dizi RecyclerView initialization
        diziRecyclerView = findViewById(R.id.recyclerView2)
        diziRecyclerView.layoutManager = LinearLayoutManager(this)

        // Film Kategorileri
        val filmCategories = listOf(
            Category("Aksiyon"), Category("Komedi"), Category("Dram"),
            Category("Macera"), Category("Bilim Kurgu"), Category("Gerilim"),
            Category("Romantik"), Category("Korku"), Category("Animasyon"),
            Category("Belgesel"), Category("Fantezi"), Category("Savaş"),
            Category("Müzikal"), Category("Biyografi"), Category("Suç")
        )

        // Dizi Kategorileri
        val diziCategories = listOf(
            Category("Drama"), Category("Komedi"), Category("Aksiyon"),
            Category("Romantik"), Category("Suç"), Category("Bilim Kurgu"),
            Category("Gerilim"), Category("Macera"), Category("Korku"),
            Category("Fantastik"), Category("Yerli"), Category("Müzikal"),
            Category("Belgesel"), Category("Hikaye"), Category("Kısa Dizi")
        )

        // Adapterları bağlama
        filmCategoryAdapter = CategoryAdapter(filmCategories)
        filmRecyclerView.adapter = filmCategoryAdapter

        diziCategoryAdapter = CategoryAdapter(diziCategories)
        diziRecyclerView.adapter = diziCategoryAdapter

        // Devam Et Butonuna Tıklama İşlevi
        val continueButton = findViewById<Button>(R.id.continueButton)
        continueButton.setOnClickListener {
            // HomeScreen Activity'ye geçiş
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
    }
}
