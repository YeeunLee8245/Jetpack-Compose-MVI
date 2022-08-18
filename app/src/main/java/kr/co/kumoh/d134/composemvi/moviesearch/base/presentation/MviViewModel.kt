package kr.co.kumoh.d134.composemvi.moviesearch.base.presentation

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Observable
import kr.co.kumoh.d134.composemvi.moviesearch.base.MviIntent
import kr.co.kumoh.d134.composemvi.moviesearch.base.MviState

interface MviViewModel<I : MviIntent, S : MviState> {
    fun processIntents(intents: Observable<I>)

    fun states(): LiveData<S>
}