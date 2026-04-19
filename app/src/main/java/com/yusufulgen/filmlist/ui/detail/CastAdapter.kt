package com.yusufulgen.filmlist.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.yusufulgen.filmlist.data.remote.Cast
import com.yusufulgen.filmlist.databinding.ItemCastBinding

class CastAdapter : RecyclerView.Adapter<CastAdapter.ViewHolder>() {
    private var castList: List<Cast> = emptyList()

    fun setList(list: List<Cast>) {
        castList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(castList[position])
    }

    override fun getItemCount(): Int = castList.size

    class ViewHolder(private val binding: ItemCastBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cast: Cast) {
            binding.castName.text = cast.name
            binding.castCharacter.text = cast.character
            binding.castImage.load(cast.getFullProfileUrl()) {
                crossfade(true)
                transformations(CircleCropTransformation())
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
        }
    }
}
