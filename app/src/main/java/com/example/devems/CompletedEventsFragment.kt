package com.example.devems

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompletedEventsFragment : BaseEventsFragment() {
    override fun loadEvents() {
        allEvents.clear()
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )

        allEvents.addAll(dbHelper.getAllEvents().filter { event ->
            val eventDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(event.date)
            eventDate?.before(today) ?: false
        }) // Fetch completed events
        filteredEvents.clear()
        filteredEvents.addAll(allEvents) // Initially display completed events
        eventsAdapter.notifyDataSetChanged()
    }
}

