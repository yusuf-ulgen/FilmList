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
                content(role = "user") { text("Sen bir film ve dizi uzmanısın. Kullanıcılara film önerileri yapmalı ve sorularını yanıtlamalısın. Resmiyet ile samimiyet arasında ince bir çizgide, kibar ve hevesli bir tonla konuş. Cevaplarında (🚀, ⭐, 🎬, 🍿 vb.) emojiler kullanarak metni görsel olarak zenginleştir. Filmleri veya dizileri listelerken dümdüz metin yerine şık, okunabilir bir format kullan (örneğin kalın harflerle başlıklar ve düzgün liste işaretleri). Türkçeyi kusursuz kullan. \n\nÖNEMLİ FORMAT KURALLARI:\n1. Her paragrafa mutlaka bir TAB boşluğu (veya 4-5 boşluk) bırakarak başla.\n2. Metni tek bir blok yerine mutlaka birden fazla paragrafa böl.\n3. Bol bol emoji kullan, her paragrafın içinde ve sonunda ilgili emojiler olsun.\n4. Film önerirken film adını bir emoji ile başlat, sonra kısa bir açıklama yap ve bir alt satıra geç.") },
                content(role = "model") { text("Anladım! 🍿 Harika film ve dizi önerileri için hazırım. Sana nasıl yardımcı olabilirim? ✨\n\n    Tabii ki! Senin için en güzel önerileri hazırlarken hem görsel olarak şık hem de okuması keyifli bir format kullanacağım. 🎬 Haydi başlayalım! 🚀") }
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
            "Bana izleyebileceğim popüler ve kaliteli 20 adet rastgele film/dizi önerir misin? Şık bir görünümle sun, emojiler de ekle."
        } else {
            "Şu anki listemde şunlar var: ${userMovies.joinToString(", ")}. Bu listeye dayanarak ve her seferinde farklı sonuçlar olacak şekilde bana benzer tarzlarda 20 adet yeni film veya dizi önerir misin? Şık bir görünümle sun, emojiler de ekle."
        }
        return sendMessage(prompt)
    }
}
