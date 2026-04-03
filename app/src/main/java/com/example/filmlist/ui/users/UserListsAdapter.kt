package com.example.filmlist.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.filmlist.R
import com.example.filmlist.data.local.UserList
import com.example.filmlist.databinding.ItemUserListBinding

class UserListsAdapter(
    private val onListClick: (UserList) -> Unit
) : RecyclerView.Adapter<UserListsAdapter.ViewHolder>() {
    private var lists: List<UserList> = emptyList()
    private var selectedId: Long = -1

    fun setLists(newLists: List<UserList>) {
        lists = newLists
        if (selectedId == -1L && lists.isNotEmpty()) {
            selectedId = lists.first().id
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onListClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = lists[position]
        holder.bind(list, list.id == selectedId)
        
        holder.itemView.setOnClickListener {
            selectedId = list.id
            notifyDataSetChanged()
            onListClick(list)
        }
    }

    override fun getItemCount(): Int = lists.size

    class ViewHolder(
        private val binding: ItemUserListBinding,
        private val onListClick: (UserList) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: UserList, isSelected: Boolean) {
            binding.listNameText.text = list.name
            binding.root.setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (isSelected) R.color.premium_gold else R.color.secondary
                )
            )
        }
    }
}
