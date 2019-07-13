package com.example.kotlinmessenger.objects

//"id" is the id of the ChatMessage object itself
class ChatMessage(val id: String, val senderID: String, val receiverID: String, val text: String, val timestamp: Long) {
    constructor() : this ("", "", "", "", -1)
}