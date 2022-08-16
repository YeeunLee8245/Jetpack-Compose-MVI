package kr.co.kumoh.d134.composemvi.moviesearch.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.MovieDetail

interface MovieDataStore {
    fun getMoviesStream(searchQuery: String): Flowable<List<Movie>>

    fun getMovies(searchQuery: String): Single<List<Movie>>

    fun addMovies(movieList: List<Movie>): Completable

    fun getMovieDetail(imdbId: String): Single<MovieDetail>

    fun addMovieDetail(movie: MovieDetail): Completable
}