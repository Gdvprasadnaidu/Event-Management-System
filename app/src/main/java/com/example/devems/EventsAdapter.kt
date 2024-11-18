package com.example.devems

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class EventsAdapter(
    private val context: Context,
    private val events: List<Event>,
    private val onLongClickListener: (Event) -> Boolean
) : BaseAdapter() {

    override fun getCount(): Int = events.size

    override fun getItem(position: Int): Any = events[position]

    override fun getItemId(position: Int): Long = events[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false)

        val event = events[position]

        view.findViewById<TextView>(R.id.eventNameTextView).text = event.name
        view.findViewById<TextView>(R.id.eventDateTextView).text = "${event.date} ${event.time}"
        view.findViewById<TextView>(R.id.eventTypeTextView).text = event.eventType
        view.findViewById<TextView>(R.id.eventDescriptionTextView).text = event.description
        view.findViewById<TextView>(R.id.eventLocationTextView).text = event.location

        view.findViewById<Button>(R.id.btn_navigate_event).setOnClickListener {
            val intent = Intent(context, UserNavigation::class.java)
            intent.putExtra("LOCATION_NAME", event.location)
            context.startActivity(intent)
        }

        // Set long click listener for the entire view
        view.setOnLongClickListener {
            onLongClickListener(event)
        }

        return view
    }
}