package com.example.filmlist.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.filmlist.data.remote.Movie
import com.example.filmlist.databinding.ItemMovieBinding

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val movies = mutableListOf<Movie>()

    fun setMovies(newMovies: List<Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.binding.movieTitle.text = movie.title
        holder.binding.movieRating.text = "⭐ ${movie.voteAverage}"
        
        holder.binding.moviePoster.load(movie.getFullPosterUrl()) {
            crossfade(true)
            placeholder(android.R.drawable.progress_indeterminate_horizontal)
        }

        holder.itemView.setOnClickListener {
            val intent = android.content.Intent(holder.itemView.context, com.example.filmlist.ui.detail.MovieDetailActivity::class.java)
            intent.putExtra("MOVIE_ID", movie.id)
            intent.putExtra("MOVIE_TITLE", movie.title)
            intent.putExtra("MOVIE_OVERVIEW", movie.overview)
            intent.putExtra("MOVIE_RATING", movie.voteAverage)
            intent.putExtra("MOVIE_DATE", movie.releaseDate)
            intent.putExtra("MOVIE_POSTER", movie.posterPath)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = movies.size
}
