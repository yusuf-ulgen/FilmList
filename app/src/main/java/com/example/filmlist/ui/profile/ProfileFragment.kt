package com.example.filmlist.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.filmlist.AuthLandingActivity
import com.example.filmlist.databinding.FragmentProfileBinding
import com.example.filmlist.util.RepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
