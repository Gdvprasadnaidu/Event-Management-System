package com.example.devems

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.util.Calendar
import java.util.Locale

abstract class BaseEventsFragment : Fragment() {
    protected lateinit var eventsListView: ListView
    protected lateinit var searchView: SearchView
    protected lateinit var dbHelper: EventDatabaseHelper
    protected val allEvents = mutableListOf<Event>() // All events fetched from DB
    protected val filteredEvents = mutableListOf<Event>() // Events to be displayed
    protected lateinit var eventsAdapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventsListView = view.findViewById(R.id.eventsListView)
        searchView = view.findViewById(R.id.searchView)
        dbHelper = EventDatabaseHelper(requireContext())

        eventsAdapter = EventsAdapter(requireContext(), filteredEvents) { event ->
            showEditDialog(event)
            true
        }

        eventsListView.adapter = eventsAdapter

        loadEvents() // Load initial data
        // Setup search functionality

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterEvents(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Reset to all events when text is cleared
                if (newText.isNullOrEmpty()) {
                    filterEvents("") // Passing an empty string resets the list
                } else {
                    filterEvents(newText)
                }
                return true
            }
        })
    }

    abstract fun loadEvents()

    private fun filterEvents(query: String?) {
        filteredEvents.clear()
        if (query.isNullOrEmpty()) {
            filteredEvents.addAll(allEvents) // Show all events when query is empty
        } else {
            val lowerCaseQuery = query.toLowerCase(Locale.getDefault())
            filteredEvents.addAll(allEvents.filter { event ->
                event.name.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)
            })
        }
        eventsAdapter.notifyDataSetChanged()
    }

    protected fun showEditDialog(event: Event) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_event, null)

        val editTextName = dialogView.findViewById<EditText>(R.id.editTextEventName)
        val editTextLocation = dialogView.findViewById<EditText>(R.id.editTextEventLocation)
        val editTextDate = dialogView.findViewById<EditText>(R.id.editTextEventDate)
        val editTextTime = dialogView.findViewById<EditText>(R.id.editTextEventTime)
        val editTextType = dialogView.findViewById<EditText>(R.id.editTextEventType)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextEventDescription)

        editTextName.setText(event.name)
        editTextLocation.setText(event.location)
        editTextDate.setText(event.date)
        editTextTime.setText(event.time)
        editTextType.setText(event.eventType)
        editTextDescription.setText(event.description)

        editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val selectedDate = String.format(
                        Locale.getDefault(),
                        "%02d/%02d/%04d",
                        day, month + 1, year
                    )
                    editTextDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        editTextTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    val selectedTime = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        hour, minute
                    )
                    editTextTime.setText(selectedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Edit Event")
            .setPositiveButton("Save") { _, _ ->
                val updatedEvent = Event(
                    id = event.id,
                    name = editTextName.text.toString(),
                    location = editTextLocation.text.toString(),
                    date = editTextDate.text.toString(),
                    time = editTextTime.text.toString(),
                    eventType = editTextType.text.toString(),
                    description = editTextDescription.text.toString()
                )

                dbHelper.updateEvent(updatedEvent)
                loadEvents()
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete") { _, _ ->
                dbHelper.deleteEvent(event.id)
                loadEvents()
            }
            .create()

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}