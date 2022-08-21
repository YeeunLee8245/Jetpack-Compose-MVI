package kr.co.kumoh.d134.composemvi.moviesearch.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import retrofit2.Converter

@Database(
    entities = [MovieEntity::class, SearchHistoryEntity::class],
    version = 2
) // class는 코틀린을 반환하는 api, class.java는 자바를 반환하는 api 객체 얻음, 각 언어의 요소 핸들링 가능
@TypeConverters(Converter::class)   // TODO: http에서 데이터 받아서 사용하기 때문
abstract class MovieDB : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    abstract fun searchDao(): SearchDao

    companion object {
        private var instance: MovieDB? = null

        @JvmStatic  // 속해있는 객체에 바로 get/set 함수 생
        @Synchronized   // 여러 스레드에 의해 메서드가 동시에 실행되지 않도록 보호, JVM 메서드를 동기화된 것으로 표시
        fun getInstance(applicationContext: Context): MovieDB {
            if (instance == null) {
                instance =
                    Room.databaseBuilder(applicationContext, MovieDB::class.java, "movie_search.db")
                        .fallbackToDestructiveMigration()
                        .build()
            }

            return instance!!
        }
    }
}