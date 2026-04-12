package com.example.filmlist.data.repository

import com.example.filmlist.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepository {
    private val modelName = "gemini-1.5-flash"
    
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = modelName,
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    private var chatSession: com.google.ai.client.generativeai.Chat? = null

    private fun getChat(): com.google.ai.client.generativeai.Chat {
        return chatSession ?: generativeModel.startChat(
            history = listOf(
                content(role = "user") { text("Sen bir film ve dizi uzmanısın. Kullanıcılara film önerileri yapmalı ve sorularını yanıtlamalısın. Kısa ve net cevaplar ver.") },
                content(role = "model") { text("Anladım! Ben bir film ve dizi uzmanıyım. Harika öneriler yapmaya hazırım.") }
            )
        ).also { chatSession = it }
    }

    suspend fun sendMessage(message: String): String? = withContext(Dispatchers.IO) {
        if (BuildConfig.GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE" || BuildConfig.GEMINI_API_KEY.isBlank()) {
            return@withContext "Gemini API anahtarı eksik! Lütfen local.properties dosyasını kontrol edin."
        }
        return@withContext try {
            val response = getChat().sendMessage(message)
            response.text
        } catch (e: Exception) {
            e.printStackTrace()
            // Reset chat session on error to allow retry with fresh state
            chatSession = null
            "Hata: ${e.message ?: "Bilinmeyen bir hata oluştu."}"
        }
    }

    suspend fun getRecommendations(userMovies: List<String>): String? {
        val prompt = if (userMovies.isEmpty()) {
            "Bana izleyebileceğim popüler ve kaliteli bir film/dizi önerir misin?"
        } else {
            "Şu anki listemde şunlar var: ${userMovies.joinToString(", ")}. Bu listeye dayanarak bana benzer tarzlarda 3 adet yeni film veya dizi önerir misin? Neden önermek istediğini de kısaca açıkla."
        }
        return sendMessage(prompt)
    }
}
