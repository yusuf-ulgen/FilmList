package com.yusufulgen.filmlist.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yusufulgen.filmlist.databinding.FragmentAiChatBinding
import com.yusufulgen.filmlist.util.RepositoryProvider
import com.yusufulgen.filmlist.util.TutorialManager
import com.yusufulgen.filmlist.util.TutorialStep
import com.yusufulgen.filmlist.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import android.view.ViewGroup.MarginLayoutParams

class AiChatFragment : Fragment() {
    private var _binding: FragmentAiChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatViewModel
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModel()
        setupObservers()
        showTutorial()
    }

    private fun showTutorial() {
        val steps = listOf(
            TutorialStep(null, "Yapay Zeka Uzmanı 🤖", "Sana en uygun film ve dizi önerilerini sunmak için buradayım!"),
            TutorialStep(R.id.messageEditText, "Soru Sor ✍️", "İstediğin tarzda filmleri buraya yazabilirsin. Örneğin: 'Bana aksiyon dolu bilim kurgu filmleri öner'"),
            TutorialStep(R.id.sendButton, "Gönder 🚀", "Mesajını yazdıktan sonra bu butona basarak bana ulaştırabilirsin.")
        )
        TutorialManager(requireActivity()).showTutorial("ai_tutorial", steps)
    }

    private fun setupUI() {
        adapter = ChatAdapter()
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = adapter

        // Handle Keyboard insets for the input area
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            binding.inputArea.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = if (imeInsets.bottom > 0) {
                    // 1. imeInsets.bottom: Keyboard top from screen bottom
                    // 2. systemBars.bottom: System navigation height
                    // 3. 60dp: Our BottomNavigationView height
                    // We subtract 2 and 3 because the fragment container already starts above them.
                        
                    val bottomNavHeightPx = (60 * resources.displayMetrics.density).toInt()
                    val keyboardHeightAboveFragmentBottom = imeInsets.bottom - systemBars.bottom - bottomNavHeightPx
                    
                    // Final tiny gap for "bitişik" (adjacent) look
                    val tinyGap = (0.5 * resources.displayMetrics.density).toInt()
                    maxOf(tinyGap, keyboardHeightAboveFragmentBottom + tinyGap)
                } else {
                    // When keyboard is closed, standard margin from the nav bar
                    (16 * resources.displayMetrics.density).toInt()
                }
            }
            windowInsets
        }

        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            if (message.isNotBlank()) {
                viewModel.sendMessage(message)
                binding.messageEditText.text.clear()
            }
        }
    }

    private fun setupViewModel() {
        val factory = RepositoryProvider.provideViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.flow.combine(viewModel.messages, viewModel.isLoading) { messages, isLoading ->
                if (isLoading) {
                    messages + ChatMessage("", false, isTyping = true)
                } else {
                    messages
                }
            }.collectLatest { finalMessages ->
                adapter.setMessages(finalMessages)
                if (finalMessages.isNotEmpty()) {
                    binding.chatRecyclerView.post {
                        binding.chatRecyclerView.smoothScrollToPosition(finalMessages.size - 1)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.sendButton.isEnabled = !isLoading
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
