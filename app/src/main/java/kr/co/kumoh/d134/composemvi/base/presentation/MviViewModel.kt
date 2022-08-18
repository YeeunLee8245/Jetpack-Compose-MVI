package kr.co.kumoh.d134.composemvi.base.presentation

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Observable
import kr.co.kumoh.d134.composemvi.base.MviIntent
import kr.co.kumoh.d134.composemvi.base.MviState

interface MviViewModel<I : MviIntent, S : MviState> {
    fun processIntents(intents: Observable<I>)

    fun states(): LiveData<S>
}