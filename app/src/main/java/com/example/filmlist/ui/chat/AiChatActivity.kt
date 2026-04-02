package com.example.filmlist.ui.chat

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmlist.data.repository.ChatRepository
import com.example.filmlist.databinding.ActivityAiChatBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AiChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAiChatBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        enableEdgeToEdge()
        supportActionBar?.hide()

        setupUI()
        setupViewModel()
        setupObservers()
    }

    private fun setupUI() {
        adapter = ChatAdapter()
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = adapter

        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            if (message.isNotBlank()) {
                viewModel.sendMessage(message)
                binding.messageEditText.text.clear()
            }
        }
    }

    private fun setupViewModel() {
        val repository = ChatRepository()
        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ChatViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.messages.collectLatest { messages ->
                adapter.setMessages(messages)
                if (messages.isNotEmpty()) {
                    binding.chatRecyclerView.scrollToPosition(messages.size - 1)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                Toast.makeText(this@AiChatActivity, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
