package com.marsya.nebr.repository

import android.content.Context
import android.content.SharedPreferences
import com.marsya.nebr.model.Route
import com.marsya.nebr.model.TransportMode
import org.json.JSONArray
import org.json.JSONObject

object RouteRepository {
    private const val PREFS_NAME = "nebr_prefs"
    private const val KEY_MY_ROUTES = "key_my_routes"
    private const val KEY_SUBSCRIBED_ROUTES = "key_subscribed_routes"

    // Default mock routes shown on Explore screen
    private val DEFAULT_EXPLORE_ROUTES = listOf(
        Route(1, "Marsya", "Depok", "Sudirman", listOf("Pasar Minggu", "Pancoran", "Kuningan"), "07.00", TransportMode.MOTORCYCLE, 300000, "Senin–Jumat"),
        Route(2, "Budi", "Bekasi", "Thamrin", listOf("Cawang", "Casablanca", "Semanggi"), "06.30", TransportMode.CAR, 600000, "Setiap Hari"),
        Route(3, "Sari", "Bogor", "UI Depok", listOf("Citayam", "Pondok Cina", "Margonda"), "06.00", TransportMode.MOTORCYCLE, 250000, "Senin–Jumat"),
        Route(4, "Dimas", "Tangerang", "Gatot Subroto", listOf("BSD", "Lebak Bulus", "Blok M"), "07.30", TransportMode.CAR, 800000, "Setiap Hari"),
        Route(5, "Reza", "Ciputat", "Fatmawati", listOf("Pamulang", "Cirendeu", "Lebak Bulus"), "07.00", TransportMode.MOTORCYCLE, 200000, "Senin–Sabtu"),
        Route(6, "Putri", "Serpong", "SCBD", listOf("Bintaro", "Pesanggrahan", "Senayan"), "08.00", TransportMode.CAR, 700000, "Senin–Jumat")
    )

    // User's own default starting routes
    private val DEFAULT_MY_ROUTES = listOf(
        Route(7, "Me", "Depok", "Sudirman", listOf("Pasar Minggu", "Pancoran", "Kuningan"), "07.00", TransportMode.MOTORCYCLE, 300000, "Senin–Jumat"),
        Route(8, "Me", "Sudirman", "Gym Senayan", listOf("Semanggi", "Senayan"), "17.30", TransportMode.MOTORCYCLE, 100000, "Selasa–Kamis")
    )

    private var myRoutesCache: MutableList<Route>? = null
    private var subscribedRoutesCache: MutableList<Route>? = null

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getExploreRoutes(context: Context): List<Route> {
        // Return standard mockup routes for explore feed
        return DEFAULT_EXPLORE_ROUTES
    }

    fun getMyRoutes(context: Context): List<Route> {
        if (myRoutesCache != null) return myRoutesCache!!

        val prefs = getPrefs(context)
        val jsonString = prefs.getString(KEY_MY_ROUTES, null)
        if (jsonString == null) {
            myRoutesCache = DEFAULT_MY_ROUTES.toMutableList()
            saveMyRoutesToPrefs(context)
        } else {
            myRoutesCache = deserializeRoutes(jsonString).toMutableList()
        }
        return myRoutesCache!!
    }

    fun addMyRoute(context: Context, route: Route) {
        val current = getMyRoutes(context).toMutableList()
        current.add(route)
        myRoutesCache = current
        saveMyRoutesToPrefs(context)
    }

    fun deleteMyRoute(context: Context, routeId: Int) {
        val current = getMyRoutes(context).toMutableList()
        current.removeAll { it.id == routeId }
        myRoutesCache = current
        saveMyRoutesToPrefs(context)
    }

    private fun saveMyRoutesToPrefs(context: Context) {
        val list = myRoutesCache ?: return
        val jsonString = serializeRoutes(list)
        getPrefs(context).edit().putString(KEY_MY_ROUTES, jsonString).apply()
    }

    fun getSubscribedRoutes(context: Context): List<Route> {
        if (subscribedRoutesCache != null) return subscribedRoutesCache!!

        val prefs = getPrefs(context)
        val jsonString = prefs.getString(KEY_SUBSCRIBED_ROUTES, null)
        if (jsonString == null) {
            subscribedRoutesCache = mutableListOf()
        } else {
            subscribedRoutesCache = deserializeRoutes(jsonString).toMutableList()
        }
        return subscribedRoutesCache!!
    }

    fun addSubscribedRoute(context: Context, route: Route) {
        val current = getSubscribedRoutes(context).toMutableList()
        if (current.none { it.id == route.id }) {
            current.add(route)
            subscribedRoutesCache = current
            saveSubscribedRoutesToPrefs(context)
        }
    }

    fun removeSubscribedRoute(context: Context, routeId: Int) {
        val current = getSubscribedRoutes(context).toMutableList()
        current.removeAll { it.id == routeId }
        subscribedRoutesCache = current
        saveSubscribedRoutesToPrefs(context)
    }

    private fun saveSubscribedRoutesToPrefs(context: Context) {
        val list = subscribedRoutesCache ?: return
        val jsonString = serializeRoutes(list)
        getPrefs(context).edit().putString(KEY_SUBSCRIBED_ROUTES, jsonString).apply()
    }

    // JSON Serialization Helpers
    private fun serializeRoutes(routes: List<Route>): String {
        val array = JSONArray()
        for (route in routes) {
            val obj = JSONObject()
            obj.put("id", route.id)
            obj.put("ownerName", route.ownerName)
            obj.put("origin", route.origin)
            obj.put("destination", route.destination)
            
            val wpArray = JSONArray()
            route.wayPoints.forEach { wpArray.put(it) }
            obj.put("wayPoints", wpArray)
            
            obj.put("departureTime", route.departureTime)
            obj.put("transportMode", route.transportMode.name)
            obj.put("monthlyCost", route.monthlyCost)
            obj.put("scheduleLabel", route.scheduleLabel)
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeRoutes(jsonString: String): List<Route> {
        val list = mutableListOf<Route>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.getInt("id")
                val ownerName = obj.getString("ownerName")
                val origin = obj.getString("origin")
                val destination = obj.getString("destination")
                
                val wpArray = obj.getJSONArray("wayPoints")
                val wayPoints = mutableListOf<String>()
                for (j in 0 until wpArray.length()) {
                    wayPoints.add(wpArray.getString(j))
                }
                
                val departureTime = obj.getString("departureTime")
                val transportModeStr = obj.getString("transportMode")
                val transportMode = TransportMode.valueOf(transportModeStr)
                val monthlyCost = obj.getInt("monthlyCost")
                val scheduleLabel = obj.getString("scheduleLabel")
                
                list.add(Route(id, ownerName, origin, destination, wayPoints, departureTime, transportMode, monthlyCost, scheduleLabel))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
