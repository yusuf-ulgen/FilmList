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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmlist.R
import com.example.filmlist.data.local.*
import com.example.filmlist.databinding.FragmentUserListBinding
import com.example.filmlist.util.RepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserListFragment : Fragment() {
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserListViewModel
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
        setupViewModel()
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        listsAdapter = UserListsAdapter(
            onListClick = { list ->
                // Navigasyon: Liste ismine tıklandığında detay sayfasına git
                val bundle = Bundle().apply {
                    putLong("listId", list.id)
                    putString("listName", list.name)
                }
                findNavController().navigate(R.id.action_navigation_list_to_listDetailFragment, bundle)
            },
            onListLongClick = { list ->
                showListActions(list)
            }
        )
        binding.listsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.listsRecyclerView.adapter = listsAdapter

        binding.addListFab.setOnClickListener {
            showCreateListDialog()
        }
    }

    private fun setupViewModel() {
        val factory = RepositoryProvider.provideViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[UserListViewModel::class.java]
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userLists.collectLatest { lists ->
                listsAdapter.setLists(lists)
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

    private fun showListActions(userList: UserList) {
        val options = arrayOf("Düzenle", "Sil")
        AlertDialog.Builder(requireContext())
            .setTitle(userList.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditListDialog(userList)
                    1 -> showDeleteListConfirm(userList)
                }
            }
            .show()
    }

    private fun showEditListDialog(userList: UserList) {
        val editText = EditText(requireContext()).apply {
            setText(userList.name)
            inputType = InputType.TYPE_CLASS_TEXT
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Listeyi Düzenle")
            .setView(editText)
            .setPositiveButton("Kaydet") { _, _ ->
                val newName = editText.text.toString()
                if (newName.isNotBlank() && newName != userList.name) {
                    viewModel.updateList(userList, newName)
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun showDeleteListConfirm(userList: UserList) {
        AlertDialog.Builder(requireContext())
            .setTitle("Listeyi Sil")
            .setMessage("${userList.name} listesini ve içindeki tüm içerikleri silmek istediğinize emin misiniz?")
            .setPositiveButton("Sil") { _, _ ->
                viewModel.deleteList(userList)
                Toast.makeText(requireContext(), "Liste silindi.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
