package com.example.devems

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> TodayEventsFragment()
            1 -> AllEventsFragment()
            2 -> CompletedEventsFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}
