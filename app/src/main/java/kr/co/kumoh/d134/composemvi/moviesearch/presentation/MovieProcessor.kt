package kr.co.kumoh.d134.composemvi.moviesearch.presentation

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableTransformer
import kr.co.kumoh.d134.composemvi.base.presentation.ISchedulerProvider
import kr.co.kumoh.d134.composemvi.base.presentation.MviActionProcessor
import kr.co.kumoh.d134.composemvi.moviesearch.data.IMovieRepository

open class MovieProcessor(
    private val repository: IMovieRepository,
    override val schedulerProvider: ISchedulerProvider  // MviActionProcessor에서 구현해줘야하기 때문
) : MviActionProcessor<MovieAction, MovieResult> {

    // 검색, 상세정보 확인, 상세정보 초기화, 검색 결과 초기화를 한 Stream으로 만듦
    override fun transformFromAction(): FlowableTransformer<MovieAction, MovieResult> =
        FlowableTransformer { actionFlowable -> // upstream
            actionFlowable.publish { shared ->  // publish를 이용하여 connectableObservable로 만들어준 후 connect()호출해야 구독 후 데이터 발행 가능
                Flowable.merge( // shared: publish가 적용된 Flowable 객체
                    shared.ofType(MovieAction.SearchAction::class.java).compose(searchMovies()),   // ofType(특정 타입만 필터),compose(앞선 Observable에 인자 Observable을 합쳐서 새 Observable을 만듦)
                    shared.ofType(MovieAction.DetailAction::class.java).compose(loadDetails()),
                    shared.ofType(MovieAction.ClearDetailAction::class.java)
                        .compose(clearDetails()),
                    shared.ofType(MovieAction.InitAction::class.java).compose(initUi())
                )
            }
        }

    private fun searchMovies(): FlowableTransformer<MovieAction.SearchAction, MovieResult.SearchResult> =
        FlowableTransformer { actionFlowable -> // 앞선 upstream에서 받은 데이터(위 함수에서 compose 앞 인자)
            actionFlowable.switchMap { action ->    // switchMap: 마지막으로 방출된 item(항목 = action)에만 연산 적용
                // ~ 가장 최근에 요청한 쿼리만 수행함(따라서 데이터로 응답받지 못한 데이터는 무시하게 됨)
                repository.getMovies(action.query)
                    .map {
                        MovieResult.SearchResult.Success(
                            movies = it,
                            query = action.query
                        )
                    }
                    .cast(MovieResult.SearchResult::class.java) // item 데이터 타입 모두 일치시킨
                    .onErrorReturn { error ->
                        MovieResult.SearchResult.Failure(
                            error = error,
                            query = action.query
                        )
                    }
                    .subscribeOn(schedulerProvider.io())    // 데이터 흐름 발생시키고 연산하는 스레드 지정
                    .observeOn(schedulerProvider.ui())    // Observable이 Observer에게 알림을 보내는 스레드 지정
                    .startWithItem( // 기존의 Observable 앞에 아이템을 emit하는 Observable 붙임
                        MovieResult.SearchResult.InProgress(action.query)
                    )
            }
        }

    private fun loadDetails(): FlowableTransformer<MovieAction.DetailAction, MovieResult.LoadDetailResult> =
        FlowableTransformer { actionFlowable ->
            actionFlowable.switchMap { action ->
                repository.getMovieDetail(action.imdbId)
                    .map {
                        MovieResult.LoadDetailResult.Success(
                            movieDetail = it,
                            imdbId = action.imdbId
                        )
                    }
                    .cast(MovieResult.LoadDetailResult::class.java) //  반환타입에 명시된 downstream 타입으로 cast
                    .onErrorReturn { error ->
                        MovieResult.LoadDetailResult.Failure(
                            error = error,
                            imdbId = action.imdbId
                        )
                    }
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .startWithItem(
                        MovieResult.LoadDetailResult.InProgress(action.imdbId)
                    )
            }
        }

    private fun clearDetails(): FlowableTransformer<MovieAction.ClearDetailAction, MovieResult.ClearDetailResult> =
        FlowableTransformer {
            it.switchMap {
                Flowable.just(MovieResult.ClearDetailResult)
            }
        }

    private fun saveSearchDetails(): FlowableTransformer<MovieAction.SaveSearchHistory, MovieResult.SaveSearchResult> =
        FlowableTransformer {
            it.switchMap { action ->
                repository.saveSearchResult(action.searchHistory)
                    .andThen(   // 스트림 연결(동기로)
                        Flowable.just(MovieResult.SaveSearchResult.Success)
                    )
                    .cast(MovieResult.SaveSearchResult::class.java)
                    .onErrorReturn { _ ->
                        MovieResult.SaveSearchResult.Error
                    }
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .startWithItem(
                        MovieResult.SaveSearchResult.Loading
                    )
            }
        }

    private fun initUi(): FlowableTransformer<MovieAction.InitAction, MovieResult.InitResult> =
        FlowableTransformer {
            it.switchMap {
                repository.getSearchHistory()
                    .toFlowable()
                    .map { searchHistory ->
                        MovieResult.InitResult(searchHistory)
                    }
                    .cast(MovieResult.InitResult::class.java)
                    .onErrorReturn { error ->
                        MovieResult.InitResult()
                    }
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .startWithItem(
                        MovieResult.InitResult()
                    )
            }
        }
}