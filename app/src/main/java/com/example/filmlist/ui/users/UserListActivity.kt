package com.example.filmlist.ui.users

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmlist.databinding.ActivityUserListBinding
import com.example.filmlist.ui.ViewModelFactory
import com.example.filmlist.util.RepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserListBinding
    private lateinit var viewModel: UserListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        supportActionBar?.hide()

        setupViewModel()
        setupObservers()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        // Note: You would typically create a MediaContentAdapter here
    }

    private fun setupViewModel() {
        val factory = RepositoryProvider.provideViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[UserListViewModel::class.java]
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.userMediaContent.collectLatest { content ->
                // adapter.setItems(content)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }
}