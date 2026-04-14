package com.example.filmlist.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmlist.databinding.FragmentHomeBinding
import com.example.filmlist.util.RepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class   HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModel()
        setupObservers()
    }

    private fun setupUI() {
        adapter = HomeAdapter(
            onVideoClick = { movieId ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val repository = RepositoryProvider.provideMovieRepository(requireContext())
                    val videoKey = repository.getMovieVideoKey(movieId)
                    if (videoKey != null) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoKey"))
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "Fragman bulunamadı.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onItemClick = { movie ->
                val intent = Intent(requireContext(), com.example.filmlist.ui.detail.MovieDetailActivity::class.java).apply {
                    putExtra("MOVIE_ID", movie.id)
                    putExtra("MOVIE_TITLE", movie.title)
                    putExtra("MOVIE_OVERVIEW", movie.overview)
                    putExtra("MOVIE_RATING", movie.voteAverage)
                    putExtra("MOVIE_DATE", movie.releaseDate)
                    putExtra("MOVIE_POSTER", movie.posterPath)
                    putExtra("MEDIA_TYPE", movie.mediaType)
                }
                startActivity(intent)
            }
        )

        binding.recyclerViewExplore.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewExplore.adapter = adapter

        binding.recyclerViewExplore.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore()
                }
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshMovies()
        }
    }

    private fun setupViewModel() {
        val factory = RepositoryProvider.provideViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.feedItems.collectLatest { items ->
                adapter.setItems(items)
                // Filmleri göster, RecyclerView zaten match_parent
                binding.recyclerViewExplore.visibility = View.VISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading && !binding.swipeRefreshLayout.isRefreshing) View.VISIBLE else View.GONE
                if (!isLoading) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
