package com.example.devems

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import java.text.SimpleDateFormat
import android.util.Log
import android.provider.Settings
import android.app.AlertDialog
import android.view.LayoutInflater

class CreateEvent : AppCompatActivity() {

    private lateinit var dbHelper: EventDatabaseHelper
    private lateinit var inputName: EditText
    private lateinit var inputLocation: EditText
    private lateinit var inputDate: EditText
    private lateinit var inputTime: EditText
    private lateinit var eventTypeSpinner: Spinner
    private lateinit var inputDescription: EditText
    private lateinit var btnSaveEvent: Button
    private lateinit var eventsListView: ListView
    private lateinit var eventsAdapter: EventsAdapter
    private val events = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        dbHelper = EventDatabaseHelper(this)

        inputName = findViewById(R.id.inputEventName)
        inputLocation = findViewById(R.id.inputLocation)
        inputDate = findViewById(R.id.inputDate)
        inputTime = findViewById(R.id.inputTime)
        eventTypeSpinner = findViewById(R.id.eventTypeSpinner)
        inputDescription = findViewById(R.id.inputDescription)
        btnSaveEvent = findViewById(R.id.btnSaveEvent)
        eventsListView = findViewById(R.id.eventsListView)

        eventsAdapter = EventsAdapter(this, events) { event ->
            showEditDialog(event)
            true
        }
        eventsListView.adapter = eventsAdapter

        loadEvents()

        setupDatePicker()
        setupTimePicker()
        setupSpinner()
        requestNotificationPermission()

        btnSaveEvent.setOnClickListener {
            saveEvent()
        }
    }


    private fun loadEvents() {
        Log.d("CreateEvent", "Loading events from database...")

        events.clear()
        val loadedEvents = dbHelper.getAllEvents()

        if (loadedEvents.isNotEmpty()) {
            events.addAll(loadedEvents)
            eventsAdapter.notifyDataSetChanged()
            Log.d("CreateEvent", "Events loaded successfully.")
        } else {
            Log.d("CreateEvent", "No events found or error retrieving events.")
        }
    }


    private fun setupDatePicker() {
        inputDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                inputDate.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            }, year, month, day)

            datePickerDialog.show()
        }
    }

    private fun setupTimePicker() {
        inputTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                inputTime.setText("$selectedHour:$selectedMinute")
            }, hour, minute, true)

            timePickerDialog.show()
        }
    }

    private fun setupSpinner() {
        val eventTypes = arrayOf("Birthday", "Marriage", "Festivals", "Weddings", "Party", "Conferences")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, eventTypes)
        eventTypeSpinner.adapter = adapter
    }

    private fun saveEvent() {
        val name = inputName.text.toString()
        val location = inputLocation.text.toString()
        val date = inputDate.text.toString()
        val time = inputTime.text.toString()
        val eventType = eventTypeSpinner.selectedItem.toString()
        val description = inputDescription.text.toString()

        val eventId = dbHelper.insertEvent(name, location, date, time, eventType, description)

        if (eventId != -1L) {
            Toast.makeText(this, "Event saved successfully!", Toast.LENGTH_SHORT).show()
            loadEvents()
            scheduleNotification(eventId, name, date, time)
        } else {
            Toast.makeText(this, "Error saving event", Toast.LENGTH_SHORT).show()
        }
    }


    private fun scheduleNotification(eventId: Long, eventName: String, date: String, time: String) {
        try {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(this, NotificationReceiver::class.java).apply {
                putExtra("eventId", eventId)
                putExtra("eventName", eventName)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                this, eventId.toInt(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val dateTimeString = "$date $time"
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateTime = dateTimeFormat.parse(dateTimeString)

            if (dateTime != null && alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateTime.time, pendingIntent)
                Toast.makeText(this, "Notification scheduled for $dateTimeString", Toast.LENGTH_SHORT).show()
                Log.d("scheduleNotification", "Notification scheduled for $dateTimeString")
            } else {
                Log.e("scheduleNotification", "Failed to parse date or time")
            }
        } catch (e: Exception) {
            Log.e("scheduleNotification", "Error scheduling notification", e)
        }
    }


    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditDialog(event: Event) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_event, null)

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
            val dateStr = event.date.split("/")
            if (dateStr.size == 3) {
                calendar.set(dateStr[2].toInt(), dateStr[1].toInt() - 1, dateStr[0].toInt())
            }

            DatePickerDialog(this,
                { _, year, month, day ->
                    editTextDate.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        editTextTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeStr = event.time.split(":")
            if (timeStr.size == 2) {
                calendar.set(Calendar.HOUR_OF_DAY, timeStr[0].toInt())
                calendar.set(Calendar.MINUTE, timeStr[1].toInt())
            }

            TimePickerDialog(this,
                { _, hour, minute ->
                    editTextTime.setText("$hour:$minute")
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Edit Event")
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete", null)
            .create()

        dialog.setOnShowListener { dialogInterface ->
            val positiveButton = (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            val neutralButton = dialogInterface.getButton(AlertDialog.BUTTON_NEUTRAL)

            positiveButton.setOnClickListener {
                val updatedEvent = Event(
                    id = event.id,
                    name = editTextName.text.toString(),
                    location = editTextLocation.text.toString(),
                    date = editTextDate.text.toString(),
                    time = editTextTime.text.toString(),
                    eventType = editTextType.text.toString(),
                    description = editTextDescription.text.toString()
                )

                if (dbHelper.updateEvent(updatedEvent)) {
                    Toast.makeText(this, "Event updated successfully!", Toast.LENGTH_SHORT).show()
                    cancelExistingNotification(event.id)
                    scheduleNotification(event.id, updatedEvent.name, updatedEvent.date, updatedEvent.time)
                    loadEvents()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show()
                }
            }

            neutralButton.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Yes") { _, _ ->
                        if (dbHelper.deleteEvent(event.id)) {
                            Toast.makeText(this, "Event deleted successfully!", Toast.LENGTH_SHORT).show()
                            cancelExistingNotification(event.id)
                            loadEvents()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }

        dialog.show()
    }

    private fun cancelExistingNotification(eventId: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            eventId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

}