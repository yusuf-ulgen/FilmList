package com.example.filmlist.ui.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.filmlist.data.remote.Movie
import com.example.filmlist.databinding.ItemFeedPostBinding

class HomeAdapter(
    private val onVideoClick: (Int) -> Unit
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private var items: List<FeedItem> = emptyList()

    fun setItems(newItems: List<FeedItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onVideoClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (item is FeedItem.MovieDiscovery) {
            holder.bind(item.movie, item.isAiSuggested)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemFeedPostBinding,
        private val onVideoClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie, isAiSuggested: Boolean) {
            binding.postTitle.text = movie.title
            binding.postDescription.text = movie.overview
            binding.postPoster.load(movie.getFullPosterUrl()) {
                crossfade(true)
            }
            
            // In a real app, metadata (genre, cast) would be fetched from TMDB
            // For now we show the AI marker or general metadata
            binding.postMetadata.text = if (isAiSuggested) "Sana Özel Öneri ✨" else "Popüler • TMDB"

            binding.watchTrailerButton.setOnClickListener {
                onVideoClick(movie.id)
            }
        }
    }
}
