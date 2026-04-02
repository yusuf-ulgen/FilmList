package com.example.filmlist.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "tr-TR",
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US" // Trailers are mostly en-US
    ): VideoResponse
}

data class VideoResponse(val results: List<Video>)
data class Video(val key: String, val site: String, val type: String)


object RetrofitInstance {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    val tmdbApi: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }
}
