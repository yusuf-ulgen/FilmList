package com.example.filmlist.data.remote

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("results")
    val results: List<Movie>
)

data class Movie(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val movieTitle: String?,
    @SerializedName("name")
    val tvName: String?,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("first_air_date")
    val firstAirDate: String?,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("media_type")
    val mediaType: String?
) {
    val title: String get() = movieTitle ?: tvName ?: "Bilinmeyen"
    val date: String? get() = releaseDate ?: firstAirDate

    fun getFullPosterUrl() = if (posterPath != null) "https://image.tmdb.org/t/p/w500$posterPath" else null
}
