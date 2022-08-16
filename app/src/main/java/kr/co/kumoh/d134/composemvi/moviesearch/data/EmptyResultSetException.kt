package kr.co.kumoh.d134.composemvi.moviesearch.data

class EmptyResultSetException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)

    // cause에 모든 예외 클래스 대입 가능, Throwable 클래스는 모든 예외 클래스의 부모
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(
        message: String?,
        cause: Throwable?,
        enableSuppression: Boolean, // TODO: 의미?
        writableStackTrace: Boolean // 쓰기 가능 여부 추적
    ) : super(message, cause, enableSuppression, writableStackTrace)


}