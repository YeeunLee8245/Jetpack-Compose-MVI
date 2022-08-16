package kr.co.kumoh.d134.composemvi.moviesearch.data.model

data class MovieDetail(
    val actors: List<String> = emptyList(),
    val awards: String = "",
    val boxOffice: String = "",
    val country: String = "",
    val dVD: String = "",
    val director: String = "",
    val genre: String = "",
    val imdbID: String = "",
    val imdbRating: String = "",
    val imdbVotes: String = "",
    val language: String = "",
    val metascore: String = "",
    val plot: String = "",
    val poster: String = "",
    val production: String = "",
    val rated: String = "",
    val ratings: List<Rating> = listOf(),
    val released: String = "",
    val response: String = "",
    val runtime: String = "",
    val title: String = "",
    val type: String = "",
    val website: String = "",
    val writer: String = "",
    val year: String = ""
) {
    data class Rating(
        val source: String = "",
        val value: String = ""
    )
}
