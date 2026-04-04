package com.example.filmlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.filmlist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Navigasyon kontrolcüsünü kur
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Başlıkları sayfalara göre güncelle
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navTitle.text = when (destination.id) {
                R.id.navigation_home -> "Keşfet"
                R.id.navigation_ai -> "Yapay Zeka"
                R.id.navigation_add -> "Ekle"
                R.id.navigation_list -> "Listeler"
                R.id.navigation_profile -> "Profil"
                else -> ""
            }
        }
    }
}
