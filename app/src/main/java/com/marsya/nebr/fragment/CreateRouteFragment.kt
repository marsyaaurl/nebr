package com.marsya.nebr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.marsya.nebr.R
import com.marsya.nebr.databinding.FragmentCreateRouteBinding
import com.marsya.nebr.model.Route
import com.marsya.nebr.model.TransportMode
import com.marsya.nebr.repository.RouteRepository

class CreateRouteFragment : Fragment() {

    private var _binding: FragmentCreateRouteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button navigation
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Save Route Button click listener
        binding.btnSaveRoute.setOnClickListener {
            if (validateInputs()) {
                saveNewRoute()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Clear existing errors
        binding.tilOrigin.error = null
        binding.tilDestination.error = null
        binding.tilCost.error = null

        val origin = binding.etOrigin.text.toString().trim()
        val destination = binding.etDestination.text.toString().trim()
        val costStr = binding.etCost.text.toString().trim()

        if (origin.isEmpty()) {
            binding.tilOrigin.error = "Departure point cannot be empty"
            isValid = false
        }

        if (destination.isEmpty()) {
            binding.tilDestination.error = "Destination point cannot be empty"
            isValid = false
        }

        if (costStr.isEmpty()) {
            binding.tilCost.error = "Monthly cost cannot be empty"
            isValid = false
        } else {
            val cost = costStr.toIntOrNull()
            if (cost == null || cost <= 0) {
                binding.tilCost.error = "Input a valid monthly cost"
                isValid = false
            }
        }

        return isValid
    }

    private fun saveNewRoute() {
        val origin = binding.etOrigin.text.toString().trim()
        val destination = binding.etDestination.text.toString().trim()
        
        // Parse waypoints
        val waypointsStr = binding.etWaypoints.text.toString().trim()
        val wayPoints = if (waypointsStr.isNotEmpty()) {
            waypointsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            emptyList()
        }

        val departureTime = binding.etDepartureTime.text.toString().trim().ifEmpty { "07.00" }
        val scheduleLabel = binding.etSchedule.text.toString().trim().ifEmpty { "Monday–Friday" }
        val cost = binding.etCost.text.toString().trim().toInt()
        
        val transportMode = if (binding.rbMotorcycle.isChecked) {
            TransportMode.MOTORCYCLE
        } else {
            TransportMode.CAR
        }

        // Generate unique dynamic ID based on timestamp and max route id
        val context = requireContext()
        val existingRoutes = RouteRepository.getMyRoutes(context)
        val maxId = existingRoutes.maxOfOrNull { it.id } ?: 8
        val newId = maxId + 1

        val newRoute = Route(
            id = newId,
            ownerName = "Me",
            origin = origin,
            destination = destination,
            wayPoints = wayPoints,
            departureTime = departureTime,
            transportMode = transportMode,
            monthlyCost = cost,
            scheduleLabel = scheduleLabel
        )

        RouteRepository.addMyRoute(context, newRoute)

        // Show feedback to user
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            "Daily route has been saved",
            Snackbar.LENGTH_LONG
        ).show()

        // Pop backstack to MyRoutesFragment
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
