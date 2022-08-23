package kr.co.kumoh.d134.composemvi.moviesearch.data.remote

import io.reactivex.rxjava3.core.Single
import kr.co.kumoh.d134.composemvi.BuildConfig
import kr.co.kumoh.d134.composemvi.moviesearch.data.remote.model.MovieDetailResponse
import kr.co.kumoh.d134.composemvi.moviesearch.data.remote.model.MovieSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("/")
    fun searchMovies(
        @Query("s") query: String,
        @Query("type") type: String = "movie",
        @Query("apikey") apikey: String = BuildConfig.ApiKey
    ): Single<MovieSearchResponse> // Rxjava에서 통지할 데이터가 반드시 1건(에러 통지 포함), 통지와 완료 나누어져 있지X, 데이터 통지 or 완료 통지

    @GET("/")
    fun getMovieDetail(
        @Query("i") imdbId: String,
        @Query("apikey") apikey: String = BuildConfig.ApiKey
    ) : Single<MovieDetailResponse>
}