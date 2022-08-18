package kr.co.kumoh.d134.composemvi.base.presentation

import io.reactivex.rxjava3.core.FlowableTransformer
import kr.co.kumoh.d134.composemvi.base.MviAction
import kr.co.kumoh.d134.composemvi.base.MviResult
import kr.co.kumoh.d134.composemvi.base.presentation.ISchedulerProvider

interface MviActionProcessor<A: MviAction, R: MviResult> {
    val schedulerProvider: ISchedulerProvider   // TODO: 인터페이스를 변수로 사용하는 이유?

    fun transformFromAction(): FlowableTransformer<A, R>    // flowable에서 upstream와 downstream을 인자로 받아
    //~ 하나의 operator(flowable)을 통합함(생산자로 반환)
}