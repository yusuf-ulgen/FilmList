package com.yusufulgen.filmlist.data.repository

import com.yusufulgen.filmlist.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepository {
    private val modelName = "gemini-2.0-flash" // Optimized for newer API keys (2.0/2.5)
    
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
                content(role = "user") { text("Sen bir film ve dizi uzmanısın. Kullanıcılara film önerileri yapmalı ve sorularını yanıtlamalısın. Kısa, samimi ve net cevaplar ver. Türkçeyi kusursuz kullan.") },
                content(role = "model") { text("Anladım! Harika film ve dizi önerileri için hazırım. Sana nasıl yardımcı olabilirim?") }
            )
        ).also { chatSession = it }
    }

    suspend fun sendMessage(message: String): String? = withContext(Dispatchers.IO) {
        if (BuildConfig.GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE" || BuildConfig.GEMINI_API_KEY.isBlank()) {
            return@withContext "Hata: Gemini API anahtarı ayarlanmamış. Lütfen local.properties dosyasını kontrol edin."
        }
        return@withContext try {
            val response = getChat().sendMessage(message)
            response.text
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMsg = e.message ?: ""
            if (errorMsg.contains("API_KEY_INVALID", ignoreCase = true)) {
                "Hata: API anahtarı geçersiz. Lütfen doğru bir anahtar girdiğinizden emin olun."
            } else if (errorMsg.contains("reasons", ignoreCase = true)) {
                "Hata: Mesaj güvenlik politikaları gereği engellendi."
            } else if (errorMsg.contains("not found", ignoreCase = true)) {
                "Hata: Model bulunamadı. Lütfen API anahtarınızın ve model isminin doğruluğunu kontrol edin. (Gemini 1.5 Flash)"
            } else {
                "Hata: Yapay zeka şu an yanıt veremiyor. (${e.localizedMessage})"
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
