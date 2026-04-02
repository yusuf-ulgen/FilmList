package com.example.filmlist.ui.add

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.filmlist.R
import com.example.filmlist.databinding.ActivityAddContentBinding

class AddContentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.filmshowmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filmekle -> {
                val intent = Intent(this, AddFilmActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.diziekle -> {
                val intent = Intent(this, AddShowActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
