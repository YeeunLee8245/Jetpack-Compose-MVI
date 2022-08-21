package kr.co.kumoh.d134.composemvi.moviesearch.data.remote

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import kr.co.kumoh.d134.composemvi.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import timber.log.Timber
import java.util.concurrent.TimeUnit

object MovieApiFactory {
    fun makeMovieApi(): MovieApi {
        val okHttpClient = makeOkHttpClient(
            makeLoggingInterceptor()
        )
        return makeMovieApi(okHttpClient, makeGson())
    }

    private fun makeMovieApi(okHttpClient: OkHttpClient, gson: Gson): MovieApi {
        return makeRetrofit(okHttpClient, gson).create(MovieApi::class.java)
    }

    private fun makeRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())  // RxJava를 사용하기 위해
            .addConverterFactory(GsonConverterFactory.create(gson)) // TODO: Jackson gson으로 바꿔서도 해보자
            .build()
    }

    private fun makeOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)  // connect timeout
            .readTimeout(120, TimeUnit.SECONDS) // server timeout
            .build()
    }

    private fun makeGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)   // 필드 이름 변경되지 않음
            .create()
    }

    private fun makeLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor(httpLogger) // 디버깅을 위한 것
        loggingInterceptor.level =
            HttpLoggingInterceptor.Level.BODY   // 통신 기록 레벨 설정
        return loggingInterceptor
    }

    private val httpLogger: HttpLoggingInterceptor.Logger by lazy {
        object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.d("HTTP::TrendingService:: $message")
            }

        }
    }
}