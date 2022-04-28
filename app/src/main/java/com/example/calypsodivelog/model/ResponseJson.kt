package com.example.calypsodivelog.model

class ResponseJson {
    var status: String? = null
    var message: String? = null

    override fun toString(): String {
        return "Response: { Status: $status, Message: $message }"
    }
}