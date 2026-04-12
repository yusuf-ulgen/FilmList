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

            binding.moviePoster.load(posterUrl) {
                crossfade(true)
                placeholder(if (item.type == "FILM") R.drawable.ic_home else R.drawable.ic_list)
                error(if (item.type == "FILM") R.drawable.ic_home else R.drawable.ic_list)
            }
        }
    }
}
