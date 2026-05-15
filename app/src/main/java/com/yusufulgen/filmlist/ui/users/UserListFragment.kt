package com.yusufulgen.filmlist.ui.users

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.yusufulgen.filmlist.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yusufulgen.filmlist.R
import com.yusufulgen.filmlist.data.local.*
import com.yusufulgen.filmlist.databinding.FragmentUserListBinding
import com.yusufulgen.filmlist.util.RepositoryProvider
import com.yusufulgen.filmlist.util.TutorialManager
import com.yusufulgen.filmlist.util.TutorialStep
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
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

    // Galeriden seçim sonucu (Yeni Photo Picker - İzin Gerekmez)
    private val pickMediaLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
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
        showTutorial()
    }

    private fun showTutorial() {
        val steps = listOf(
            TutorialStep(R.id.listsRecyclerView, "Listelerin 📂", "Oluşturduğun tüm film ve dizi listelerini burada görebilirsin. Üzerine tıklayarak detaylara ulaşabilirsin."),
            TutorialStep(R.id.addListFab, "Yeni Liste ➕", "Yeni bir kategori veya özel liste oluşturmak için bu butonu kullan.")
        )
        TutorialManager(requireActivity()).showTutorial("list_tutorial", steps)
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
                MainActivity.showNotification(this@UserListFragment, error, true)
            }
        }
    }

    private fun showCreateListDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_list, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(dialogView)
            .create()

        dialogView.findViewById<MaterialButton>(R.id.createButton).setOnClickListener {
            val name = dialogView.findViewById<TextInputEditText>(R.id.listNameEditText).text.toString()
            if (name.isNotBlank()) {
                viewModel.createList(name)
                dialog.dismiss()
                MainActivity.showNotification(this, "Liste oluşturuldu: $name")
            } else {
                MainActivity.showNotification(this, "Lütfen bir isim girin", true)
            }
        }

        dialogView.findViewById<MaterialButton>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        applyDialogWidth(dialog)
    }

    private fun showListActions(userList: UserList) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_list_actions, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(dialogView)
            .create()

        dialogView.findViewById<android.widget.TextView>(R.id.dialogTitle).text = userList.name

        dialogView.findViewById<View>(R.id.editAction).setOnClickListener {
            dialog.dismiss()
            showEditListDialog(userList)
        }

        dialogView.findViewById<View>(R.id.deleteAction).setOnClickListener {
            dialog.dismiss()
            showDeleteListConfirm(userList)
        }

        dialog.show()
        applyDialogWidth(dialog)
    }

    private fun showEditListDialog(userList: UserList) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_list, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(dialogView)
            .create()

        dialogView.findViewById<android.widget.TextView>(R.id.dialogTitle)?.text = "Listeyi Düzenle"
        
        val editText = dialogView.findViewById<TextInputEditText>(R.id.listNameEditText)
        editText.setText(userList.name)
        
        val createButton = dialogView.findViewById<MaterialButton>(R.id.createButton)
        createButton.text = "Kaydet"
        createButton.setOnClickListener {
            val newName = editText.text.toString()
            if (newName.isNotBlank() && newName != userList.name) {
                viewModel.updateList(userList, newName)
                dialog.dismiss()
                MainActivity.showNotification(this, "Liste güncellendi")
            } else {
                dialog.dismiss()
            }
        }

        dialogView.findViewById<MaterialButton>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        applyDialogWidth(dialog)
    }

    private fun showDeleteListConfirm(userList: UserList) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(dialogView)
            .create()

        dialogView.findViewById<android.widget.TextView>(R.id.confirmMessage).text = 
            "${userList.name} listesini silmek istediğinizden emin misiniz?"

        dialogView.findViewById<MaterialButton>(R.id.confirmButton).setOnClickListener {
            viewModel.deleteList(userList)
            dialog.dismiss()
            MainActivity.showNotification(this, "Liste silindi")
        }

        dialogView.findViewById<MaterialButton>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        applyDialogWidth(dialog)
    }

    private fun applyDialogWidth(dialog: AlertDialog) {
        dialog.window?.setLayout(
            (320 * resources.displayMetrics.density).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("📷 Kamera", "🖼️ Galeri")
        AlertDialog.Builder(requireContext())
            .setTitle("Liste Fotoğrafı Seç")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> openGallery()
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
        pickMediaLauncher.launch(
            androidx.activity.result.PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
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
