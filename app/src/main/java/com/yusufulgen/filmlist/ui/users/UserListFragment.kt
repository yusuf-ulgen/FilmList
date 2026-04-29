package com.yusufulgen.filmlist.ui.users

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
import com.yusufulgen.filmlist.R
import com.yusufulgen.filmlist.data.local.*
import com.yusufulgen.filmlist.databinding.FragmentUserListBinding
import com.yusufulgen.filmlist.util.RepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserListFragment : Fragment() {
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserListViewModel
    private lateinit var listsAdapter: UserListsAdapter
    private var hasRedirected = false
    
    private var cameraImageUri: Uri? = null
    private var selectedListForImage: UserList? = null

    // Galeriden seçim sonucu
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val persistedUri = copyImageToInternal(it)
            if (persistedUri != null && selectedListForImage != null) {
                viewModel.updateListImage(selectedListForImage!!, persistedUri.toString())
            }
        }
    }

    // Kameradan çekim sonucu
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && cameraImageUri != null && selectedListForImage != null) {
            viewModel.updateListImage(selectedListForImage!!, cameraImageUri.toString())
        }
    }

    // Kamera izni sonucu
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Kamera izni gerekli.", Toast.LENGTH_SHORT).show()
        }
    }

    // Galeri izni sonucu
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(requireContext(), "Depolama izni gerekli.", Toast.LENGTH_SHORT).show()
        }
    }

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
            },
            onIconClick = { list ->
                selectedListForImage = list
                showImagePickerDialog()
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

    private fun showImagePickerDialog() {
        val options = arrayOf("📷 Kamera", "🖼️ Galeri")
        AlertDialog.Builder(requireContext())
            .setTitle("Liste Fotoğrafı Seç")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> checkStoragePermissionAndOpen()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun checkStoragePermissionAndOpen() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        } else {
            storagePermissionLauncher.launch(permission)
        }
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        cameraLauncher.launch(cameraImageUri)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().cacheDir
        return File.createTempFile("LIST_${timeStamp}_", ".jpg", storageDir)
    }

    private fun copyImageToInternal(sourceUri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(sourceUri) ?: return null
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(requireContext().filesDir, "list_${timeStamp}.jpg")
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
