package com.example.filmlist.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.filmlist.databinding.FragmentAddContentBinding
import com.example.filmlist.util.RepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddContentFragment : Fragment() {
    private var _binding: FragmentAddContentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddContentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupObservers()

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val type = if (binding.radioFilm.isChecked) "FILM" else "SHOW"
            val rating = binding.ratingBar.rating.toInt()
            val comment = binding.commentEditText.text.toString()
            
            val selectedList = viewModel.userLists.value.getOrNull(binding.listSpinner.selectedItemPosition)

            if (title.isNotBlank() && selectedList != null) {
                viewModel.saveMediaContent(title, type, rating, comment, selectedList.id)
            } else if (selectedList == null) {
                Toast.makeText(requireContext(), "Henüz bir listeniz yok. Lütfen 'Listeler' sekmesinden bir liste oluşturun.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Lütfen bir başlık girin.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewModel() {
        val factory = RepositoryProvider.provideViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[AddContentViewModel::class.java]
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userLists.collectLatest { lists ->
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lists.map { it.name })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.listSpinner.adapter = adapter
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contentSaved.collectLatest { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Başarıyla listenize eklendi!", Toast.LENGTH_SHORT).show()
                    binding.titleEditText.text?.clear()
                    binding.commentEditText.text?.clear()
                    binding.ratingBar.rating = 0f
                }
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
