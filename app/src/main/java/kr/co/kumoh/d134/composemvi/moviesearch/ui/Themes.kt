package kr.co.kumoh.d134.composemvi.moviesearch.ui

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MoviesTheme(colors: Colors = MaterialTheme.colors, content: @Composable () -> Unit) {
    // 1 param: 디폴트 테마 컬러로 디폴트 지정
    MaterialTheme(colors = colors, content = content)
}