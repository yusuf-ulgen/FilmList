package com.yusufulgen.filmlist.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.yusufulgen.filmlist.R
import com.yusufulgen.filmlist.data.local.MediaContent
import com.yusufulgen.filmlist.data.repository.StatsRepository
import com.yusufulgen.filmlist.databinding.ItemProfileHeaderBinding
import com.yusufulgen.filmlist.databinding.ItemProfileWatchedBinding

class ProfileGridAdapter(
    private val onProfileImageClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private var stats: StatsRepository.UserStats? = null
    private var username: String? = null
    private var profileImageUri: String? = null
    private var watchedItems: List<MediaContent> = emptyList()

    fun updateData(newStats: StatsRepository.UserStats?, newUsername: String?, newProfileImageUri: String?, newItems: List<MediaContent>) {
        stats = newStats
        username = newUsername
        profileImageUri = newProfileImageUri
        watchedItems = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(ItemProfileHeaderBinding.inflate(inflater, parent, false))
        } else {
            ItemViewHolder(ItemProfileWatchedBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind()
        } else if (holder is ItemViewHolder) {
            holder.bind(watchedItems[position - 1])
        }
    }

    override fun getItemCount(): Int = watchedItems.size + 1

    inner class HeaderViewHolder(private val binding: ItemProfileHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.usernameText.text = username ?: "Kullanıcı"
            binding.totalWatchedText.text = stats?.totalWatched?.toString() ?: "0"
            binding.bioText.text = "Favori Tür: ${stats?.favoriteGenre ?: "Belirlenmedi"}\nDizi Alışkanlığı: ${stats?.showHabit ?: "Henüz veri yok"}"
            
            if (profileImageUri != null) {
                binding.profileImage.load(profileImageUri) {
                    transformations(CircleCropTransformation())
                    error(R.drawable.ic_profile)
                }
                binding.profileImage.imageTintList = null
                binding.profileImage.background = null
                binding.profileImage.setPadding(0, 0, 0, 0)
            } else {
                binding.profileImage.setImageResource(R.drawable.ic_profile)
            }

            binding.profileImage.setOnClickListener { onProfileImageClick() }
        }
    }

    inner class ItemViewHolder(private val binding: ItemProfileWatchedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MediaContent) {
            if (item.posterPath != null) {
                val fullUrl = "https://image.tmdb.org/t/p/w342${item.posterPath}"
                binding.watchedPoster.load(fullUrl) {
                    crossfade(true)
                    placeholder(R.color.surface)
                }
            } else {
                binding.watchedPoster.setImageResource(R.drawable.ic_movie_placeholder)
            }
        }
    }
}
