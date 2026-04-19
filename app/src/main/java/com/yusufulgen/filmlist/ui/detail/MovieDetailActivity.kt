package com.yusufulgen.filmlist.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.yusufulgen.filmlist.R
import com.yusufulgen.filmlist.data.remote.Movie
import com.yusufulgen.filmlist.databinding.ActivityMovieDetailBinding
import com.yusufulgen.filmlist.util.RepositoryProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MovieDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailBinding
    private val castAdapter = CastAdapter()
    private lateinit var similarAdapter: SimilarMoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        val mediaType = intent.getStringExtra("MEDIA_TYPE") ?: "movie"
        val isTv = mediaType == "tv" || mediaType == "show"
        
        val title = intent.getStringExtra("MOVIE_TITLE") ?: ""
        val overview = intent.getStringExtra("MOVIE_OVERVIEW") ?: ""
        val rating = intent.getDoubleExtra("MOVIE_RATING", 0.0)
        val date = intent.getStringExtra("MOVIE_DATE") ?: ""
        val posterPath = intent.getStringExtra("MOVIE_POSTER") ?: ""

        setupUI(title, overview, rating, date, posterPath)
        setupAdapters()
        fetchDetails(movieId, isTv)
        setupTrailer(movieId, isTv)

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupUI(title: String, overview: String, rating: Double, date: String, poster: String) {
        binding.movieTitle.text = title
        binding.movieOverview.text = overview
        binding.movieRating.text = String.format("%.1f", rating)
        binding.movieDate.text = date

        binding.movieBackdrop.load("https://image.tmdb.org/t/p/w780$poster") {
            crossfade(true)
            placeholder(R.drawable.ic_movie_placeholder)
            error(R.drawable.ic_movie_placeholder)
        }
    }

    private fun setupAdapters() {
        binding.recyclerViewCast.adapter = castAdapter
        
        similarAdapter = SimilarMoviesAdapter { movie ->
            val intent = Intent(this, MovieDetailActivity::class.java).apply {
                putExtra("MOVIE_ID", movie.id)
                putExtra("MOVIE_TITLE", movie.title)
                putExtra("MOVIE_OVERVIEW", movie.overview)
                putExtra("MOVIE_RATING", movie.voteAverage)
                putExtra("MOVIE_DATE", movie.date)
                putExtra("MOVIE_POSTER", movie.posterPath)
                putExtra("MEDIA_TYPE", movie.mediaType)
            }
            startActivity(intent)
        }
        binding.recyclerViewSimilar.adapter = similarAdapter
    }

    private fun fetchDetails(movieId: Int, isTv: Boolean) {
        val repository = RepositoryProvider.provideMovieRepository(this)
        
        lifecycleScope.launch {
            // Paralel veri çekme
            val detailsDeferred = async { repository.getMovieDetails(movieId, isTv) }
            val creditsDeferred = async { repository.getMovieCredits(movieId, isTv) }
            val similarDeferred = async { repository.getSimilarContent(movieId, isTv) }

            val details = detailsDeferred.await()
            val credits = creditsDeferred.await()
            val similar = similarDeferred.await()

            // UI Güncelleme - Detaylar
            details?.let {
                val runtime = if (isTv) it.episodeRuntime?.firstOrNull() else it.runtime
                binding.movieRuntime.text = runtime?.let { min -> "${min / 60}s ${min % 60}dk" } ?: ""
                binding.movieGenres.text = it.genres.joinToString(", ") { genre -> genre.name }
            }

            // UI Güncelleme - Credits
            credits?.let {
                val director = it.crew.find { member -> member.job == "Director" || member.job == "Executive Producer" }
                binding.directorText.text = director?.let { d -> "Yönetmen: ${d.name}" } ?: ""
                castAdapter.setList(it.cast.take(15)) // İlk 15 oyuncuyu göster
            }

            // UI Güncelleme - Benzerler
            similarAdapter.setList(similar)
        }
    }

    private fun setupTrailer(movieId: Int, isTv: Boolean) {
        val repository = RepositoryProvider.provideMovieRepository(this)
        
        binding.playTrailerButton.setOnClickListener {
            binding.playTrailerButton.isEnabled = false
            lifecycleScope.launch {
                val videoKey = repository.getMovieVideoKey(movieId, isTv)
                if (videoKey != null) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoKey))
                    startActivity(intent)
                    binding.playTrailerButton.isEnabled = true
                } else {
                    Toast.makeText(this@MovieDetailActivity, "Fragman bulunamadı.", Toast.LENGTH_SHORT).show()
                    binding.playTrailerButton.isEnabled = true
                }
            }
        }
    }
}
