package com.example.devems

class AllEventsFragment : BaseEventsFragment() {
    override fun loadEvents() {
        allEvents.clear()
        allEvents.addAll(dbHelper.getAllEvents()) // Fetch all events from DB
        filteredEvents.clear()
        filteredEvents.addAll(allEvents) // Initially display all events
        eventsAdapter.notifyDataSetChanged()
    }
}


