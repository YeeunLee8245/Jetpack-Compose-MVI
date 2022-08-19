package kr.co.kumoh.d134.composemvi.moviesearch.presentation

import kr.co.kumoh.d134.composemvi.base.MviState
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.MovieDetail
import kr.co.kumoh.d134.composemvi.util.emptyString

data class MoviesState(
    val query: String = emptyString(),
    val movies: List<Movie> = listOf(),
    val error: Throwable? = null,
    val isLoading: Boolean = false,
    val detail: MovieDetail? = null,
    val searchHistory: List<String> = emptyList(),
    val skipSplash: Boolean = false
) : MviState {
    companion object {
        fun initialState(): MoviesState = MoviesState() // MoviesSate 초기화, 명확한 의미 전달을 위해 Companion object로 표현
    }
}

fun MoviesState.isIdleState(): Boolean =  // data class여도 확장함수 가능
    query.isBlank() && movies.isEmpty() && error == null && !isLoading && detail == null

fun MoviesState.isDetailState(): Boolean =
    detail != null

fun MoviesState.isLoading(): Boolean = isLoading

fun MoviesState.resetDetailState(): MoviesState = copy(detail = null) // 객체 복사 후 새로운 인스턴스 반환, copy는 data class의 내장 함수