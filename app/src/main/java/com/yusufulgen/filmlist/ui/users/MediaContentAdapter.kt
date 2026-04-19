package com.yusufulgen.filmlist.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.yusufulgen.filmlist.R
import com.yusufulgen.filmlist.data.local.MediaContent
import com.yusufulgen.filmlist.databinding.ItemMovieBinding

class MediaContentAdapter(
    private val onItemLongClick: (MediaContent) -> Unit
) : RecyclerView.Adapter<MediaContentAdapter.ViewHolder>() {
    private var items: List<MediaContent> = emptyList()
    private var fullList: List<MediaContent> = emptyList()

    fun setItems(newItems: List<MediaContent>) {
        items = newItems
        fullList = newItems
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        items = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.title.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    fun sortList(criteria: String, ascending: Boolean) {
        items = when (criteria) {
            "DATE" -> if (ascending) items.sortedBy { it.date } else items.sortedByDescending { it.date }
            "RATING" -> if (ascending) items.sortedBy { it.rating } else items.sortedByDescending { it.rating }
            "TITLE" -> if (ascending) items.sortedBy { it.title } else items.sortedByDescending { it.title }
            else -> items
        }
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
                    placeholder(R.drawable.ic_movie_placeholder)
                    error(R.drawable.ic_movie_placeholder)
                    listener(
                        onSuccess = { _, _ ->
                            binding.posterPlaceholderText.visibility = android.view.View.GONE
                        },
                        onError = { _, _ ->
                            showPlaceholder(binding, item)
                        }
                    )
                }
            } else {
                showPlaceholder(binding, item)
            }
        }

        private fun showPlaceholder(binding: ItemMovieBinding, item: MediaContent) {
            binding.moviePoster.visibility = android.view.View.GONE
            binding.posterPlaceholderText.visibility = android.view.View.VISIBLE
            binding.posterPlaceholderText.text = item.title
            
            val bgColor = if (item.type == "FILM") 
                android.graphics.Color.parseColor("#E91E63") 
            else 
                android.graphics.Color.parseColor("#2196F3")
            binding.posterPlaceholderText.setBackgroundColor(bgColor)
        }
    }
}