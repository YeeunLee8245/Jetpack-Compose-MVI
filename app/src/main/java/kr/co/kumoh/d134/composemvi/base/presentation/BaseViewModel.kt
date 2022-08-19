package kr.co.kumoh.d134.composemvi.base.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.subjects.PublishSubject
import kr.co.kumoh.d134.composemvi.base.*

abstract class BaseViewModel<I : MviIntent, S : MviState, A : MviAction, R : MviResult> :
    ViewModel(),
    MviViewModel<I, S> {

    protected abstract val actionProcessor: MviActionProcessor<A, R>    // 구현해야하는 인터페이스 변수

    private val intentsSubjet: PublishSubject<I> = PublishSubject.create() // 구독 시점부터 발생하는 데이터 전달, subject는 스트림과 관찰자(구독자) 성격 모두 가지고 있음
    private val statesLiveData: LiveData<S> by lazy {
        LiveDataReactiveStreams.fromPublisher(statesObservable)
    }
    val statesObservable: Flowable<S> by lazy {
        compose()
    }

    override fun processIntents(intents: Observable<I>) {
        intents.subscribe(intentsSubjet)
    }

    private fun compose(): Flowable<S> {
        return intentsSubjet
            .toFlowable(BackpressureStrategy.LATEST)    // 최신순으로 유지
            .compose(intentFilter())    // flowable 변형(기존의 전체 스트림에 붙임)
            .map(this::actionFromIntent)    // 각 item에 적용할 operation
            .compose(actionProcessor.transformFromAction())
            .scan(initialSate(), reducer()) // 함수를 인자로 받아 각 item에서 호출되게 함
            .distinctUntilChanged() // subscribe 전에 중복이 연속으로 일어나는 것 제한
            .replay(1)  // 구독을 시작한 후에도 모든 observable에 동일한 시퀀스를 제공하게 함(인자: replay할 아이템 최대 개수)
            .autoConnect(0) // 지정 구독자 수에 도달해야만 item 발행 시작(0이라서 즉각 발행)
    }

    abstract fun actionFromIntent(intent: I): A

    abstract fun intentFilter(): FlowableTransformer<I, I>

    abstract fun reducer(): BiFunction<S, R, S>

    abstract fun initialSate(): S
}