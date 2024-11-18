package com.example.devems

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EventDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "events.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "events"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_EVENT_TYPE = "event_type"
        const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_LOCATION TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_TIME TEXT,
                $COLUMN_EVENT_TYPE TEXT,
                $COLUMN_DESCRIPTION TEXT
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun deleteEvent(eventId: Long): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(eventId.toString()))
        return rowsDeleted > 0
    }

    fun insertEvent(name: String, location: String, date: String, time: String, eventType: String, description: String): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_LOCATION, location)
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_TIME, time)
        values.put(COLUMN_EVENT_TYPE, eventType)
        values.put(COLUMN_DESCRIPTION, description)
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllEvents(): List<Event> {
        val eventsList = mutableListOf<Event>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val event = Event(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                    eventType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EVENT_TYPE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                )
                eventsList.add(event)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return eventsList
    }

    fun updateEvent(event: Event): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, event.name)
            put(COLUMN_LOCATION, event.location)
            put(COLUMN_DATE, event.date)
            put(COLUMN_TIME, event.time)
            put(COLUMN_EVENT_TYPE, event.eventType)
            put(COLUMN_DESCRIPTION, event.description)
        }
        val rowsUpdated = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(event.id.toString()))
        return rowsUpdated > 0
    }
}
