package com.yusufulgen.filmlist.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.yusufulgen.filmlist.data.remote.Provider
import com.yusufulgen.filmlist.databinding.ItemProviderBinding

class WatchProvidersAdapter : RecyclerView.Adapter<WatchProvidersAdapter.ViewHolder>() {
    private var providers: List<Provider> = emptyList()

    fun setList(newList: List<Provider>) {
        providers = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProviderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(providers[position])
    }

    override fun getItemCount(): Int = providers.size

    class ViewHolder(private val binding: ItemProviderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(provider: Provider) {
            binding.providerName.text = provider.providerName
            binding.providerLogo.load(provider.getFullLogoUrl()) {
                crossfade(true)
            }
        }
    }
}
