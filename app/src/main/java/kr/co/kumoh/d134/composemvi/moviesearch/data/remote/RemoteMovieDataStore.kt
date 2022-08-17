package kr.co.kumoh.d134.composemvi.moviesearch.data.remote

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kr.co.kumoh.d134.composemvi.moviesearch.data.MovieDataStore
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.Movie
import kr.co.kumoh.d134.composemvi.moviesearch.data.model.MovieDetail

class RemoteMovieDataStore(private val movieApi: MovieApi) : MovieDataStore {
    override fun getMoviesStream(searchQuery: String): Flowable<List<Movie>> {
        return Flowable.error(UnsupportedOperationException("Can't get stream from Remote"))
    }

    override fun getMovies(searchQuery: String): Single<List<Movie>> {
        return movieApi.searchMovies(searchQuery)
            .map { movieResponse -> // 데이터 방출 시, single의 데이터에 적용할 함수
                movieResponse.movies
                    .map { movie -> // List<Movie>를 통해 Movie 객체들 생성 => map으로 각 Movie 타입에 넣어줌
                        Movie(  // SerializedName가 없는 일반 data class로 바꿈
                            imdbID = movie.imdbID,
                            poster = movie.poster,
                            title = movie.title,
                            type = movie.type,
                            year = movie.year
                        )
                    }
            }
    }

    override fun addMovies(movieList: List<Movie>): Completable {   // LocalMovieDataStore에서만 구현
        return Completable.error(UnsupportedOperationException("Can't add to Remote"))
    }

    override fun getMovieDetail(imdbId: String): Single<MovieDetail> {
        return movieApi.getMovieDetail(imdbId)
            .map {
                MovieDetail(
                    actors = it.actors.split(
                        ", ",
                        ignoreCase = true
                    ), // Jesse Eisenberg, Rooney Mara, Bryan Barter, Dustin Fitzsimons
                    awards = it.awards, // Won 3 Oscars. Another 171 wins & 183 nominations.
                    boxOffice = it.boxOffice, // $96,400,000
                    country = it.country, // USA
                    dVD = it.dVD, // 11 Jan 2011
                    director = it.director, // David Fincher
                    genre = it.genre, // Biography, Drama
                    imdbID = it.imdbID, // tt1285016
                    imdbRating = it.imdbRating, // 7.7
                    imdbVotes = it.imdbVotes, // 590,040
                    language = it.language, // English, French
                    metascore = it.metascore, // 95
                    plot = it.plot, // As Harvard student Mark Zuckerberg creates the social networking site that would become known as Facebook, he is sued by the twins who claimed he stole their idea, and by the co-founder who was later squeezed out of the business.
                    poster = it.poster, // https://m.media-amazon.com/images/M/MV5BOGUyZDUxZjEtMmIzMC00MzlmLTg4MGItZWJmMzBhZjE0Mjc1XkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg
                    production = it.production, // Columbia Pictures
                    rated = it.rated, // PG-13
                    ratings = it.ratings.map {
                        MovieDetail.Rating(
                            source = it.source,
                            value = it.value
                        )
                    },
                    released = it.released, // 01 Oct 2010
                    response = it.response, // True
                    runtime = it.runtime, // 120 min
                    title = it.title, // The Social Network
                    type = it.type, // movie
                    website = it.website, // N/A
                    writer = it.writer, // Aaron Sorkin (screenplay), Ben Mezrich (book)
                    year = it.year // 2010
                )
            }
    }

    override fun addMovieDetail(movie: MovieDetail): Completable {  // LocalMovieDataStore에서만 구현
        return Completable.error(UnsupportedOperationException("Can't add to Remote"))
    }
}