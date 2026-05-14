package com.yusufulgen.filmlist.ui.profile

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
import com.yusufulgen.filmlist.AuthLandingActivity
import com.yusufulgen.filmlist.R
import com.yusufulgen.filmlist.databinding.FragmentProfileBinding
import com.yusufulgen.filmlist.util.RepositoryProvider
import com.yusufulgen.filmlist.util.TutorialManager
import com.yusufulgen.filmlist.util.TutorialStep
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel
    private lateinit var adapter: ProfileGridAdapter

    private var cameraImageUri: Uri? = null

    // Galeriden seçim sonucu
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val persistedUri = copyImageToInternal(it)
            if (persistedUri != null) {
                viewModel.saveProfileImage(persistedUri.toString())
            }
        }
    }

    // Kameradan çekim sonucu
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && cameraImageUri != null) {
            viewModel.saveProfileImage(cameraImageUri.toString())
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
        setupUI()
        setupViewModel()
        setupObservers()
        showTutorial()
    }

    private fun showTutorial() {
        val steps = listOf(
            TutorialStep(R.id.watchedGridRecyclerView, "Profil Özeti 👤", "İzleme istatistiklerini ve son izlediğin içerikleri buradan takip edebilirsin."),
            TutorialStep(R.id.settingsButtonMain, "Ayarlar ⚙️", "Hesap çıkışı yapmak veya favori türlerini düzenlemek için ayarlara göz at.")
        )
        TutorialManager(requireActivity()).showTutorial("profile_tutorial", steps)
    }

    private fun setupUI() {
        adapter = ProfileGridAdapter(
            onProfileImageClick = { showImagePickerDialog() }
        )
        
        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(), 3)
        layoutManager.spanSizeLookup = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 3 else 1
            }
        }
        
        binding.watchedGridRecyclerView.layoutManager = layoutManager
        binding.watchedGridRecyclerView.adapter = adapter
    }

    fun showSettingsMenuFromActivity(view: View) {
        val bottomSheet = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext(), R.style.CustomDialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_profile_settings, null)
        bottomSheet.setContentView(dialogView)

        dialogView.findViewById<View>(R.id.editCategoriesAction).setOnClickListener {
            bottomSheet.dismiss()
            val intent = Intent(requireContext(), com.yusufulgen.filmlist.ui.categories.CategoriesActivity::class.java)
            startActivity(intent)
        }

        dialogView.findViewById<View>(R.id.otherAppsAction).setOnClickListener {
            bottomSheet.dismiss()
            val developerName = "Yusuf Ulgen"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:$developerName")))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Yusuf+Ulgen")))
            }
        }

        dialogView.findViewById<View>(R.id.logoutAction).setOnClickListener {
            bottomSheet.dismiss()
            showLogoutConfirmation()
        }

        bottomSheet.show()
    }

    private fun showLogoutConfirmation() {
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

    private fun setupViewModel() {
        val factory = RepositoryProvider.provideViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Kombine gözlemleyici: Herhangi bir veri değiştiğinde adaptörü güncelle
            combine(
                viewModel.stats,
                viewModel.username,
                viewModel.profileImageUri,
                viewModel.watchedContent
            ) { stats, username, profileUri, watchedItems ->
                adapter.updateData(stats, username, profileUri, watchedItems)
            }.collect { }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

