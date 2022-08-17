package kr.co.kumoh.d134.composemvi.moviesearch.data.local

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kr.co.kumoh.d134.composemvi.moviesearch.data.EmptyResultSetException
import kr.co.kumoh.d134.composemvi.moviesearch.data.MovieDataStore
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.MovieDetail

class LocalMovieDataStore : MovieDataStore {

    // kapt와 Jetpack Compose의 충돌 방지를 위해 아직 여기에 ROOM 구현 X

    val movies: MutableSet<Movie> = mutableSetOf()  // 가변
    val movieDetail: MutableMap<String, MovieDetail> = mutableMapOf()   // 가변, key 값은 set 값과 같이 교유한 값만 가짐

    override fun getMoviesStream(searchQuery: String): Flowable<List<Movie>> {  // TODO: getMovies 함수와 뭐가 다른지 호출단에서 확인
        return Single.defer<List<Movie>> {
            Single.just<List<Movie>>(
                movies.filter {
                    it.title.contains(searchQuery, ignoreCase = true)
                }
            )
        }.toFlowable()
    }

    override fun getMovies(searchQuery: String): Single<List<Movie>> {  // subscribe할 때(defer 작용) just로 갱신하고 바로 데이터 생성
        return Single.defer<List<Movie>> {  // 구독자가 subscribe할 때까지 데이터 흐름 생성 지연(subscribe마다 생성 시점 다름(동시에 X)) and subscribe(실제 데이터 발행)로 동작시킬 observable 객체(데이터 담고 있는, 여기선 Single) 인자로 받음
            Single.just<List<Movie>>(   // 생성시점에(지금) 데이터 바로 방출 (onNext ~ onCompleted)
                movies.filter {
                    it.title.contains(searchQuery, ignoreCase = true)   // ig-: 문자열 비교시 대소문자 무시(default: false)
                }
            )
        }
    }

    override fun addMovies(movieList: List<Movie>): Completable {
        return Completable.fromCallable {   // subscribe 시에 Completable 반환하고 함수 안을 실행시킴, defer과 다르게 어떤 데이터 타입도 사용 가능
            // -> Observable 클래스의 자매(Maybe, Flowable ..)에서 defer와 같은 역할 함
            movies.addAll(movieList)
        }
    }

    override fun getMovieDetail(imdbId: String): Single<MovieDetail> {
        return Single.defer {
            if (movieDetail.containsKey(imdbId)) {
                Single.just(movieDetail.get(imdbId))
            } else {
                Single.error(EmptyResultSetException("Movie not found in DB"))
            }
        }
    }

    override fun addMovieDetail(movie: MovieDetail): Completable {
        return Completable.fromAction { // fromCallable과 기능에선 아무런 차이가 없지만 반환값이 없는 경우에 fromAction이 깔끔
            movieDetail[movie.imdbID] = movie
        }
    }

}