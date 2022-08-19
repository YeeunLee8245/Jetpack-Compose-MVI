package kr.co.kumoh.d134.composemvi.moviesearch.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface SearchDao {
    @Query("select * from searchHistory order by timeStamp desc")
    fun getSearchHistroy(): Single<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSearchHistory(searchHistory: List<SearchHistoryEntity>): Completable
}