package com.example.devems

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodayEventsFragment : BaseEventsFragment() {
    override fun loadEvents() {
        allEvents.clear()
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        allEvents.addAll(dbHelper.getAllEvents().filter { it.date == today }) // Fetch today's events
        filteredEvents.clear()
        filteredEvents.addAll(allEvents) // Initially display today's events
        eventsAdapter.notifyDataSetChanged()
    }
}


