package com.example.filmlist.ui.users

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmlist.R
import com.example.filmlist.data.local.*
import com.example.filmlist.databinding.BottomSheetItemActionBinding
import com.example.filmlist.databinding.FragmentUserListBinding
import com.example.filmlist.util.RepositoryProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserListFragment : Fragment() {
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserListViewModel
    private lateinit var contentAdapter: MediaContentAdapter
    private lateinit var listsAdapter: UserListsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModel()
        setupObservers()
    }

    private fun setupUI() {
        // Categories Adapter
        listsAdapter = UserListsAdapter { list ->
            viewModel.loadListContent(list.id)
        }
        binding.listsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.listsRecyclerView.adapter = listsAdapter

        // Content Adapter
        contentAdapter = MediaContentAdapter { content ->
            showItemActions(content)
        }
        binding.userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.userRecyclerView.adapter = contentAdapter

        binding.addListFab.setOnClickListener {
            showCreateListDialog()
        }
        
        setupDragAndDrop()
    }

    private fun setupViewModel() {
        val factory = RepositoryProvider.provideViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[UserListViewModel::class.java]
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userLists.collectLatest { lists ->
                listsAdapter.setLists(lists)
                if (lists.isEmpty()) {
                    binding.userRecyclerView.visibility = View.GONE
                } else {
                    binding.userRecyclerView.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedListContent.collectLatest { content ->
                contentAdapter.setItems(content)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showCreateListDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "Liste Adı"
            inputType = InputType.TYPE_CLASS_TEXT
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("Yeni Liste Oluştur")
            .setView(editText)
            .setPositiveButton("Oluştur") { _, _ ->
                val name = editText.text.toString()
                if (name.isNotBlank()) {
                    viewModel.createList(name)
                }
            }
            .setNegativeButton("İptal", null)
            .show()
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
                .setTitle("Emin misiniz?")
                .setMessage("Bu içeriği listenizden tamamen silecek misiniz?")
                .setPositiveButton("Evet, Sil") { _, _ ->
                    viewModel.deleteItem(content)
                    Toast.makeText(requireContext(), "Silindi.", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "${target.name} listesine taşındı.", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun setupDragAndDrop() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.START or ItemTouchHelper.END, 0) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                // Drag Lists logic
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(binding.listsRecyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
