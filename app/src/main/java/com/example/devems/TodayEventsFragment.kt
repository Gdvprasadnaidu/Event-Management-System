package com.example.devems

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodayEventsFragment : BaseEventsFragment() {
    override fun loadEvents() {
        events.clear()
        val allEvents = dbHelper.getAllEvents()
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        events.addAll(allEvents.filter { it.date == today })
        eventsAdapter.notifyDataSetChanged()
    }
}
