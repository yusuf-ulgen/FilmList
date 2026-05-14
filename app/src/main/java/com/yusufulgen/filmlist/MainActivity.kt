package com.yusufulgen.filmlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yusufulgen.filmlist.databinding.ActivityMainBinding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

import android.view.View
import com.yusufulgen.filmlist.R
import com.yusufulgen.filmlist.util.AppUpdateManager
import android.widget.TextView
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment

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

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Check for updates and release notes
        val updateManager = AppUpdateManager(this)
        updateManager.checkAndShowReleaseNotes()
        updateManager.checkForUpdates()

        // Bottom Navigation için padding ekle (İçeriğin arkada kalmaması için)
        binding.bottomNavigation.post {
            binding.navHostFragment.setPadding(0, 0, 0, binding.bottomNavigation.height)
        }

        // Başlıkları sayfalara göre güncelle
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Reset header position when changing pages
            binding.appBarLayout.setExpanded(true, true)
            binding.appBarLayout.isLifted = false
            
            val params = binding.titleContainer.layoutParams as com.google.android.material.appbar.AppBarLayout.LayoutParams
            if (destination.id == R.id.navigation_home) {
                params.scrollFlags = com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or 
                                    com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or 
                                    com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
            } else {
                params.scrollFlags = 0 // No scroll
            }
            binding.titleContainer.layoutParams = params

            binding.navTitle.text = when (destination.id) {
                R.id.navigation_home -> "Keşfet"
                R.id.navigation_ai -> "Yapay Zeka"
                R.id.navigation_add -> "Ekle"
                R.id.navigation_list -> "Listeler"
                R.id.navigation_profile -> "Profil"
                else -> ""
            }

            // Profile sayfasında ayarlar butonu göster
            binding.settingsButtonMain.visibility = if (destination.id == R.id.navigation_profile) View.VISIBLE else View.GONE
            binding.settingsButtonMain.setOnClickListener {
                // ProfileFragment'a erişip menüyü göster
                val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    ?.childFragmentManager?.fragments?.firstOrNull { it is com.yusufulgen.filmlist.ui.profile.ProfileFragment }
                if (currentFragment is com.yusufulgen.filmlist.ui.profile.ProfileFragment) {
                    currentFragment.showSettingsMenuFromActivity(it)
                }
            }

            // Sayfa bazlı renk ve arka plan ayarları
            if (destination.id == R.id.navigation_home) {
                binding.titleContainer.setBackgroundColor(getColor(R.color.black))
                binding.navTitle.setTextColor(getColor(R.color.white))
                binding.settingsButtonMain.visibility = View.GONE // Home'da gizle
            } else {
                binding.titleContainer.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                binding.navTitle.setTextColor(getColor(R.color.black))
                binding.settingsButtonMain.setColorFilter(getColor(R.color.black))
            }
            
            // Material3 AppBarLayout'un otomatik kararmasını (lift) engellemek için
            binding.appBarLayout.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            binding.appBarLayout.outlineProvider = null
        }
    }

    fun setHeaderTranslation(translationY: Float) {
        binding.titleContainer.translationY = translationY
    }

    fun getHeaderHeight(): Int {
        return binding.titleContainer.height
    }

    fun showNotification(message: String, isError: Boolean = false) {
        com.yusufulgen.filmlist.util.NotificationHelper.showNotification(this, message, isError)
    }

    companion object {
        fun showNotification(fragment: androidx.fragment.app.Fragment, message: String, isError: Boolean = false) {
            (fragment.requireActivity() as? MainActivity)?.showNotification(message, isError)
        }
    }
}

