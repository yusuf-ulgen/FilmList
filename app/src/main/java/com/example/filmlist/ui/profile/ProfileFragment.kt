package com.example.filmlist.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.filmlist.AuthLandingActivity
import com.example.filmlist.R
import com.example.filmlist.databinding.FragmentProfileBinding
import com.example.filmlist.util.RepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    private var cameraImageUri: Uri? = null

    // Galeriden seçim sonucu
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // URI'yi kalıcı olarak saklamak için kopyala
            val persistedUri = copyImageToInternal(it)
            if (persistedUri != null) {
                viewModel.saveProfileImage(persistedUri.toString())
                loadProfileImage(persistedUri.toString())
            }
        }
    }

    // Kameradan çekim sonucu
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && cameraImageUri != null) {
            viewModel.saveProfileImage(cameraImageUri.toString())
            loadProfileImage(cameraImageUri.toString())
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupObservers()

        // Profil resmine tıklama
        binding.profileImage.setOnClickListener {
            showImagePickerDialog()
        }

        binding.logoutButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Çıkış Yap")
                .setMessage("Hesabınızdan çıkış yapmak istediğinizden emin misiniz?")
                .setPositiveButton("Evet, Çıkış Yap") { _, _ ->
                    viewModel.logout()
                    val intent = Intent(requireActivity(), AuthLandingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("İptal", null)
                .show()
        }

        binding.editPreferencesButton.setOnClickListener {
            val intent = Intent(requireContext(), com.example.filmlist.ui.categories.CategoriesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("📷 Kamera", "🖼️ Galeri")
        AlertDialog.Builder(requireContext())
            .setTitle("Profil Fotoğrafı Seç")
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
        return File.createTempFile("PROFILE_${timeStamp}_", ".jpg", storageDir)
    }

    private fun copyImageToInternal(sourceUri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(sourceUri) ?: return null
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(requireContext().filesDir, "profile_${timeStamp}.jpg")
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

    private fun loadProfileImage(uriString: String) {
        binding.profileImage.load(Uri.parse(uriString)) {
            crossfade(true)
            transformations(CircleCropTransformation())
            error(R.drawable.ic_profile)
        }
        // Tint'i kaldır çünkü artık gerçek resim var
        binding.profileImage.imageTintList = null
        binding.profileImage.background = null
        binding.profileImage.setPadding(0, 0, 0, 0)
        binding.profileImage.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
    }

    private fun setupViewModel() {
        val factory = RepositoryProvider.provideViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.username.collectLatest { name ->
                binding.usernameText.text = name ?: "Kullanıcı"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stats.collectLatest { stats ->
                stats?.let {
                    binding.totalWatchedText.text = it.totalWatched.toString()
                    binding.favoriteGenreText.text = it.favoriteGenre
                    binding.showHabitText.text = it.showHabit
                }
            }
        }

        // Kaydedilmiş profil resmini yükle
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileImageUri.collectLatest { uriString ->
                if (!uriString.isNullOrEmpty()) {
                    loadProfileImage(uriString)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
