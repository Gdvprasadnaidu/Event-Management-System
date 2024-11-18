package com.example.devems

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompletedEventsFragment : BaseEventsFragment() {
    override fun loadEvents() {
        events.clear()
        val allEvents = dbHelper.getAllEvents()
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )

        events.addAll(allEvents.filter { event ->
            val eventDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(event.date)
            eventDate?.before(today) ?: false
        })
        eventsAdapter.notifyDataSetChanged()
    }
}