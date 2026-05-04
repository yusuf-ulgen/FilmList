package com.yusufulgen.filmlist.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.yusufulgen.filmlist.R
import com.yusufulgen.filmlist.databinding.ActivityActorDetailBinding
import com.yusufulgen.filmlist.util.RepositoryProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ActorDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityActorDetailBinding
    private lateinit var creditsAdapter: SimilarMoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val personId = intent.getIntExtra("PERSON_ID", -1)
        if (personId == -1) {
            finish()
            return
        }

        setupAdapters()
        fetchActorDetails(personId)

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupAdapters() {
        creditsAdapter = SimilarMoviesAdapter { movie ->
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
        binding.recyclerViewCredits.adapter = creditsAdapter
    }

    private fun fetchActorDetails(personId: Int) {
        val repository = RepositoryProvider.provideMovieRepository(this)
        
        lifecycleScope.launch {
            val detailsDeferred = async { repository.getPersonDetails(personId) }
            val creditsDeferred = async { repository.getPersonCredits(personId) }

            val details = detailsDeferred.await()
            val credits = creditsDeferred.await()

            details?.let {
                binding.actorName.text = it.name
                binding.collapsingToolbar.title = it.name
                binding.actorBiography.text = it.biography ?: "Biyografi bulunamadı."
                
                val birthInfo = StringBuilder()
                if (!it.birthday.isNullOrEmpty()) birthInfo.append("Doğum: ${it.birthday}")
                if (!it.placeOfBirth.isNullOrEmpty()) birthInfo.append(" - ${it.placeOfBirth}")
                binding.actorPersonalInfo.text = birthInfo.toString()

                binding.actorProfileImage.load(it.getFullProfileUrl()) {
                    crossfade(true)
                    placeholder(R.drawable.ic_movie_placeholder)
                    error(R.drawable.ic_movie_placeholder)
                }
            }

            if (credits.isNotEmpty()) {
                creditsAdapter.setList(credits)
            }
        }
    }
}
