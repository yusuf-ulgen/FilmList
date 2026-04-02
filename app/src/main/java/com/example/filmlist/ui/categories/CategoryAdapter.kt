package com.example.filmlist.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filmlist.R
import com.example.filmlist.databinding.ItemCategoryBinding

class CategoryAdapter(private var categoryList: MutableList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    fun updateCategories(newCategories: List<Category>) {
        categoryList = newCategories.toMutableList()
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val category = categoryList[position]
                    category.isSelected = !category.isSelected
                    notifyItemChanged(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.binding.categoryName.text = category.name

        if (category.isSelected) {
            holder.binding.categoryName.setBackgroundColor(holder.itemView.context.getColor(R.color.green))
        } else {
            holder.binding.categoryName.setBackgroundColor(holder.itemView.context.getColor(android.R.color.transparent))
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
