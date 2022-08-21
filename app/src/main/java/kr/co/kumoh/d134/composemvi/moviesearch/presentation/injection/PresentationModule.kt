package kr.co.kumoh.d134.composemvi.moviesearch.presentation.injection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kr.co.kumoh.d134.composemvi.base.presentation.ISchedulerProvider
import kr.co.kumoh.d134.composemvi.moviesearch.data.IMovieRepository
import kr.co.kumoh.d134.composemvi.moviesearch.presentation.MovieProcessor

@Module
@InstallIn(ActivityComponent::class)
object PresentationModule {
    @Provides
    fun provideMovieProcessor(  // TODO: 각 인자 구현부 확인
        repository: IMovieRepository,
        scheduleProvider: ISchedulerProvider
    ) = MovieProcessor(repository, scheduleProvider)
}