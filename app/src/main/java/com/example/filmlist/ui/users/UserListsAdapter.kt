package com.example.filmlist.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filmlist.data.local.UserList
import com.example.filmlist.databinding.ItemUserListBinding

class UserListsAdapter(
    private val onListClick: (UserList) -> Unit,
    private val onListLongClick: (UserList) -> Unit
) : RecyclerView.Adapter<UserListsAdapter.ViewHolder>() {
    private var lists: List<UserList> = emptyList()

    fun setLists(newLists: List<UserList>) {
        lists = newLists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = lists[position]
        holder.bind(list)
        
        holder.itemView.setOnClickListener {
            onListClick(list)
        }

        holder.itemView.setOnLongClickListener {
            onListLongClick(list)
            true
        }
    }

    override fun getItemCount(): Int = lists.size

    class ViewHolder(
        private val binding: ItemUserListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: UserList) {
            binding.listNameText.text = list.name
        }
    }
}
