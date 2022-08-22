package kr.co.kumoh.d134.composemvi.moviesearch.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.LiveData
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transition.CrossfadeTransition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kr.co.kumoh.d134.composemvi.R
import kr.co.kumoh.d134.composemvi.SEARCH_HINT
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.MovieDetail
import kr.co.kumoh.d134.composemvi.moviesearch.presentation.MovieState
import kr.co.kumoh.d134.composemvi.moviesearch.presentation.isDetailState
import kr.co.kumoh.d134.composemvi.moviesearch.presentation.isIdleState
import kr.co.kumoh.d134.composemvi.moviesearch.presentation.isLoading
import timber.log.Timber

@Composable
fun MovieScreen(
    stateLiveData: LiveData<MovieState>,
    onSearch: (String) -> Unit, // TODO: onSearch 구현부 확인
    onMovieClick: (String) -> Unit
) {
    val isSplashPlaying = remember { mutableStateOf(true) }  // Composable 업데이트: 리컴포지션

    val movieState: MovieState by stateLiveData.observeAsState(MovieState.initialState())   // observeAsState: 주로 ViewModel에서 LiveData<T>를 관찰하고 데이터 변경 시,
    //~업데이트 되는 State<T> 객체를 반환(* State<T>는 Compose가 사용할 수 있는 관찰 가능 유형임!)
    //~observeAsState는 컴포지션에 있는 동안에만 LiveData를 관찰하는 걸 유의하자!, by를 사용했기 때문에 래핑이 해제 되어 State<MovieState>이 아닌 MovieState!

    if (isSplashPlaying.value && !movieState.skipSplash) {
        Splash(isSplashPlaying)
    } else {
        val movieDetail = movieState.detail
        val searchState = when {
            movieState.isDetailState() && movieDetail != null -> {  // 뒤에 한번 더 null이 아님을 이중으로 확인해주는 이유는 느낌표 생략하려고
                SearchState.Detail(movieDetail.title)   // 영화 상세정보 상태
            }
            !movieState.query.isBlank() -> {
                SearchState.SearchTyped(movieState.query)   // 타이핑 완료후 검색
            }
            else -> {
                SearchState.Icon
            }
        }

        Column {
            // Appbar
            Appbar(
                searchState = searchState,
                isIdle = movieState.isIdleState(),  // 활용 안 함
                onSearch = onSearch
            )

            val movies = movieState.movies

            Timber.d("State: $movieState")

            when {
                movieState.isLoading() -> { // 영화 로딩 상태
                    LoadingScreen()
                }
                movieState.isDetailState() && movieDetail != null -> {  // 영화 상세정보 상태
                    DetailScreen(movieDetail = movieDetail)
                }
                movies.isNotEmpty() -> {    //  검색 완료 성공 상태
                    ListScreen(movieList = movies, onMovieClick = onMovieClick)
                }
                movieState.error != null -> {   // 검색 결과 local에도, remote에도 없음
                    ErrorScreen(throwable = movieState.error!!)
                }
                else -> {   // 메인 화면, history가 있을 땐 텍스트 하단에 hitory 띄움
                    IdleScreen(searchHistory = movieState.searchHistory, onSearch = onSearch)
                }
            }

        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IdleScreen(searchHistory: List<String>, onSearch: (String) -> Unit = {}) {
    Column {
        Row {
            Text("Tap on Search button to Search for Movies")
        }

        if (searchHistory.isNotEmpty()) {   // history가 있으면
            Row(Modifier.padding(top = 5.dp)) {
                Text("Below are your past searches")
            }

            LazyVerticalGrid(cells = GridCells.Fixed(3)) {  // 3열
                items(searchHistory) { item ->
                    Column(modifier = Modifier.padding(5.dp)) {
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .background(color = Color.Red, shape = RoundedCornerShape(5.dp))
                                .padding(5.dp)
                                .clickable {
                                    onSearch(item)
                                }
                        ) {
                            Text(
                                text = item,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(throwable: Throwable) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Error Showing Movies :: ${throwable.localizedMessage}")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListScreen(movieList: List<Movie>, onMovieClick: (String) -> Unit) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        modifier = Modifier.semantics { testTag = "movieList" }
    ) {
        items(movieList) { movie ->
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable(onClick = {
                        onMovieClick(movie.imdbID)  // 특정 영화 클릭
                    })
            ) {
                MovieItemCard(
                    movie = movie
                )
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun MovieItemCard(modifier: Modifier = Modifier, movie: Movie) {    // 검색 결과 리스트에서 보여지는 영화 아이템
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 7.dp,
        modifier = modifier
            .wrapContentHeight(align = Alignment.CenterVertically)
            .fillMaxWidth()
            .shadow(2.dp)
    ) {
        ConstraintLayout {
            val logo = createRef()
            val itemTitle = createRef()
            val itemType = createRef()
            val itemYear = createRef()
            val itemImdb = createRef()


            Image(
                painter = rememberImagePainter(
                    data = movie.poster,
                    builder = {
                        transition(CrossfadeTransition())
                        placeholder(R.drawable.cinema)
                    }
                ),
                contentDescription = movie.title + " poster",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .constrainAs(logo) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
            )

            Text(
                text = movie.title,
                color = Color.Blue,
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(5.dp)
                    .constrainAs(itemTitle) {
                        start.linkTo(parent.start)
                        top.linkTo(logo.bottom)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            )
            Text(
                text = "Type: ${movie.type.capitalize()}",
                style = TextStyle.Default.copy(fontSize = 10.sp),
                modifier = Modifier
                    .padding(2.dp)
                    .constrainAs(itemType) {
                        start.linkTo(logo.end)
                        top.linkTo(itemImdb.bottom)
                        bottom.linkTo(itemYear.top)
                    }
            )
            Text(
                text = "Year: ${movie.year}",
                style = TextStyle.Default.copy(fontSize = 10.sp),
                modifier = Modifier
                    .padding(2.dp)
                    .constrainAs(itemYear) {
                        start.linkTo(logo.end)
                        top.linkTo(itemType.bottom)
                        bottom.linkTo(itemTitle.top)
                    }
            )
            Text(
                text = "IMDB: ${movie.imdbID}",
                style = TextStyle.Default.copy(fontSize = 10.sp),
                modifier = Modifier
                    .padding(2.dp)
                    .constrainAs(itemImdb) {
                        start.linkTo(logo.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(itemType.top)
                    }
            )
        }
    }
}


@OptIn(ExperimentalComposeApi::class)   // transition, CrpssfadeTramsition api 사용 가능
@Composable
fun DetailScreen(movieDetail: MovieDetail) {
    ConstraintLayout {
        val logo = createRef()
        val itemTitle = createRef()
        val itemType = createRef()
        val itemYear = createRef()
        val itemImdb = createRef()
        val itemPlot = createRef()

        Image(
            painter = rememberImagePainter(
                data = movieDetail.poster,
                builder = {
                    transition(CrossfadeTransition())   // 현재 drawable에서 새 drawable로 전환 (cross fade 사용: 오버랩 되면서 바뀜)
                    placeholder(R.drawable.cinema)
                }
            ),
            contentDescription = movieDetail.title + " poster", // 접근성을 위해 사용(사용자가 사용X, 사용자의 의미있는 작업X)
            modifier = Modifier
                .constrainAs(logo) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
                .heightIn(max = 500.dp)
        )

        Text(
            text = movieDetail.title,
            color = Color.Blue,
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .constrainAs(itemTitle) {
                    start.linkTo(parent.start)
                    top.linkTo(logo.bottom)
                }
                .padding(8.dp)
        )
        Text(
            text = "Type: ${movieDetail.type.capitalize()}",
            style = TextStyle.Default.copy(fontSize = 15.sp),
            modifier = Modifier
                .constrainAs(itemType) {
                    start.linkTo(parent.start)
                    top.linkTo(itemImdb.bottom)
                }
                .padding(5.dp)
        )
        Text(
            text = "Year: ${movieDetail.year}",
            style = TextStyle.Default.copy(fontSize = 15.sp),
            modifier = Modifier
                .constrainAs(itemYear) {
                    start.linkTo(parent.start)
                    top.linkTo(itemType.bottom)
                }
                .padding(5.dp)
        )
        Text(
            text = "IMDB: ${movieDetail.imdbID}",
            style = TextStyle.Default.copy(fontSize = 15.sp),
            modifier = Modifier
                .constrainAs(itemImdb) {
                    start.linkTo(parent.start)
                    top.linkTo(itemPlot.bottom)
                }
                .padding(5.dp)
        )
        Text(
            text = "Plot: ${movieDetail.plot}",
            style = TextStyle.Default.copy(fontSize = 15.sp),
            modifier = Modifier
                .constrainAs(itemPlot) {
                    start.linkTo(parent.start)
                    top.linkTo(itemTitle.bottom)
                }
                .padding(5.dp)
        )
    }
}

@Composable
fun LoadingScreen() {
    CircularProgressIndicator(
        Modifier
            .fillMaxSize()
            .semantics { testTag = "progressbar" }
    )
}

const val APPBAR_SEARCH_ICON_TAG = "searchIcon"

@Composable
fun Appbar(
    searchState: SearchState,
    isIdle: Boolean = false,
    onSearch: (String) -> Unit
) {
    val isSearchbarVisible = remember { mutableStateOf(false) } // 검색창 보이는 유무

    TopAppBar(modifier = Modifier.semantics { testTag = "appbar" }) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val title = createRef()
            val searchIcon = createRef()

            Text(text = searchState.titlebarText, modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })

            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                modifier = Modifier
                    .constrainAs(searchIcon) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                    .clickable(onClick = {  //   view의 setONClickListener와 같은 작용
                        isSearchbarVisible.value = true
                    })
                    .semantics { testTag = APPBAR_SEARCH_ICON_TAG }
            )
        }
    }
    if (isSearchbarVisible.value) {
        SearchScreen(hint = "Search Movies", onSearch = {
            onSearch(it)
            isSearchbarVisible.value = false    // 검색 수행되면 검색창 없앰
        })
    }
}

@OptIn(ExperimentalComposeApi::class)   // api가 실험적 상태이고 변경 가능성이 있는 경우 API에 알림, 해당 api를 함수 안에서 사용 가능하게 함
@Composable
fun SearchScreen(hint: String, onSearch: (String) -> Unit) {
    val typedText = remember { mutableStateOf(TextFieldValue("")) }  // 텍스트 변경 감지

    Column {
        Row(modifier = Modifier.padding(5.dp)) {
            Text(text = "Enter Movie Name to Search")
        }
        Row {
            TextField(  // EditText View와 같은 역할
                value = typedText.value,
                onValueChange = { newTextValue: TextFieldValue ->
                    typedText.value = newTextValue
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { testTag = "searchBar" },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search    // 검색용(기능X, 단지 키보드에 확인 표기만 바뀜)
                ),
                placeholder = { // placeholder: 미리 데이터를 입력해두기 위해 사용(힌트 입력)
                    Text(text = hint)
                },
                keyboardActions = KeyboardActions {
                    onSearch(typedText.value.text)  // Search 작업 트리거 시(키보드 검색 확인 버튼 클릭 시), 실행
                }
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp)) {
            Column {
                Button(modifier = Modifier.padding(5.dp), onClick = {
                    onSearch(typedText.value.text)
                }) {
                    Text(
                        text = "Search",
                        modifier = Modifier.semantics { testTag = "searchButton" }
                    )
                }
            }
            Column {
                Button(modifier = Modifier.padding(5.dp), onClick = {
                    typedText.value = TextFieldValue("")
                }) {
                    Text(text = "Clear")
                }
            }
        }
    }
}

@Composable
fun Splash(
    isPlaying: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_animation_jetpack))
    val progress by animateLottieCompositionAsState(composition = composition)  //  조건 없이 lottie를 재생시키기만 할 것, 조건이 붙는다면 LottieAnimatable 활용 필요

    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .semantics { testTag = "splash" }) { // 시멘틱: UI 요소에 의미부여 역할. UI 계층 구조 형v
        val lottie = createRef()    // constratingLayout 내에 할당됨
        val credit = createRef()

        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier
                .constrainAs(lottie) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(100.dp)   // content 크기
        )

        Text(
            text = "Lottie from: https://lottiefiles.com/29328-android-jetpack",
            fontSize = 8.sp,
            modifier = Modifier.constrainAs(credit) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(lottie.bottom)
                bottom.linkTo(parent.bottom)
            }
        )
    }

    isPlaying.value =
        progress < 1f // (애니메이션 진행정도(1f=100%))값 바뀌면 리컴포지션(remember 객체가 정의된 컴포저블에서) => 진행완료시 false
}

sealed class SearchState(val titlebarText: String) {    // 인자값 X, data class 성격 띄고있음
    object Icon : SearchState(SEARCH_HINT)
    data class Typing(val typedText: String = "Typing...") : SearchState(typedText) // 타이핑 중
    data class SearchTyped(val typedText: String) : SearchState(typedText)  // TODO:
    data class Detail(val movieTitle: String) : SearchState(movieTitle) // TODO:
}