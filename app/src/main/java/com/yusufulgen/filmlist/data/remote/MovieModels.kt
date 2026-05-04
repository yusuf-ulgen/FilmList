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

data class ExternalIdsResponse(
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("facebook_id") val facebookId: String?,
    @SerializedName("instagram_id") val instagramId: String?,
    @SerializedName("twitter_id") val twitterId: String?
)

data class WatchProvidersResponse(
    @SerializedName("results") val results: Map<String, CountryProviders>
)

data class CountryProviders(
    @SerializedName("link") val link: String,
    @SerializedName("flatrate") val flatrate: List<Provider>?,
    @SerializedName("rent") val rent: List<Provider>?,
    @SerializedName("buy") val buy: List<Provider>?
)

data class Provider(
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("logo_path") val logoPath: String
) {
    fun getFullLogoUrl() = "https://image.tmdb.org/t/p/original$logoPath"
}

data class OmdbMovieResponse(
    @SerializedName("imdbRating") val imdbRating: String?,
    @SerializedName("Ratings") val ratings: List<OmdbRating>?,
    @SerializedName("Metascore") val metascore: String?
)

data class OmdbRating(
    @SerializedName("Source") val source: String,
    @SerializedName("Value") val value: String
)

data class PersonDetails(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("biography") val biography: String?,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("deathday") val deathday: String?,
    @SerializedName("place_of_birth") val placeOfBirth: String?,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("known_for_department") val knownFor: String?
) {
    fun getFullProfileUrl() = if (profilePath != null) "https://image.tmdb.org/t/p/h632$profilePath" else null
}

data class CombinedCreditsResponse(
    @SerializedName("cast") val cast: List<Movie>
)
