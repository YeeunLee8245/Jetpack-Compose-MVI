# 첫 MVI 학습기

눈대중으로 보는 것보다 코드를 따라 치는 것이 눈, 손, 머리로 학습할 수 있을 것 같아 클론 코딩하며 MVI 패턴을 익혀보는 시간을 가졌다.<br/>

코드를 치기 전에 MVI에 대해서 이것 저것 봤지만 역시 백번 보는 것이 한 번 치는 것만 못하다. ~~(백견이 불어일타)~~<br><br>

### 무엇이 핵심일까?
MVI의 장점 중 하나는 단방향 데이터 플로우다.<br>
이 코드에서는 Intent ▶️ Action ▶️ Result ▶️ State ▶️ View 순으로 데이터를 전달한다.<br>예를 들어, 사용자가 앱 내에서 검색 기능을 수행하기 위해 검색어를 입력하고 검색 버튼을 눌렀다고 가정해보자.<br>
그렇다면 우선 사용자의 의도를 전달하기 위해 검색 기능 수행하라는 의도인 Intent가 생성되고 Intent의 타입을 바탕으로 Action이 생성된다. 마찬가지로 Action 타입을 바탕으로 State가 생성된다. 일련의 과정이 정상적으로 수행되었다면 최종적으로 State의 결과가 View에 반영된다. 여기에선 SideEffect를 다루지 않았지만 State에 의해 SideEffect가 발생되는 경우도 있다.<br>

Intent, Action, Result, State와 관련된 클래스 대부분은 Sealed Class이다. Sealed Class는 내부 상속 관계를 컴파일러도 알 수 있게 해주기 때문에 when으로 분기를 나눌 때 컴파일러가 이미 상속받는 하위 Class들을 알고있어서 오류를 예방할 수 있다. 또한 Sealed Class 내에 싱글톤으로 동작하게 만들고싶은 객체는 object로 선언해주면 되니 메모리를 절약할 수 있다.(상태 변수가 없다면 object를 사용하자!)<br>

데이터를 발생시키고 스케쥴링하기 위해 사용되는 RxJAVA는 Intent, Action, Result, State가 유기적으로 동작할 수 있도록 중간자 역할을 한다. 이 코드에서 다루는 RxJAVA의 Observable는 다음과 같이 크게 5가지가 있다.
* Observable
    * rx의 기본 단위
    * 데이터의 변화가 발생함
    * 데이터의 흐름에 맞게 Consumer에게 알림을 보내는 class
    * 사용: 지속적으로 발생하는 이벤트, 비동기 네트워크 요청, 리스트 스트림 처리
* Single
    * 지속적인 스트리밍 이벤트가 아닌, 데이터를 한 번 가져 오도록 호출하면 단일 결과만 예상될 때 사용
    * 의도를 명확히 하기 위해 Observable 대신 Single
* Flowable
    * 배압현상(Backpressure) 스스로 제어 가능
    * 배압현상: 발행과 소비가 불균형하게 일어날 때, Observable이 데이터를 발행하는 속도를 Observer의 소비속도가 따라가지 못하는 것 => overflow => OutOfMemoryError => 앱 터짐
    * 배압전략(MISSING, ERROR, BUFFER, DROP, LATEST) 명시 가능

* Completable
    * 데이터를 통지하지 않고 데이터 발행 완료/에러 신호만 보내는 특수한 형태
    * Completable 내에서 특정 작업 수행 후에, 해당 작업 끝난 것을 통지하는 목적으로 사용
* Maybe
    * 최대 데이터 하나 가질 수 있음 (Single과 같은 특성)
    * 데이터 발행 없이 바로 데이터 발생 완료, 발생 완료하지 않을 수도 있음

다양한 RxJAVA 함수들을 이용하여 Observable로부터 발행하고싶은 item들을 지정하고 알맞은 시점에 발행하도록 할 수 있다.<br>

### 후기
MVI 구조와 더불어 Sealed Class 적용과 RxJAVA에 대해서도 함께 공부할 수 있었던 시간이었다. <br>
~~MVI 구조를 익히는 것보다 이 코드에 적용된 RxJAVA를 익히는 것에 시간이 더 많이 걸렸~~<br>

지금 학교 사람들과 진행하고 있는 프로젝트에도 이 프로젝트 구조를 바탕으로 MVI를 적용해볼 계획이다. 다만, 비동기는 RxJAVA가 아닌 Coroutine을 사용하고싶어서 MVI에 Coroutine을 적용한 여러 코드들을 찾아보며 공부해야겠다.<br><br>