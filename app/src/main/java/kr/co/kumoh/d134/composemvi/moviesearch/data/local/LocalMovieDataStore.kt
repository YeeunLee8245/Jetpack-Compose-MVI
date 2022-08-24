package kr.co.kumoh.d134.composemvi.moviesearch.data.local

import androidx.room.rxjava3.EmptyResultSetException
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kr.co.kumoh.d134.composemvi.moviesearch.data.MovieDataStore
import kr.co.kumoh.d134.composemvi.moviesearch.data.local.database.MovieDao
import kr.co.kumoh.d134.composemvi.moviesearch.data.local.database.MovieEntity
import kr.co.kumoh.d134.composemvi.moviesearch.data.local.database.SearchDao
import kr.co.kumoh.d134.composemvi.moviesearch.data.local.database.SearchHistoryEntity
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.MovieDetail

class LocalMovieDataStore(  // remote에서 응답을 받은 결과가 있고난 후, 사용자가 remote에 없는 결과를 검색할 시, 이전의 캐시값 바로 반환
    private val movieDao: MovieDao, // TODO: MovieDao 어떻게 정의해서 넣는지 구현부 확인
    private val searchDao: SearchDao
) : MovieDataStore {

//    val movies: MutableSet<Movie> = mutableSetOf()  // 가변
//    val movieDetail: MutableMap<String, MovieDetail> = mutableMapOf()   // 가변, key 값은 set 값과 같이 교유한 값만 가짐

    override fun getMoviesStream(searchQuery: String): Flowable<List<Movie>> {  // unit test용
        return movieDao.getMoviesStream(searchQuery)
            .map { list ->
                list.map {
                    Movie(
                        imdbID = it.imdbID,
                        poster = it.poster,
                        title = it.title,
                        type = it.type,
                        year = it.year
                    )
                }
            }
    }

    override fun getMovies(searchQuery: String): Single<List<Movie>> {  // subscribe할 때(defer 작용) just로 갱신하고 바로 데이터 생성
        return movieDao.getMovies(searchQuery)  //  로컬 데이터 볼러와서 매칭
            .map { list ->
                list.map {
                    Movie(
                        imdbID = it.imdbID,
                        poster = it.poster,
                        title = it.title,
                        type = it.type,
                        year = it.year
                    )
                }
            }
//        return Single.defer<List<Movie>> {  // 구독자가 subscribe할 때까지 데이터 흐름 생성 지연(subscribe마다 생성 시점 다름(동시에 X)) and subscribe(실제 데이터 발행)로 동작시킬 observable 객체(데이터 담고 있는, 여기선 Single) 인자로 받음
//            Single.just<List<Movie>>(   // 생성시점에(지금) 데이터 바로 방출 (onNext ~ onCompleted)
//                movies.filter {
//                    it.title.contains(searchQuery, ignoreCase = true)   // ig-: 문자열 비교시 대소문자 무시(default: false)
//                }
//            )
//        }
    }

    override fun addMovies(movieList: List<Movie>): Completable {
        return movieDao.addMovies(
            movieList.map {
                MovieEntity(
                    imdbID = it.imdbID,
                    poster = it.poster,
                    title = it.title,
                    type = it.type,
                    year = it.year
                )
            }
        )
//        return Completable.fromCallable {   // subscribe 시에 Completable 반환하고 함수 안을 실행시킴, defer과 다르게 어떤 데이터 타입도 사용 가능
//            // -> Observable 클래스의 자매(Maybe, Flowable ..)에서 defer와 같은 역할 함
//            movies.addAll(movieList)
//        }
    }

    override fun getMovieDetail(imdbId: String): Single<MovieDetail> {
        return movieDao.getMovie(imdbId)
            .map {
                if (it.detail == null) {
                    throw EmptyResultSetException("detail not present for this movie")
                } else {
                    MovieDetail(
                        imdbID = it.imdbID,
                        poster = it.poster,
                        title = it.title,
                        type = it.type,
                        year = it.year,

                        response = it.detail!!.response,
                        actors = it.detail!!.actors,
                        awards = it.detail!!.awards,
                        boxOffice = it.detail!!.boxOffice,
                        country = it.detail!!.country,
                        dVD = it.detail!!.dVD,
                        director = it.detail!!.director,
                        genre = it.detail!!.genre,
                        imdbRating = it.detail!!.imdbRating,
                        imdbVotes = it.detail!!.imdbVotes,
                        language = it.detail!!.language,
                        metascore = it.detail!!.metascore,
                        plot = it.detail!!.plot,
                        production = it.detail!!.production,
                        rated = it.detail!!.rated,
                        ratings = it.detail!!.ratings.map { rating ->
                            MovieDetail.Rating(
                                source = rating.source,
                                value = rating.value
                            )
                        },
                        released = it.detail!!.released,
                        runtime = it.detail!!.runtime,
                        website = it.detail!!.website,
                        writer = it.detail!!.writer
                    )
                }
            }
//        return Single.defer {
//            if (movieDetail.containsKey(imdbId)) {
//                Single.just(movieDetail.get(imdbId))
//            } else {
//                Single.error(EmptyResultSetException("Movie not found in DB"))
//            }
//        }
    }

    override fun addMovieDetail(movie: MovieDetail): Completable {
        return movieDao.updateMovieInDB(
            MovieEntity(
                imdbID = movie.imdbID,
                poster = movie.poster,
                title = movie.title,
                type = movie.type,
                year = movie.year,
                detail = MovieEntity.Detail(
                    response = movie.response,
                    actors = movie.actors,
                    awards = movie.awards,
                    boxOffice = movie.boxOffice,
                    country = movie.country,
                    dVD = movie.dVD,
                    director = movie.director,
                    genre = movie.genre,
                    imdbRating = movie.imdbRating,
                    imdbVotes = movie.imdbVotes,
                    language = movie.language,
                    metascore = movie.metascore,
                    plot = movie.plot,
                    production = movie.production,
                    rated = movie.rated,
                    ratings = movie.ratings.map {
                        MovieEntity.Detail.Rating(
                            source = it.source,
                            value = it.value
                        )
                    },
                    released = movie.released,
                    runtime = movie.runtime,
                    website = movie.website,
                    writer = movie.writer
                )
            )
        )
//        return Completable.fromAction { // fromCallable과 기능에선 아무런 차이가 없지만 반환값이 없는 경우에 fromAction이 깔끔
//            movieDetail[movie.imdbID] = movie
//        }
    }

    override fun saveSearchHistory(list: List<String>): Completable {   // TODO: 아래 save~와 어디에서 사용성 차이가 있는지 활용 확인
        return searchDao.addSearchHistory(
            list.map {
                SearchHistoryEntity(it, System.currentTimeMillis().toString())
            }
        )
    }

    override fun saveSearchHistory(currentSearch: String): Completable {
        return searchDao.addSearchHistory(
            listOf(SearchHistoryEntity(currentSearch, System.currentTimeMillis().toString()))
        )
    }

    override fun getSearchHistory(): Single<List<String>> {
        return searchDao.getSearchHistroy()
            .map {
                it.map {
                    it.searchTerm   // 키워드만 추출
                }
            }
    }

}