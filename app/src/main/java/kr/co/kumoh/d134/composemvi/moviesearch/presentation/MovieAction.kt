package kr.co.kumoh.d134.composemvi.moviesearch.presentation

sealed class MovieAction {
    data class SearchAction(val query: String) : MovieAction()

    data class DetailAction(val imdbId: String) : MovieAction()

    object ClearDetailAction : MovieAction()    // object: 싱글톤 패턴으로 사용할 때(메모리에 한번 올라간 뒤 재사용)
}
