package com.example.filmlist.ui.detail

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.filmlist.databinding.ActivityMovieDetailBinding
import com.example.filmlist.util.RepositoryProvider
import kotlinx.coroutines.launch

class MovieDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        val title = intent.getStringExtra("MOVIE_TITLE") ?: ""
        val overview = intent.getStringExtra("MOVIE_OVERVIEW") ?: ""
        val rating = intent.getDoubleExtra("MOVIE_RATING", 0.0)
        val date = intent.getStringExtra("MOVIE_DATE") ?: ""
        val posterPath = intent.getStringExtra("MOVIE_POSTER") ?: ""

        setupUI(title, overview, rating, date, posterPath)
        setupTrailer(movieId)

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupUI(title: String, overview: String, rating: Double, date: String, poster: String) {
        binding.movieTitle.text = title
        binding.movieOverview.text = overview
        binding.movieRating.text = "⭐ $rating"
        binding.movieDate.text = date

        binding.movieBackdrop.load("https://image.tmdb.org/t/p/w780$poster") {
            crossfade(true)
        }
    }

    private fun setupTrailer(movieId: Int) {
        val repository = RepositoryProvider.provideMovieRepository(this)
        
        binding.playTrailerButton.setOnClickListener {
            binding.playTrailerButton.isEnabled = false
            lifecycleScope.launch {
                val videoKey = repository.getMovieVideoKey(movieId)
                if (videoKey != null) {
                    playYouTubeVideo(videoKey)
                } else {
                    android.widget.Toast.makeText(this@MovieDetailActivity, "Fragman bulunamadı.", android.widget.Toast.LENGTH_SHORT).show()
                    binding.playTrailerButton.isEnabled = true
                }
            }
        }
    }

    private fun playYouTubeVideo(videoKey: String) {
        binding.trailerWebview.visibility = View.VISIBLE
        binding.trailerWebview.settings.javaScriptEnabled = true
        binding.trailerWebview.webChromeClient = WebChromeClient()
        binding.trailerWebview.webViewClient = WebViewClient()
        
        val html = """
            <html>
            <body style="margin:0;padding:0;">
                <iframe width="100%" height="100%" src="https://www.youtube.com/embed/$videoKey?autoplay=1" frameborder="0" allowfullscreen></iframe>
            </body>
            </html>
        """.trimIndent()
        
        binding.trailerWebview.loadData(html, "text/html", "utf-8")
    }
}
