package com.marsya.nebr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.marsya.nebr.R
import com.marsya.nebr.adapter.RouteAdapter
import com.marsya.nebr.databinding.BottomSheetRouteDetailBinding
import com.marsya.nebr.databinding.FragmentExploreBinding
import com.marsya.nebr.model.Route
import com.marsya.nebr.repository.RouteRepository

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exploreRoutes = RouteRepository.getExploreRoutes(requireContext())
        val adapter = RouteAdapter(exploreRoutes) { route -> showRouteDetail(route) }
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoutes.adapter = adapter

        binding.btnShareRoute.setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.myRoutesFragment
        }
        binding.cardAddRoute.setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.myRoutesFragment
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    com.google.android.material.snackbar.Snackbar.make(
                        binding.root, "Settings coming soon!", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                    ).show()
                    true
                }
                R.id.action_about -> {
                    com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setTitle("About Nebr")
                        .setMessage("Nebr v1.0\nAn app for sharing daily travel routes and finding ride-sharing buddies.")
                        .setPositiveButton("OK", null)
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun showRouteDetail(route: Route) {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val bsBinding = BottomSheetRouteDetailBinding.inflate(layoutInflater)

        bsBinding.bsOwnerName.text = route.ownerName
        bsBinding.bsRoute.text = "${route.origin} → ${route.destination}"
        bsBinding.bsWaypoints.text = route.wayPoints.mapIndexed { i, wp -> "${i + 1}. $wp" }.joinToString("\n")
        bsBinding.bsDeparture.text = route.departureTime
        bsBinding.bsNebenCost.text = "Rp ${"%,d".format(route.hitchhikeCost).replace(',', '.')}/bln"

        bsBinding.btnSubscribe.setOnClickListener {
            dialog.dismiss()
            
            // Save subscription to local storage
            RouteRepository.addSubscribedRoute(requireContext(), route)
            
            com.google.android.material.snackbar.Snackbar.make(
                binding.root,
                "Successfully subscribed ${route.ownerName}'s route!",
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
            ).show()
        }

        dialog.setContentView(bsBinding.root)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}