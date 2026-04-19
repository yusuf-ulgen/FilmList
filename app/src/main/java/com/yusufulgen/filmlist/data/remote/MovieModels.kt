package com.yusufulgen.filmlist.data.remote

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
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String? = null,
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
    fun getFullBackdropUrl() = if (backdropPath != null) "https://image.tmdb.org/t/p/w780$backdropPath" else getFullPosterUrl()
}

data class Genre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class MovieDetails(
    @SerializedName("id")
    val id: Int,
    @SerializedName("runtime")
    val runtime: Int? = null,
    @SerializedName("episode_run_time")
    val episodeRuntime: List<Int>? = null,
    @SerializedName("genres")
    val genres: List<Genre>
)

data class CreditsResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cast")
    val cast: List<Cast>,
    @SerializedName("crew")
    val crew: List<Crew>
)

data class Cast(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("character")
    val character: String,
    @SerializedName("profile_path")
    val profilePath: String?
) {
    fun getFullProfileUrl() = if (profilePath != null) "https://image.tmdb.org/t/p/w185$profilePath" else null
}

data class Crew(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("job")
    val job: String
)
