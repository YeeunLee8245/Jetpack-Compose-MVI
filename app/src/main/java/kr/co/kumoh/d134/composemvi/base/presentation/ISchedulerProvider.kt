package kr.co.kumoh.d134.composemvi.base.presentation

import io.reactivex.rxjava3.core.Scheduler

interface ISchedulerProvider {  // 이름에서 I는 단지 Interface라는 것을 알려주기 위해 넣은 것!
    fun io(): Scheduler
    fun computation(): Scheduler
    fun ui(): Scheduler
}