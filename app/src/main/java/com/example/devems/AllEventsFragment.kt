package com.example.devems

class AllEventsFragment : BaseEventsFragment() {
    override fun loadEvents() {
        events.clear()
        events.addAll(dbHelper.getAllEvents())
        eventsAdapter.notifyDataSetChanged()
    }
}
