package com.example.filmlist.ui.categories

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmlist.databinding.ActivityCategoriesBinding
import com.example.filmlist.ui.home.HomeScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var viewModel: CategoriesViewModel
    private lateinit var filmCategoryAdapter: CategoryAdapter
    private lateinit var diziCategoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        enableEdgeToEdge()
        supportActionBar?.hide()

        viewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerViews()
        setupObservers()

        binding.continueButton.setOnClickListener {
            if (viewModel.isSelectionValid()) {
                val intent = Intent(this, HomeScreen::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Lütfen en az 3 film ve 3 dizi kategorisi seçin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerViews() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView2.layoutManager = LinearLayoutManager(this)

        filmCategoryAdapter = CategoryAdapter(mutableListOf())
        binding.recyclerView.adapter = filmCategoryAdapter

        diziCategoryAdapter = CategoryAdapter(mutableListOf())
        binding.recyclerView2.adapter = diziCategoryAdapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.filmCategories.collectLatest { categories ->
                filmCategoryAdapter.updateCategories(categories)
            }
        }

        lifecycleScope.launch {
            viewModel.diziCategories.collectLatest { categories ->
                diziCategoryAdapter.updateCategories(categories)
            }
        }
    }
}
