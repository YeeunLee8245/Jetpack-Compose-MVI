package kr.co.kumoh.d134.composemvi.moviesearch.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.MovieDetail

interface IMovieRepository {
    fun getMovies(searchQuery: String): Flowable<List<Movie>>

    fun addMovies(movieList: List<Movie>): Completable

    fun synMovieSearchResult(searchQuery: String): Single<List<Movie>>  // TODO: 무슨 역할? 결과 출력 상황?

    fun getMovieDetail(imdbId: String): Flowable<MovieDetail>

    fun saveSearchResult(list: List<String>): Completable

    fun getSearchHistory(): Single<List<String>>
}