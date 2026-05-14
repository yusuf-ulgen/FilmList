package com.yusufulgen.filmlist.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.text.Html
import androidx.core.text.HtmlCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yusufulgen.filmlist.R
import com.yusufulgen.filmlist.databinding.ItemChatMessageBinding
import com.yusufulgen.filmlist.databinding.ItemChatTypingBinding
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import android.content.res.ColorStateList
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_BOT = 1
        private const val TYPE_TYPING = 2
    }

    private val messages = mutableListOf<ChatMessage>()

    fun setMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root)
    inner class TypingViewHolder(val binding: ItemChatTypingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            message.isTyping -> TYPE_TYPING
            message.isUser -> TYPE_USER
            else -> TYPE_BOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TYPING -> {
                val binding = ItemChatTypingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TypingViewHolder(binding)
            }
            else -> {
                val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChatViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        
        if (holder is ChatViewHolder) {
            val formattedText = message.text
                .replace("\n", "<br/>")
                .replace(Regex("\\*\\*(.*?)\\*\\*"), "<b>$1</b>")
            holder.binding.messageText.text = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            
            val params = holder.binding.messageBubble.layoutParams as ConstraintLayout.LayoutParams
            val context = holder.itemView.context
            val density = context.resources.displayMetrics.density
            
            if (message.isUser) {
                params.marginStart = (64 * density).toInt()
                params.marginEnd = (16 * density).toInt()
                params.horizontalBias = 1.0f
                holder.binding.messageBubble.setBackgroundResource(R.drawable.bg_message_user)
                holder.binding.messageText.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                params.marginStart = (16 * density).toInt()
                params.marginEnd = (64 * density).toInt()
                params.horizontalBias = 0.0f
                holder.binding.messageBubble.setBackgroundResource(R.drawable.bg_message_bot)
                holder.binding.messageText.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            
            holder.binding.messageBubble.clipToOutline = true
            holder.binding.messageBubble.layoutParams = params
        } else if (holder is TypingViewHolder) {
            holder.binding.typingBubble.setBackgroundResource(R.drawable.bg_message_user)
            holder.binding.typingBubble.clipToOutline = true
            startTypingAnimation(holder)
        }
    }

    private fun startTypingAnimation(holder: TypingViewHolder) {
        val dots = listOf(holder.binding.dot1, holder.binding.dot2, holder.binding.dot3)
        dots.forEachIndexed { index, view ->
            view.clearAnimation()
            val anim = AlphaAnimation(0.2f, 1.0f).apply {
                duration = 400
                repeatMode = Animation.REVERSE
                repeatCount = Animation.INFINITE
                startOffset = (index * 200).toLong()
            }
            view.startAnimation(anim)
        }
    }

    override fun getItemCount() = messages.size
}
