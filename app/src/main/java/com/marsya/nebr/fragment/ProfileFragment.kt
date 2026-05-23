package com.marsya.nebr.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.marsya.nebr.R
import com.marsya.nebr.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Exit") { _, _ ->
                            com.google.android.material.snackbar.Snackbar.make(
                                binding.root, "Goodbye!", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                    true
                }
                else -> false
            }
        }

        binding.tvSettings.setOnClickListener {
            com.google.android.material.snackbar.Snackbar.make(
                binding.root, "Settings coming soon!", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
            ).show()
        }

        binding.tvAbout.setOnClickListener {
            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("About Nebr")
                .setMessage("Nebr v1.0\nAn app for sharing daily travel routes and finding ride-sharing buddies.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}