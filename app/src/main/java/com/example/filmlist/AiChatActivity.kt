package com.example.filmlist

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.filmlist.data.repository.ChatRepository
import com.example.filmlist.databinding.ActivityAiChatBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import com.example.filmlist.ui.chat.ChatAdapter
import com.example.filmlist.ui.chat.ChatViewModel

class AiChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAiChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAiChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}