package kr.co.kumoh.d134.composemvi.moviesearch.base.presentation

import io.reactivex.rxjava3.core.FlowableTransformer
import kr.co.kumoh.d134.composemvi.moviesearch.base.MviAction
import kr.co.kumoh.d134.composemvi.moviesearch.base.MviResult

interface MviActionProcessor<A: MviAction, R: MviResult> {
    fun transformFromAction(): FlowableTransformer<A, R>    // flowable에서 upstream와 downstream을 인자로 받아
    //~ 하나의 operator(flowable)을 통합함(생산자로 반환)
}