package kr.co.kumoh.d134.composemvi.moviesearch.presentation

import kr.co.kumoh.d134.composemvi.base.MviAction

sealed class MovieAction : MviAction{   // action 상태 표현
    object InitAction : MovieAction()   // 초기화는 싱글톤으로

    data class SearchAction(val query: String) : MovieAction()

    data class DetailAction(val imdbId: String) : MovieAction()

    object ClearDetailAction : MovieAction()    // object: 싱글톤 패턴으로 사용할 때(메모리에 한번 올라간 뒤 재사용)

    data class SaveSearchHistory(val searchHistory: List<String>) : MovieAction()
}
