package kr.co.kumoh.d134.composemvi.moviesearch.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import androidx.room.rxjava3.EmptyResultSetException
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.MovieDetail

class MovieRepository(  // 정의된 DataStore 인터페이스 2개를 인자로 받음
    private val localDataStore: MovieDataStore,
    private val remoteDataStore: MovieDataStore
) : IMovieRepository {
    override fun getMovies(searchQuery: String): Flowable<List<Movie>> {    // 검색 결과 내부 db에서 먼저 찾기. 없을 시 원격으로 찾기
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

    override fun synMovieSearchResult(searchQuery: String): Single<List<Movie>> {
        return remoteDataStore.getMovies(searchQuery)
            .flatMap { movies -> // List<Movie>
                // flatMap: api 순서를 지정할 때 유용함(내부 아이템들은 순서 X(iterable일 때만 주의하면 될듯)). 불러야할 api가 여러개일 때 flatMap을 순서대로 정의한다. 앞 순서가 정상 실행된 경우에만 호출됨
                if (movies.isNotEmpty()) {  // 원격에 데이터 있을 때
                    addMovies(movies)   // 로컬 데베에 데이터 저장
                        .andThen(localDataStore.saveSearchHistory(searchQuery)) // 검색 기록도 로컬에 저장
                        .andThen(
                            Single.just(movies) // TODO: 반환 이유? 테스트 용도?
                        )
                } else {  // 불러올 수 있는 서버 데이터가 없음
                    Single.error(EmptyResultSetException("No data found in Remote"))
                }
            }
    }

    override fun addMovies(movieList: List<Movie>): Completable {
        return localDataStore.addMovies(movieList)
    }

    override fun getMovieDetail(imdbId: String): Flowable<MovieDetail> {    // TODO: 외부에서 쓰임?
        return localDataStore.getMovieDetail(imdbId)    // 로컬에서 먼저 탐색
            .flatMapPublisher{ localMovieDetail ->
                Flowable.just(localMovieDetail)
                    .mergeWith (
                        remoteDataStore.getMovieDetail(imdbId)  // 원격 탐색
                            .flatMap { remoteMovieDetail ->
                                localDataStore.addMovieDetail(remoteMovieDetail)    // 원격 탐색 결과 로컬 저장(데이터 업뎃을 위해 이렇게 하는듯)
                                    .andThen(Single.just(remoteMovieDetail))    // 호출되면 바로 할당(발행할 때 할당하고싶으면 defer 쓰자) => 근데 마지막에 maybe여서 사용자가 쓸 수 있는 data는 아님
                            }
                            .onErrorComplete()  // 오류(Throwable) 무시하고 완료 보냄(maybe observable: 데이터 발행X)
                    )
            }
            .onErrorResumeNext {    // (로컬에서 값 가져오는)에러 발생 시, 무시한 다음 실행되어야하는 publisher 정의
                remoteDataStore.getMovieDetail(imdbId)  // 원격 탐색
                    .flatMap { remoteMovieDetail ->
                        localDataStore.addMovieDetail(remoteMovieDetail)     // 원격 탐색 결과 로컬 저장
                            .andThen(Single.just(remoteMovieDetail))
                    }
                    .toFlowable()   // 로컬에 데이터가 없어도 원격으로 받아서 쓸 수 있음!
            }
    }

    override fun saveSearchResult(list: List<String>): Completable {
        return localDataStore.saveSearchHistory(list = list)    // TODO: 히스토리 'list 저장'은 외부에서 어디에 쓰일까?
    }

    override fun getSearchHistory(): Single<List<String>> {
        return localDataStore.getSearchHistory()
    }

}