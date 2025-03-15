package com.example.filmlist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class AddContentActivity : AppCompatActivity() {

    // Bu, doğru yerinde `override` kullanımıdır
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_content)
    }

    // Menü öğelerini şişirirken
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.filmshowmenu, menu)
        return true
    }

    // Menü öğeleri seçildiğinde yapılacak işlemler
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filmekle -> {
                // Film ekleme sayfasına yönlendirme
                val intent = Intent(this, AddFilmActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.diziekle -> {
                // Dizi ekleme sayfasına yönlendirme
                val intent = Intent(this, AddShowActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
