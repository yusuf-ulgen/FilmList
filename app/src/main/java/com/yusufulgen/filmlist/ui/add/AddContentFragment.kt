package com.yusufulgen.filmlist.ui.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.yusufulgen.filmlist.databinding.FragmentAddContentBinding
import com.yusufulgen.filmlist.util.RepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddContentFragment : Fragment() {
    private var _binding: FragmentAddContentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddContentViewModel
    private var searchAdapter: ArrayAdapter<String>? = null
    private var searchResultsList: List<com.yusufulgen.filmlist.data.remote.Movie> = emptyList()
    private var selectedType: String = "FILM"
    private var selectedPosterPath: String? = null
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

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
        setupDefaultDates()
    }

    private fun setupDefaultDates() {
        val today = dateFormatter.format(Calendar.getInstance().time)
        binding.watchDateEditText.setText(today)
        binding.startDateEditText.setText(today)
        binding.endDateEditText.setText(today)
    }

    private fun setupListeners() {
        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val rating = binding.ratingBar.rating.toInt()
            val comment = binding.commentEditText.text.toString().trim()

            val lists = viewModel.userLists.value
            val selectedText = binding.listSpinnerAutoComplete.text.toString()
            val selectedList = lists.find { it.name == selectedText } ?: lists.firstOrNull()

            if (lists.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen önce bir liste oluşturun.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (title.isBlank()) {
                Toast.makeText(requireContext(), "Lütfen bir başlık girin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedList == null) {
                Toast.makeText(requireContext(), "Lütfen bir liste seçin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val date1 = if (selectedType == "FILM") binding.watchDateEditText.text.toString() else binding.startDateEditText.text.toString()
            val date2 = if (selectedType == "SHOW") binding.endDateEditText.text.toString() else null

            viewModel.saveMediaContent(title, selectedType, rating, comment.ifBlank { null }, selectedList.id, selectedPosterPath, date1, date2)
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

        binding.watchDateEditText.setOnClickListener { showDatePicker(binding.watchDateEditText) }
        binding.startDateEditText.setOnClickListener { showDatePicker(binding.startDateEditText) }
        binding.endDateEditText.setOnClickListener { showDatePicker(binding.endDateEditText) }

        (binding.titleEditText as? AutoCompleteTextView)?.setOnItemClickListener { parent: AdapterView<*>, _: View, position: Int, _: Long ->
            val selectedTitle = parent.getItemAtPosition(position) as String
            val selectedMovie = searchResultsList.find { it.title.equals(selectedTitle, ignoreCase = true) }
            
            selectedMovie?.let {
                selectedType = if (it.mediaType == "tv" || it.tvName != null) "SHOW" else "FILM"
                selectedPosterPath = it.posterPath
                
                // Görünürlük ayarları
                if (selectedType == "FILM") {
                    binding.watchDateLayout.visibility = View.VISIBLE
                    binding.showDatesLayout.visibility = View.GONE
                } else {
                    binding.watchDateLayout.visibility = View.GONE
                    binding.showDatesLayout.visibility = View.VISIBLE
                }

                // Afiş önizleme kaldırıldı (istek üzerine), sadece path tutuluyor
                selectedPosterPath?.let { path ->
                    // binding.posterPreview.visibility = View.VISIBLE
                    // binding.posterPreview.load(...)
                }

                Toast.makeText(requireContext(), "$selectedTitle seçildi ($selectedType)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, y, m, d ->
            calendar.set(y, m, d)
            editText.setText(dateFormatter.format(calendar.time))
        }, year, month, day).show()
    }

    private fun setupViewModel() {
        val factory = RepositoryProvider.provideViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[AddContentViewModel::class.java]
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userLists.collectLatest { lists ->
                val listNames = lists.map { it.name }
                
                // Filtreleme yapmayan özel bir adapter oluşturuyoruz
                val adapter = object : ArrayAdapter<String>(requireContext(), com.yusufulgen.filmlist.R.layout.item_dropdown_list, listNames) {
                    override fun getFilter(): android.widget.Filter {
                        return object : android.widget.Filter() {
                            override fun performFiltering(constraint: CharSequence?): FilterResults {
                                val results = FilterResults()
                                results.values = listNames
                                results.count = listNames.size
                                return results
                            }
                            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
                
                binding.listSpinnerAutoComplete.setAdapter(adapter)
                
                if (listNames.isNotEmpty() && binding.listSpinnerAutoComplete.text.isNullOrBlank()) {
                    binding.listSpinnerAutoComplete.setText(listNames.first(), false)
                }

                // Tıklayınca her zaman dropdown'ı göster
                binding.listSpinnerAutoComplete.setOnClickListener {
                    binding.listSpinnerAutoComplete.showDropDown()
                }
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
                    binding.watchDateLayout.visibility = View.VISIBLE
                    binding.showDatesLayout.visibility = View.GONE
                    setupDefaultDates()
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
