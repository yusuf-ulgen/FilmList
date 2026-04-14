package com.example.filmlist.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.filmlist.R
import com.example.filmlist.data.local.MediaContent
import com.example.filmlist.databinding.ItemMovieBinding

class MediaContentAdapter(
    private val onItemLongClick: (MediaContent) -> Unit
) : RecyclerView.Adapter<MediaContentAdapter.ViewHolder>() {
    private var items: List<MediaContent> = emptyList()

    fun setItems(newItems: List<MediaContent>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemLongClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemMovieBinding,
        private val onItemLongClick: (MediaContent) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MediaContent) {
            binding.root.setOnLongClickListener {
                onItemLongClick(item)
                true
            }
            binding.movieTitle.text = item.title
            binding.movieReleaseDate.text = "${item.type} - ${item.date}"
            binding.movieRating.text = "Puan: ${item.rating}/5"
            
            val posterUrl = if (!item.posterPath.isNullOrEmpty()) {
                "https://image.tmdb.org/t/p/w500${item.posterPath}"
            } else {
                null
            }

            if (posterUrl != null) {
                binding.posterPlaceholderText.visibility = android.view.View.GONE
                binding.moviePoster.visibility = android.view.View.VISIBLE
                binding.moviePoster.load(posterUrl) {
                    crossfade(true)
                    error(R.drawable.ic_launcher_background) // Fallback error
                    listener(
                        onSuccess = { _, _ ->
                            binding.posterPlaceholderText.visibility = android.view.View.GONE
                        },
                        onError = { _, _ ->
                            binding.moviePoster.visibility = android.view.View.GONE
                            binding.posterPlaceholderText.visibility = android.view.View.VISIBLE
                            binding.posterPlaceholderText.text = item.title
                        }
                    )
                }
            } else {
                binding.moviePoster.visibility = android.view.View.GONE
                binding.posterPlaceholderText.visibility = android.view.View.VISIBLE
                binding.posterPlaceholderText.text = item.title
                // Use a different color based on type for visual variety
                val bgColor = if (item.type == "FILM") 
                    android.graphics.Color.parseColor("#E91E63") // Pinkish
                else 
                    android.graphics.Color.parseColor("#2196F3") // Blueish
                binding.posterPlaceholderText.setBackgroundColor(bgColor)
            }
        }
    }
}
