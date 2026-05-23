package com.marsya.nebr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.marsya.nebr.R
import com.marsya.nebr.databinding.FragmentMyRoutesBinding

class MyRoutesFragment : Fragment() {

    private var _binding: FragmentMyRoutesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyRoutesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up ViewPager2 with TabLayout
        val tabAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                return if (position == 0) {
                    MyOwnRoutesTabFragment()
                } else {
                    SubscribedRoutesTabFragment()
                }
            }
        }
        binding.viewPager.adapter = tabAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "My Routes" else "Subscription"
        }.attach()

        // Sync Fab Add Route click to navigate to form
        binding.fabAddRoute.setOnClickListener {
            findNavController().navigate(R.id.action_myRoutesFragment_to_createRouteFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}