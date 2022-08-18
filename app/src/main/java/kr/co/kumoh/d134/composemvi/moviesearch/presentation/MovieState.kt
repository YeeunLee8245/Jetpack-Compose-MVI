package kr.co.kumoh.d134.composemvi.moviesearch.presentation

import kr.co.kumoh.d134.composemvi.moviesearch.base.MviState
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.MovieDetail
import kr.co.kumoh.d134.composemvi.util.emptyString
import java.lang.Exception

data class MovieState(
    val query: String = emptyString(),
    val movies: List<Movie> = listOf(),
    val error: Exception? = null,
    val isLoading: Boolean = false,
    val detail: MovieDetail? = null
) : MviState

fun MovieState.isIdleState() =  // data class여도 확장함수 가능
    query.isBlank() && movies.isEmpty() && error == null && !isLoading && detail == null

fun MovieState.isDetailState() =
    detail != null

fun MovieState.resetDetailState(): MovieState = copy(detail = null) // 객체 복사 후 새로운 인스턴스 반환, copy는 data class의 내장 함수