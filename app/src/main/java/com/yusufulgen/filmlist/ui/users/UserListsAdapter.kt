package com.yusufulgen.filmlist.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yusufulgen.filmlist.data.local.UserList
import com.yusufulgen.filmlist.databinding.ItemUserListBinding

import coil.load
import coil.transform.CircleCropTransformation
import com.yusufulgen.filmlist.R

class UserListsAdapter(
    private val onListClick: (UserList) -> Unit,
    private val onListLongClick: (UserList) -> Unit,
    private val onIconClick: (UserList) -> Unit
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

        holder.binding.listIcon.setOnClickListener {
            onIconClick(list)
        }
    }

    override fun getItemCount(): Int = lists.size

    class ViewHolder(
        val binding: ItemUserListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: UserList) {
            binding.listNameText.text = list.name
            
            if (!list.imageUrl.isNullOrEmpty()) {
                binding.listIcon.load(list.imageUrl) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                    error(R.drawable.ic_list_premium)
                }
                binding.listIcon.imageTintList = null
                binding.listIcon.background = null
                binding.listIcon.setPadding(0, 0, 0, 0)
                binding.listIcon.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            } else {
                binding.listIcon.load(R.drawable.ic_list_premium) {
                    crossfade(true)
                }
                binding.listIcon.imageTintList = android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(binding.root.context, R.color.white)
                )
                binding.listIcon.setBackgroundResource(R.drawable.bg_circle_outline)
                binding.listIcon.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(binding.root.context, R.color.secondary)
                )
                val padding = (12 * binding.root.context.resources.displayMetrics.density).toInt()
                binding.listIcon.setPadding(padding, padding, padding, padding)
                binding.listIcon.scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
            }
        }
    }
}
