package kr.co.kumoh.d134.composemvi.moviesearch.presentation

sealed class MovieIntent {
    object InitaialIntent : MovieIntent()

    data class SearchIntent(val query: String) : MovieIntent()

    data class ClickIntent(val imdbId: String) : MovieIntent()

    object ClearClickIntent : MovieIntent()
}
