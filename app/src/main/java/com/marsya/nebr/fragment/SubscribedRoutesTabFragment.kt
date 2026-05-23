package com.marsya.nebr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.marsya.nebr.R
import com.marsya.nebr.adapter.RouteAdapter
import com.marsya.nebr.databinding.BottomSheetRouteDetailBinding
import com.marsya.nebr.databinding.FragmentTabSubscribedRoutesBinding
import com.marsya.nebr.model.Route
import com.marsya.nebr.repository.RouteRepository

class SubscribedRoutesTabFragment : Fragment() {

    private var _binding: FragmentTabSubscribedRoutesBinding? = null
    private val binding get() = _binding!!
    private var adapter: RouteAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabSubscribedRoutesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment is visible/resumed
        loadRoutes()
    }

    private fun setupRecyclerView() {
        binding.rvSubscribedRoutesTab.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadRoutes() {
        val context = requireContext()
        val routes = RouteRepository.getSubscribedRoutes(context)

        if (routes.isEmpty()) {
            binding.rvSubscribedRoutesTab.visibility = View.GONE
            binding.layoutEmptyStateSub.visibility = View.VISIBLE
        } else {
            binding.rvSubscribedRoutesTab.visibility = View.VISIBLE
            binding.layoutEmptyStateSub.visibility = View.GONE

            adapter = RouteAdapter(routes) { route ->
                showSubscribedRouteDetail(route)
            }
            binding.rvSubscribedRoutesTab.adapter = adapter
        }
    }

    private fun showSubscribedRouteDetail(route: Route) {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val bsBinding = BottomSheetRouteDetailBinding.inflate(layoutInflater)

        bsBinding.bsOwnerName.text = route.ownerName
        bsBinding.bsRoute.text = "${route.origin} → ${route.destination}"
        bsBinding.bsWaypoints.text = if (route.wayPoints.isEmpty()) {
            "No way points added"
        } else {
            route.wayPoints.mapIndexed { i, wp -> "${i + 1}. $wp" }.joinToString("\n")
        }
        bsBinding.bsDeparture.text = route.departureTime
        bsBinding.bsNebenCost.text = "Rp ${"%,d".format(route.hitchhikeCost).replace(',', '.')}/bln"

        // Customize the button to act as "Batal Nebeng" (Cancel Hitchhiking)
        bsBinding.btnSubscribe.text = "Cancel Subscription"
        bsBinding.btnSubscribe.setTextColor(ContextCompat.getColor(requireContext(), R.color.nebr_white))
        bsBinding.btnSubscribe.setBackgroundResource(R.drawable.bg_button)
        bsBinding.btnSubscribe.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.nebr_red)

        bsBinding.btnSubscribe.setOnClickListener {
            dialog.dismiss()

            // Material 3 Dialog for cancellation confirmation
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancel Subscription")
                .setMessage("Are you sure you want to cancel your ride with ${route.ownerName}?")
                .setNegativeButton("Back", null)
                .setPositiveButton("Cancel Subscription") { _, _ ->
                    val context = requireContext()
                    RouteRepository.removeSubscribedRoute(context, route.id)
                    loadRoutes()

                    // Snackbar feedback
                    Snackbar.make(
                        requireView(),
                        "Subscription is successfully canceled",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                .show()
        }

        dialog.setContentView(bsBinding.root)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
