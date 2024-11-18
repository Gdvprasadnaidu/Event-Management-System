package com.example.devems

data class Event(
    val id: Long,
    var name: String,
    var location: String,
    var date: String,
    var time: String,
    var eventType: String,
    var description: String
)
