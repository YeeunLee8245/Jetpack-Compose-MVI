package kr.co.kumoh.d134.composemvi.moviesearch.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.MovieDetail

class MovieRepository(  // 정의된 DataStore 인터페이스 2개를 인자로 받음
    private val localDataStore: MovieDataStore,
    private val remoteDataStore: MovieDataStore
) : IMovieRepository {
    override fun getMovies(searchQuery: String): Flowable<List<Movie>> {
        return localDataStore.getMovies(searchQuery)
            .map {  // it: List<Movie>, emit(발행)된 결과 and map은 이 함수의 결과를 반환함
                if (it.isNullOrEmpty()) {
                    throw EmptyResultSetException("Movies matching the query isn't in db")
                } else {
                    it
                }
            }   // map은 Single<List<Movie>>를 반환하기 때문에 Flowable 타입으로 변환해줘야함
            .mergeWith(synMovieSearchResult(searchQuery))   // 여러 Single(. 앞 값과 인자 값(Single) 합침)을 하나의 Flowable로 결합
            .onErrorResumeNext {    // it: Trowable and 오류를 알리지 않고 함수를 통해 현재 Flowable의 Publisher로 흐름 재개
                synMovieSearchResult(searchQuery)   // TODO: 무슨 역할?
                    .toFlowable()   // Single을 Flowablw로 변환
            }
    }

    override fun addMovies(movieList: List<Movie>): Completable {
        TODO("Not yet implemented")
    }

    override fun synMovieSearchResult(searchQuery: String): Single<List<Movie>> {
        TODO("Not yet implemented")
    }

    override fun getMovieDetail(imdbId: String): Single<MovieDetail> {
        TODO("Not yet implemented")
    }

}