package com.example.filmlist.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmlist.data.local.MediaContent
import com.example.filmlist.databinding.BottomSheetItemActionBinding
import com.example.filmlist.databinding.FragmentListDetailBinding
import com.example.filmlist.util.RepositoryProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ListDetailFragment : Fragment() {
    private var _binding: FragmentListDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserListViewModel
    private lateinit var contentAdapter: MediaContentAdapter
    private var listId: Long = -1L
    private var listName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        arguments?.let {
            listId = it.getLong("listId")
            listName = it.getString("listName") ?: ""
        }

        setupViewModel()
        setupUI()
        setupObservers()
        
        viewModel.loadListContent(listId)
    }

    private fun setupUI() {
        contentAdapter = MediaContentAdapter { content ->
            showItemActions(content)
        }
        binding.detailRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.detailRecyclerView.adapter = contentAdapter
    }

    private fun setupViewModel() {
        val factory = RepositoryProvider.provideViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[UserListViewModel::class.java]
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedListContent.collectLatest { content ->
                contentAdapter.setItems(content)
                binding.emptyText.visibility = if (content.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showItemActions(content: MediaContent) {
        val dialog = BottomSheetDialog(requireContext())
        val actionBinding = BottomSheetItemActionBinding.inflate(layoutInflater)
        dialog.setContentView(actionBinding.root)

        actionBinding.moveButton.setOnClickListener {
            showMoveToListMenu(content)
            dialog.dismiss()
        }

        actionBinding.deleteButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("İçeriği Sil")
                .setMessage("Bu içeriği listenizden silmek istediğinize emin misiniz?")
                .setPositiveButton("Evet, Sil") { _, _ ->
                    viewModel.deleteItem(content)
                    viewModel.loadListContent(listId) // Refresh
                }
                .setNegativeButton("İptal", null)
                .show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showMoveToListMenu(content: MediaContent) {
        val lists = viewModel.userLists.value
        val names = lists.map { it.name }.toTypedArray()
        
        AlertDialog.Builder(requireContext())
            .setTitle("Hedef Liste Seçin")
            .setItems(names) { _, which ->
                val target = lists[which]
                viewModel.moveItem(content, target.id)
                viewModel.loadListContent(listId) // Refresh
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
