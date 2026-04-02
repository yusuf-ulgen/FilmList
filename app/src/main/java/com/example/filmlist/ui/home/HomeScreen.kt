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

        binding.aiButton.setOnClickListener {
            startActivity(Intent(this, AiChatActivity::class.java))
        }

        binding.profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.listButton.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
        }

        binding.addButton.setOnClickListener {
            startActivity(Intent(this, AddContentActivity::class.java))
        }
    }

    private fun setupViewModel() {
        val repository = MovieRepository()
        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.popularMovies.collectLatest { movies ->
                adapter.setMovies(movies)
            }
        }

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
