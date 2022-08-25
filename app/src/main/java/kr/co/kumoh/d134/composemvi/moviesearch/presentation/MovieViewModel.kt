package kr.co.kumoh.d134.composemvi.moviesearch.presentation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableTransformer
import io.reactivex.rxjava3.functions.BiFunction
import kr.co.kumoh.d134.composemvi.base.presentation.BaseViewModel
import timber.log.Timber

class MovieViewModel @ViewModelInject constructor(
    override val actionProcessor: MovieProcessor
) :
    BaseViewModel<MovieIntent, MovieState, MovieAction, MovieResult>() {

    override fun intentFilter(): FlowableTransformer<MovieIntent, MovieIntent> {    // TODO: 1. 첫번째로 사용자의 Intent가 들어온다.
        return FlowableTransformer { intents ->
            intents.publish { shared ->
                Flowable.merge<MovieIntent>(
                    shared.ofType(MovieIntent.InitaialIntent::class.java).take(1),  // InitaialIntent 중에 가장 먼저 들어온 첫 InitaialIntent 아이템만
                    shared.filter{
                        it !is MovieIntent.InitaialIntent   // InitaialIntent 타입이 아닐 경우는 뒤에 붙는다.
                    }
                )
            }
        }
    }

    override fun actionFromIntent(intent: MovieIntent): MovieAction {   //  intent에서 발생된 action // TODO: 2. compose()로 인해, Intent로 Action이 발생한다.
        return when (intent) {
            is MovieIntent.InitaialIntent -> MovieAction.InitAction
            is MovieIntent.SearchIntent -> MovieAction.SearchAction(intent.query)   // 검색 action(검색하려는 query 포함)
            is MovieIntent.ClickIntent -> MovieAction.DetailAction(intent.imdbId)   // 특정 영화 클릭
            is MovieIntent.ClearClickIntent -> MovieAction.ClearDetailAction    //  상세정보 화면에서 없앰
            is MovieIntent.SaveSearchHistory -> MovieAction.SaveSearchHistory(intent.searchHistory)
        }
    }

    // TODO: 5. Reducer은 Intent가 Transformer를 이루고 Transformer가 reduce를 이룬 것을 처리하여 State를 만드는 메카니즘이다.
    override fun reducer(): BiFunction<MovieState, MovieResult, MovieState> =   // reducer는 observable에게 2번째 인자(MovieResult) 받음
        BiFunction { previousState, result ->   // 인자 2개를 받아 특정 결과를 반환하는 apply 메소드만 있는 함수형 인터페이스, previousState는 초기화 세팅된 state
            //~ 연산 코드를 간결하게 만들어준다
            Timber.d("reducer 작동: $previousState , $result")
            when (result) { // result는 is에 명시된 타입과 동일하기 때문에 해당 타입이 가지고있는 프로퍼티만 활용 가능
                is MovieResult.InitResult -> previousState.copy(searchHistory = result.searchHistory)   // 과거 기록 세팅
                is MovieResult.SearchResult.InProgress -> previousState.copy(   // 검색 결과 로딩 중
                    query = result.query,
                    isLoading = true
                )
                is MovieResult.SearchResult.Success -> previousState.copy(  // 검색 완료
                    query = result.query,
                    movies = result.movies,
                    error = null,
                    isLoading = false,
                    detail = null
                )
                is MovieResult.SearchResult.Failure -> previousState.copy(  // 검색 실패
                    query = result.query,
                    error = result.error,
                    isLoading = false
                )
                is MovieResult.LoadDetailResult.InProgress -> previousState.copy(   // 영화상세 정보 로딩 중
                    isLoading = true
                )
                is MovieResult.LoadDetailResult.Success -> previousState.copy(  // 영화상세 정보 성공
                    error = null,
                    isLoading = false,
                    detail = result.movieDetail
                )
                is MovieResult.LoadDetailResult.Failure -> previousState.copy(  // 영화상세 정보 오류
                    error = result.error,
                    isLoading = false
                )
                is MovieResult.ClearDetailResult -> previousState.copy( // 영화상세 초기화
                    detail = null
                )
                is MovieResult.SaveSearchResult -> previousState    // 그대로 저장
            }
        }

    override fun initialSate(): MovieState = MovieState.initialState()  // state 객체 새로 생성(초기화)
}