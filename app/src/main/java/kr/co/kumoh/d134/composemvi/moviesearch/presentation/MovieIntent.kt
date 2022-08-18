package kr.co.kumoh.d134.composemvi.moviesearch.presentation

import kr.co.kumoh.d134.composemvi.base.MviIntent

sealed class MovieIntent : MviIntent {
    object InitaialIntent : MovieIntent()

    data class SearchIntent(val query: String) : MovieIntent()

    data class ClickIntent(val imdbId: String) : MovieIntent()

    object ClearClickIntent : MovieIntent()
}
