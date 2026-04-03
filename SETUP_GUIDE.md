# FilmList API Setup Guide

Uygulamanın tam fonksiyonel çalışabilmesi (film listesi ve AI sohbet) için API anahtarlarınızı eklemeniz gerekmektedir.

## Adımlar:

1.  Projenin ana dizininde (root folder) `local.properties` dosyasını bulun veya oluşturun.
2.  Dosyanın içine şu iki satırı kendi anahtarlarınızla birlikte ekleyin:
    ```properties
    TMDB_API_KEY=buraya_tmdb_api_keyinizi_yazın
    GEMINI_API_KEY=buraya_gemini_api_keyinizi_yazın
    ```
3.  **Önemli**: Anahtarların başında veya sonunda boşluk/tırnak işareti olmadığından emin olun.
4.  Android Studio'da "Sync Project with Gradle Files" butonuna basın.

## Alternatif (Hızlı Kontrol):
Eğer anahtarlarınız henüz yoksa veya hatalıysa, uygulama artık kapanmak yerine size bir uyarı mesajı gösterecektir.

---
*Bu rehber Antigravity tarafından oluşturulmuştur.*
