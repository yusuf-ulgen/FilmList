package com.example.filmlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.filmlist.databinding.ActivityMainBinding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge desteğini etkinleştir
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Sistem çubukları için padding ekle (Inset handling)
        ViewCompat.setOnApplyWindowInsetsListener(binding.titleContainer) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, insets.top, 0, 0)
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = insets.bottom)
            windowInsets
        }

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

            // Keşfet sayfasında başlık alanını siyah yap (Görsellik için)
            if (destination.id == R.id.navigation_home) {
                binding.titleContainer.setBackgroundColor(getColor(R.color.black))
                binding.navTitle.setTextColor(getColor(R.color.white))
            } else {
                binding.titleContainer.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                binding.navTitle.setTextColor(getColor(R.color.black))
            }
        }
    }
}
