package com.example.filmlist.ui.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
    private var searchAdapter: ArrayAdapter<String>? = null
    private var searchResultsList: List<com.example.filmlist.data.remote.Movie> = emptyList()
    private var selectedType: String = "FILM"
    private var selectedPosterPath: String? = null

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
        setupListeners()
    }

    private fun setupListeners() {
        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val rating = binding.ratingBar.rating.toInt()
            val comment = binding.commentEditText.text.toString().trim()

            val lists = viewModel.userLists.value
            val selectedPosition = binding.listSpinner.selectedItemPosition

            if (lists.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen önce bir liste oluşturun.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (title.isBlank()) {
                Toast.makeText(requireContext(), "Lütfen bir başlık girin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedListId = if (selectedPosition >= 0 && selectedPosition < lists.size) {
                lists[selectedPosition].id
            } else {
                lists.firstOrNull()?.id ?: -1L
            }

            viewModel.saveMediaContent(title, selectedType, rating, comment.ifBlank { null }, selectedListId, selectedPosterPath)
        }

        binding.titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length >= 2) {
                    viewModel.searchMovies(query)
                } else if (query.isEmpty()) {
                    selectedPosterPath = null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        (binding.titleEditText as? AutoCompleteTextView)?.setOnItemClickListener { parent: AdapterView<*>, _: View, position: Int, _: Long ->
            val selectedTitle = parent.getItemAtPosition(position) as String
            val selectedMovie = searchResultsList.find { it.title == selectedTitle }
            
            selectedMovie?.let {
                selectedType = if (it.mediaType == "tv" || it.tvName != null) "SHOW" else "FILM"
                selectedPosterPath = it.posterPath
                Toast.makeText(requireContext(), "İçerik türü otomatik belirlendi: $selectedType", Toast.LENGTH_SHORT).show()
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
                val listNames = lists.map { it.name }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.listSpinner.adapter = adapter
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collectLatest { results ->
                searchResultsList = results
                val titles = results.map { it.title }
                searchAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, titles)
                (binding.titleEditText as? AutoCompleteTextView)?.setAdapter(searchAdapter)
                searchAdapter?.notifyDataSetChanged()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contentSaved.collectLatest { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Başarıyla eklendi!", Toast.LENGTH_SHORT).show()
                    binding.titleEditText.text?.clear()
                    binding.commentEditText.text?.clear()
                    binding.ratingBar.rating = 0f
                    selectedType = "FILM" // Reset to default
                    selectedPosterPath = null // Reset
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
