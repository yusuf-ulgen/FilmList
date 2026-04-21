package com.yusufulgen.filmlist.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepository {
    private val modelName = "gemini-2.5-flash"

    private val generativeModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(modelName)
    }

    private var chatSession: com.google.firebase.ai.Chat? = null

    private fun getChat(): com.google.firebase.ai.Chat {
        return chatSession ?: generativeModel.startChat(
            history = listOf(
                content(role = "user") { text("Sen bir film ve dizi uzmanısın. Kullanıcılara film önerileri yapmalı ve sorularını yanıtlamalısın. Kısa, samimi ve net cevaplar ver. Türkçeyi kusursuz kullan.") },
                content(role = "model") { text("Anladım! Harika film ve dizi önerileri için hazırım. Sana nasıl yardımcı olabilirim?") }
            )
        ).also { chatSession = it }
    }

    suspend fun sendMessage(message: String): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = getChat().sendMessage(message)
            response.text
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMsg = e.message ?: e.localizedMessage ?: "Bilinmeyen hata"

            when {
                errorMsg.contains("API_KEY_INVALID", ignoreCase = true) ||
                errorMsg.contains("403", ignoreCase = true) ->
                    "Hata: API anahtarı geçersiz veya yetkisiz. Firebase konsolunda Gemini API'nin etkinleştirildiğinden emin olun."

                errorMsg.contains("not found", ignoreCase = true) ||
                errorMsg.contains("404", ignoreCase = true) ->
                    "Hata: Model bulunamadı. Lütfen API anahtarınızı ve model isminin doğruluğunu kontrol edin. ($modelName)"

                errorMsg.contains("quota", ignoreCase = true) ||
                errorMsg.contains("429", ignoreCase = true) ->
                    "Hata: Kullanım kotası doldu. Lütfen bir süre sonra tekrar deneyin."

                else -> "Hata: Yapay zeka şu an yanıt veremiyor.\nDetay: $errorMsg"
            }
        }
    }

    suspend fun getRecommendations(userMovies: List<String>): String? {
        val prompt = if (userMovies.isEmpty()) {
            "Bana izleyebileceğim popüler ve kaliteli 20 adet rastgele film/dizi önerir misin? Sadece isimlerini liste halinde ver (1. Film Adı şeklinde)."
        } else {
            "Şu anki listemde şunlar var: ${userMovies.joinToString(", ")}. Bu listeye dayanarak ve her seferinde farklı sonuçlar olacak şekilde bana benzer tarzlarda 20 adet yeni film veya dizi önerir misin? Sadece isimlerini liste halinde ver (1. Film Adı şeklinde)."
        }
        return sendMessage(prompt)
    }
}
