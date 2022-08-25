package kr.co.kumoh.d134.composemvi

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import kr.co.kumoh.d134.composemvi.moviesearch.presentation.MovieIntent
import kr.co.kumoh.d134.composemvi.moviesearch.presentation.MovieViewModel
import kr.co.kumoh.d134.composemvi.moviesearch.presentation.isDetailState
import kr.co.kumoh.d134.composemvi.moviesearch.ui.MovieScreen
import kr.co.kumoh.d134.composemvi.moviesearch.ui.MoviesTheme
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val moviesViewModel: MovieViewModel by viewModels()

    val liveData by lazy {
        moviesViewModel.states()
    }

    val searchHistory = mutableListOf<String>()


    val searchPublisher: PublishSubject<MovieIntent.SearchIntent> = PublishSubject.create()
    val clickPublisher: PublishSubject<MovieIntent.ClickIntent> = PublishSubject.create()
    val clearClickPublisher: PublishSubject<MovieIntent.ClearClickIntent> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoviesTheme {
                MovieScreen(liveData, ::search, ::click)    // 결과적으로 liveData에 MovieIntent 타입의 데이터(Observable)가 전달된다!
            }
        }

        //For logging
        liveData.observe(this, Observer {
            Timber.d("Updated State $it")
        })

        moviesViewModel.processIntents(intents())
    }

    private fun search(query: String) {
        searchHistory.add(query)
        searchWithoutHistory(query)
    }

    private fun searchWithoutHistory(query: String) {
        searchPublisher.onNext(MovieIntent.SearchIntent(query))
    }

    private fun click(imdbId: String) {
        clickPublisher.onNext(MovieIntent.ClickIntent(imdbId))  // 특정 영화 클릭
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun onBackPressed() {
        val state = liveData.value
        if (state != null && state.isDetailState()) {
            clearClickPublisher.onNext(MovieIntent.ClearClickIntent)    // 상세정보 화면에서 없앰
        } else {
            if (state?.query != null && searchHistory.contains(state.query)) {
                searchHistory.remove(state.query)
            }
            val pastSearch = searchHistory.removeLastOrNull()
            if (pastSearch != null) {
                searchWithoutHistory(pastSearch)
            } else {
                super.onBackPressed()
            }
        }
    }

    fun intents(): Observable<MovieIntent> {    // intent 초기화하고 넘겨제
        return Observable.merge(
            Observable.defer {
                Observable.just(
                    MovieIntent.InitaialIntent
                )
            },
            searchPublisher.hide(),
            clickPublisher.hide(),
            clearClickPublisher.hide()
        )
    }
}