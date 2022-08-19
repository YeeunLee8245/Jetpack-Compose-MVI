package kr.co.kumoh.d134.composemvi.moviesearch.data.local.database

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface MovieDao {
    @Query("select * from movies where title like '%' || :searchQuery || '%'")  // title에 searchQuery를 포함하는 모든 데이터
    fun getMoviesStream(searchQuery: String): Flowable<List<MovieEntity>>   // unit test용

    @Query("select * from movies where title like '%' || :searchQuery || '%'")
    fun getMovies(searchQuery: String): Single<List<MovieEntity>>   // 검색했을 때 영화 결과 얻기

    @Query("select * from movies where imdbID = :imdbID")
    fun getMovie(imdbID: String): Single<MovieEntity>   // 검색결과로 선택한 영화(상세정보) 결과 얻기

    @Insert(onConflict = OnConflictStrategy.REPLACE)  // onConflict: 충돌 시(ex. primary key 겹침) 처리 전략, replace: 덮어씀
    fun addMovies(movieList: List<MovieEntity>): Completable    // insert할 때는 인자가 @Entity 클래스여야함

    @Update(onConflict = OnConflictStrategy.REPLACE)    // onConflict: ex. UNIQUE 제약 조건 위반 시
    fun updateMovieInDB(movie: MovieEntity): Completable
}