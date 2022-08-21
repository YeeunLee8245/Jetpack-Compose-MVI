package kr.co.kumoh.d134.composemvi.moviesearch.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    val gson by lazy {
        Gson()
    }

    @TypeConverter
    fun fromListOfStringToString(list: List<String>): String {  // [가,나,다,라,마] => 가|나|다|라|마
        return list.joinToString(separator = "|")
    }

    @TypeConverter
    fun fromStringToListString(str: String): List<String> { // 가|나|다|라|마 => [가,나,다,라,마]
        return str.split("|")
    }

    @TypeConverter
    fun fromListOfRatingToString(list: List<MovieEntity.Detail.Rating>): String { // data class 값 => {키: 값, ..}
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringToListOfRating(str: String): List<MovieEntity.Detail.Rating> { // {키: 값, ..} => [Movie~객체]
        val listType = object :
            TypeToken<List<MovieEntity.Detail.Rating>>() {}.type // list와 같은 제네릭 타입은 컴파일 후 타입 소거가 돼서 원래 타입 알 수 없게 되어버리기 때문에
        // ~ gson의 TypeToken을 사용해 컴파일 후 List<Object>가 아닌 List<지정한 타입>의 형태로 만들어야한다는 것을 알린다.
        // ~ 익명클래스 형태로 상속을 받게 한 후 타입을 보존시키는 편법(ㅋㅋ)을 이용
        // ~.type() 메서드로 List<Movie~> 데이터 타입을 얻어옴
        // ~TypeToken를 익명 클래스(object)로 상속받은 후 {}으로 구현하고 바로 객체 생성한 것
        return gson.fromJson(str, listType)
    }
}