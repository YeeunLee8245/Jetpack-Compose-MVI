package kr.co.kumoh.d134.composemvi.base.injection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kr.co.kumoh.d134.composemvi.base.presentation.ISchedulerProvider
import kr.co.kumoh.d134.composemvi.base.presentation.SchedulerProvider
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class) // 모듈 사용처 레벨
object AppPresentationModule {

    @Provides   // interface를 반환하므로 Hilt에게 반환 정보 알려줌
    @Singleton  // 해당 Component에 의존하는 생명주기 기반의 단일 객체 제공(Component 객체가 달라지면 이 함수 역시 다시 생성됨)
    fun provideScheduleProvider(): ISchedulerProvider =
        SchedulerProvider()
}