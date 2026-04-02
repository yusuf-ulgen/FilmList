package com.example.filmlist.data.repository

import com.example.filmlist.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRepository {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") { text("Sen bir film ve dizi uzmanısın. Kullanıcılara film önerileri yapmalı ve sorularını yanıtlamalısın.") },
            content(role = "model") { text("Anladım! Ben bir film ve dizi uzmanıyım. Harika öneriler yapmaya hazırım.") }
        )
    )

    suspend fun sendMessage(message: String): String? {
        return try {
            val response = chat.sendMessage(message)
            response.text
        } catch (e: Exception) {
            null
        }
    }
}
