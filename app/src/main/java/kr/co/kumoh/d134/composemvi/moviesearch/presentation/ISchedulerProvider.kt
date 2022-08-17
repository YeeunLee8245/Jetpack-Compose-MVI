package kr.co.kumoh.d134.composemvi.moviesearch.presentation

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

interface ISchedulerProvider {
    fun io(): Scheduler
    fun computation(): Scheduler
    fun ui(): Scheduler
}

class ScheduleProvider: ISchedulerProvider {
    override fun io(): Scheduler = Schedulers.io()  // 파일/network IO 작업 용도, cachedPool 사용

    override fun computation(): Scheduler = Schedulers.computation() // 대기 시간 없이 빠르게 계산 작업 수행, 논리 연산 처리 시 사용

    override fun ui(): Scheduler = AndroidSchedulers.mainThread()
}