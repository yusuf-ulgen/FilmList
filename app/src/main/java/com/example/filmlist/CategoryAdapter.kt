package com.example.filmlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.filmlist.R

class CategoryAdapter(private val categoryList: MutableList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // ViewHolder sınıfı, her bir öğe için layout'u tutar
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.category_name)

        // Kategori tıklama işlemi
        init {
            itemView.setOnClickListener {
                val category = categoryList[adapterPosition]
                category.isSelected = !category.isSelected // Seçimi tersine çevir
                notifyItemChanged(adapterPosition) // Göstergeleri yenile
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // item_category.xml dosyasını inflater ile bağla
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        // Veriyi, uygun ViewHolder'a bağla
        val category = categoryList[position]
        holder.categoryName.text = category.name

        // Seçilen kategoriyi görselleştir
        if (category.isSelected) {
            holder.categoryName.setBackgroundColor(holder.itemView.context.getColor(R.color.green)) // Seçilen renk
        } else {
            holder.categoryName.setBackgroundColor(holder.itemView.context.getColor(android.R.color.transparent)) // Normal renk
        }
    }

    override fun getItemCount(): Int {
        // Liste eleman sayısını döndür
        return categoryList.size
    }
}
