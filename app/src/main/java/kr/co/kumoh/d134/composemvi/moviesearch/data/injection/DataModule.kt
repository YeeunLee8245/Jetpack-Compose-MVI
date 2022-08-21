package kr.co.kumoh.d134.composemvi.moviesearch.data.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kr.co.kumoh.d134.composemvi.moviesearch.data.IMovieRepository
import kr.co.kumoh.d134.composemvi.moviesearch.data.MovieDataStore
import kr.co.kumoh.d134.composemvi.moviesearch.data.MovieRepository
import kr.co.kumoh.d134.composemvi.moviesearch.data.local.LocalMovieDataStore
import kr.co.kumoh.d134.composemvi.moviesearch.data.local.database.MovieDB
import kr.co.kumoh.d134.composemvi.moviesearch.data.local.database.MovieDao
import kr.co.kumoh.d134.composemvi.moviesearch.data.local.database.SearchDao
import kr.co.kumoh.d134.composemvi.moviesearch.data.remote.MovieApi
import kr.co.kumoh.d134.composemvi.moviesearch.data.remote.MovieApiFactory
import kr.co.kumoh.d134.composemvi.moviesearch.data.remote.RemoteMovieDataStore
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object DataModule {
    @Provides   // interface를 반환하므로 Hilt에게 반환 정보 알려줌
    @Named("local") // provider에서 제공(반환)하는 인스턴스 타입이 같은 경우 어떤 인스턴스를 주입해야할지 구분하기 위해 id 지정
    fun provideLocalMovieDataStore(movieDao: MovieDao, searchDao: SearchDao): MovieDataStore =  // 충돌을 replace로 지정해놔서 별도로 single 안 추가한듯
        LocalMovieDataStore(movieDao, searchDao)    // 구현한 LocalMovieDataStore 객체 반환
    // ~TODO: MovieDao 인터페이스에는 어떤 구현부 들어갈지?

    @Provides
    @Named("remote")
    fun provideRemoteMovieDataStore(api: MovieApi): MovieDataStore = RemoteMovieDataStore(api)  // TODO: 인자 인터페이스 구현부 확인

    @Provides
    fun provideMovieRepository(
        @Named("local") localMovieDataStore: MovieDataStore,
        @Named("remote") remoteMovieDataStore: MovieDataStore
    ): IMovieRepository = MovieRepository(localMovieDataStore, remoteMovieDataStore)
}

@Module
@InstallIn(ActivityComponent::class)
object AppDataModule {
    @Provides
    @Singleton
    fun provideMovieApi(): MovieApi = MovieApiFactory.makeMovieApi()

    @Provides
    @Singleton
    fun provideDB(@ApplicationContext context: Context): MovieDB = MovieDB.getInstance(context) // room DB 객체 생성

    @Provides
    @Singleton
    fun provideMovieDao(db: MovieDB): MovieDao = db.movieDao()  // TODO: 추상 인자 구현부 확인

    @Provides
    @Singleton
    fun provideSearchDao(db: MovieDB): SearchDao = db.searchDao() // TODO: 이하동문
}