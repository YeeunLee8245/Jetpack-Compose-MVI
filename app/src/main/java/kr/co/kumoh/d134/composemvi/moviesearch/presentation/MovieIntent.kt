package kr.co.kumoh.d134.composemvi.moviesearch.presentation

import kr.co.kumoh.d134.composemvi.base.MviIntent

sealed class MovieIntent : MviIntent {  // action에서 전달받은 intent 상태
    object InitaialIntent : MovieIntent()

    data class SearchIntent(val query: String) : MovieIntent()

    data class ClickIntent(val imdbId: String) : MovieIntent()

    object ClearClickIntent : MovieIntent()

    data class SaveSearchHistory(val searchHistory: List<String>) : MovieIntent()
}
