package com.example.filmlist.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
        
        // Simple layout logic for user vs model
        val params = holder.binding.messageCard.layoutParams as ViewGroup.MarginLayoutParams
        if (message.isUser) {
            params.marginStart = 100
            params.marginEnd = 0
            holder.binding.messageCard.setCardBackgroundColor(0xFF03DAC6.toInt()) // Teal
        } else {
            params.marginStart = 0
            params.marginEnd = 100
            holder.binding.messageCard.setCardBackgroundColor(0x33FFFFFF.toInt()) // Translucent white
        }
        holder.binding.messageCard.layoutParams = params
    }

    override fun getItemCount() = messages.size
}
