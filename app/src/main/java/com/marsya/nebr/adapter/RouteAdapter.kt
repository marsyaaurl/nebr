package com.marsya.nebr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marsya.nebr.databinding.ItemRouteBinding
import com.marsya.nebr.model.Route
import com.marsya.nebr.model.TransportMode

class RouteAdapter(
    private val routes: List<Route>,
    private val onItemClick: (Route) -> Unit
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    inner class RouteViewHolder(private val binding: ItemRouteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(route: Route) {
            val (_, ownerName, origin, destination, waypoints, departureTime, transportMode, monthlyCost, scheduleLabel) = route

            binding.tvOwnerName.text = ownerName
            binding.tvOrigin.text = origin
            binding.tvDestination.text = destination
            binding.tvDeparture.text = "🕐   $departureTime"
            binding.tvSchedule.text = "🗓️   $scheduleLabel"
            if (transportMode == TransportMode.MOTORCYCLE) {
                binding.tvTransportIcon.text = "🏍"
                binding.tvTransportMode.text = "Motorcycle"
            } else {
                binding.tvTransportIcon.text = "🚗"
                binding.tvTransportMode.text = "Car"
            }
            binding.tvNebenCost.text = "Rp ${"%,d".format(route.hitchhikeCost).replace(',', '.')}/bln"

            binding.btnDetail.setOnClickListener { onItemClick(route) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding = ItemRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(routes[position])
    }

    override fun getItemCount() = routes.size
}