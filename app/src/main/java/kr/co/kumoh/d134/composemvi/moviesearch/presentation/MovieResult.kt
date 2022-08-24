package kr.co.kumoh.d134.composemvi.moviesearch.presentation

import kr.co.kumoh.d134.composemvi.base.MviResult
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.MovieDetail

sealed class MovieResult : MviResult {
    sealed class SearchResult : MovieResult() { // 검색 결과 상태일 때
        abstract val query: String  // SearchResult 상속시 반드시 구현

        data class Success(val movies: List<Movie>, override val query: String) : SearchResult()

        data class Failure(val error: Throwable, override val query: String) : SearchResult()

        data class InProgress(override val query: String) : SearchResult()  // 검색 로딩 중
    }

    sealed class LoadDetailResult : MovieResult() { // 검색해서 들어간 영화 상세 설명 출력 상태일 때
        abstract val imdbId: String

        data class Success(val movieDetail: MovieDetail, override val imdbId: String) :
            LoadDetailResult()

        data class Failure(val error: Throwable, override val imdbId: String) :
            LoadDetailResult()

        data class InProgress(override val imdbId: String) : LoadDetailResult() // 상세화면 로딩 중
    }

    object ClearDetailResult : MovieResult()    // TODO: 영화 상세 설명 초기화 상태일 때

    data class InitResult(val searchHistory: List<String> = emptyList()) : MovieResult()    // TODO: 검색 결과 초기화 상태일 때

    sealed class SaveSearchResult : MovieResult() { //  검색결과 저장 상태일 때
        object Loading : SaveSearchResult()
        object Success : SaveSearchResult()
        object Error : SaveSearchResult()
    }
}

