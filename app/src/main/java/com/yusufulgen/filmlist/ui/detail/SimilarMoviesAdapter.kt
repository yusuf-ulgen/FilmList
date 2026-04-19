package com.yusufulgen.filmlist.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.yusufulgen.filmlist.data.remote.Movie
import com.yusufulgen.filmlist.databinding.ItemMovieMiniBinding

class SimilarMoviesAdapter(private val onItemClick: (Movie) -> Unit) : RecyclerView.Adapter<SimilarMoviesAdapter.ViewHolder>() {
    private var movies: List<Movie> = emptyList()

    fun setList(list: List<Movie>) {
        movies = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieMiniBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    class ViewHolder(
        private val binding: ItemMovieMiniBinding,
        private val onItemClick: (Movie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.root.setOnClickListener { onItemClick(movie) }
            binding.movieTitle.text = movie.title
            binding.movieRating.text = String.format("⭐ %.1f", movie.voteAverage)
            binding.moviePoster.load(movie.getFullPosterUrl()) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
            }
        }
    }
}
