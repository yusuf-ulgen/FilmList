package com.example.filmlist.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filmlist.data.repository.MovieRepository
import com.example.filmlist.databinding.ActivityHomeScreenBinding
import com.example.filmlist.ui.chat.AiChatActivity
import com.example.filmlist.ui.profile.ProfileActivity
import com.example.filmlist.ui.users.UserListActivity
import com.example.filmlist.ui.add.AddContentActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeScreen : AppCompatActivity() {
    private lateinit var binding: ActivityHomeScreenBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupUI()
        setupViewModel()
        setupObservers()
    }

    private fun setupUI() {
        adapter = MovieAdapter()
        binding.recyclerViewExplore.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewExplore.adapter = adapter
    }

    private fun setupViewModel() {
        val factory = com.example.filmlist.util.RepositoryProvider.provideViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                Toast.makeText(this@HomeScreen, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
