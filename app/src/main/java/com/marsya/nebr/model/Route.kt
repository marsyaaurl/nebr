package com.marsya.nebr.model

data class Route(
    val id: Int,
    val ownerName: String,
    val origin: String,
    val destination: String,
    val wayPoints: List<String>,
    val departureTime: String,
    val transportMode: TransportMode,
    val monthlyCost: Int,
    val scheduleLabel: String,
) {
    val hitchhikeCost: Int get() = monthlyCost / 2
}
