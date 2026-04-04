package com.example.filmlist.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.filmlist.R
import com.example.filmlist.databinding.ItemChatMessageBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    fun setMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.binding.messageText.text = message.text
        
        val params = holder.binding.messageCard.layoutParams as ViewGroup.MarginLayoutParams
        val context = holder.itemView.context
        
        if (message.isUser) {
            params.marginStart = 64
            params.marginEnd = 0
            holder.binding.messageCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.secondary))
            holder.binding.messageText.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            params.marginStart = 0
            params.marginEnd = 64
            holder.binding.messageCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface))
            holder.binding.messageText.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
        holder.binding.messageCard.layoutParams = params
    }

    override fun getItemCount() = messages.size
}
