package kr.co.kumoh.d134.composemvi.moviesearch.data.remote.model

import com.google.gson.annotations.SerializedName
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import retrofit2.Response

data class MovieSearchResponse(
    @SerializedName("Response")
    val response: String = "",  // True
    @SerializedName("Search")
    val movies: List<Movie> = listOf(),
    @SerializedName("totalResults")
    val totalResults: String = ""   // 96
) {
    data class Movie(   // Movie 클래스에 들어오는 값 형태와 동일
        @SerializedName("imdbID")
        val imdbID: String = "",
        @SerializedName("Poster")
        val poster: String = "",
        @SerializedName("Title")
        val title: String = "",
        @SerializedName("Type")
        val type: String = "",
        @SerializedName("Year")
        val year: String = ""
    )
}
